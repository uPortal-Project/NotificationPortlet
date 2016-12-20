/**
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.portlet.notice.service.jpa;

import org.apache.commons.lang3.Validate;
import org.jasig.portlet.notice.rest.AddresseeDTO;
import org.jasig.portlet.notice.rest.EntryDTO;
import org.jasig.portlet.notice.rest.EventDTO;
import org.jasig.portlet.notice.rest.RecipientDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * @author Josh Helmer, jhelmer.unicon.net
 * @since 3.0
 */
@Service
public class JpaNotificationRESTService implements IJpaNotificationRESTService {
    @Autowired
    private INotificationDao notificationDao;
    @Autowired
    private INotificationDTOMapper notificationMapper;


    @Override
    @Transactional(readOnly = true)
    public List<EntryDTO> getNotifications(Integer page, Integer pageSize) {
        List<JpaEntry> entries = notificationDao.list(page, pageSize);

        return notificationMapper.toEntryList(entries);
    }


    @Override
    @Transactional(readOnly = true)
    public EntryDTO getNotification(long id, boolean full) {
        JpaEntry entry = full ? notificationDao.getFullEntry(id) : notificationDao.getEntry(id);
        if (entry == null) {
            return null;
        }

        return notificationMapper.toEntry(entry);
    }


    @Override
    @Transactional
    public EntryDTO createNotification(EntryDTO entry) {
        Validate.isTrue(entry.getId() == 0, "Do not include an 'id' attribute when creating entries");

        JpaEntry jpaEntry = notificationMapper.toJpaEntry(entry);

        JpaEntry inserted = notificationDao.createOrUpdateEntry(jpaEntry);

        return notificationMapper.toEntry(inserted);
    }


    @Override
    @Transactional(readOnly = true)
    public Set<AddresseeDTO> getAddressees(long notificationId) {
        JpaEntry jpaEntry = notificationDao.getFullEntry(notificationId);
        if (jpaEntry == null) {
            return null;
        }

        return notificationMapper.toAddresseeSet(jpaEntry.getAddressees());
    }


    @Override
    @Transactional(readOnly = true)
    public AddresseeDTO getAddressee(long addresseeId) {
        JpaAddressee addr = notificationDao.getAddressee(addresseeId);
        if (addr == null) {
            return null;
        }

        return notificationMapper.toAddressee(addr);
    }


    @Override
    @Transactional
    public AddresseeDTO createAddressee(long notificationId, AddresseeDTO addressee) {
        Validate.isTrue(addressee.getId() == 0, "Do not include an 'id' attribute when creating addressees");
        for (RecipientDTO r : addressee.getRecipients()) {
            Validate.isTrue(r.getId() == 0, "Do not include an 'id' attribute on recipients when creating addressees");
        }

        JpaEntry entry = notificationDao.getEntry(notificationId);
        if (entry == null) {
            return null;
        }

        JpaAddressee jpa = notificationMapper.toJpaAddressee(addressee);
        entry.addAddressee(jpa);

        JpaAddressee persisted = notificationDao.createOrUpdateAddressee(jpa);
        return notificationMapper.toAddressee(persisted);
    }


    @Override
    @Transactional(readOnly = true)
    public List<EventDTO> getEventsByNotification(long notificationId) {
        List<JpaEvent> events = notificationDao.getEvents(notificationId);

        return notificationMapper.toEventList(events);
    }


    @Override
    @Transactional(readOnly = true)
    public EventDTO getEvent(long eventId) {
        JpaEvent event = notificationDao.getEvent(eventId);
        if (event == null) {
            return null;
        }

        return notificationMapper.toEvent(event);
    }


    @Override
    @Transactional
    public EventDTO createEvent(long notificationId, EventDTO event) {
        Validate.isTrue(event.getId() == 0, "Do not include an 'id' attribute when creating events");

        JpaEntry entry = notificationDao.getEntry(notificationId);
        JpaEvent jpa = notificationMapper.toJpaEvent(event);
        jpa.setEntry(entry);

        JpaEvent jpaResult = notificationDao.createOrUpdateEvent(jpa);
        return notificationMapper.toEvent(jpaResult);
    }
}
