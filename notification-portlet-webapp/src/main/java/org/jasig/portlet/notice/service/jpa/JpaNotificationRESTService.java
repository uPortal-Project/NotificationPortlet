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
import org.jasig.portlet.notice.NotificationEntry;
import org.jasig.portlet.notice.rest.AddresseeDTO;
import org.jasig.portlet.notice.rest.EntryDTO;
import org.jasig.portlet.notice.rest.EventDTO;
import org.jasig.portlet.notice.rest.RecipientDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * @author Josh Helmer, jhelmer.unicon.net
 * @since 3.0
 */
@Service("jpaNotificationRestService")
public class JpaNotificationRESTService implements IJpaNotificationRESTService {

    @Autowired
    private INotificationDao notificationDao;

    @Autowired
    private JpaNotificationService jpaNotificationService;

    @Autowired
    private INotificationDTOMapper notificationMapper;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    @Transactional(readOnly = true)
    public EntryDTO getNotification(long id, boolean full) {
        final JpaEntry entry = full ? notificationDao.getFullEntry(id) : notificationDao.getEntry(id);
        if (entry == null) {
            return null;
        }

        return notificationMapper.toEntry(entry);
    }

    @Override
    @Transactional(readOnly = true)
    public EntryDTO getNotification(NotificationEntry entry, boolean full) {
        EntryDTO rslt = null;  // default
        if (jpaNotificationService.contains(entry)) {
            final String idString = entry.getId().substring(JpaNotificationService.ID_PREFIX.length());
            final long id = Long.parseLong(idString);
            rslt = getNotification(id, full);
        }
        return rslt;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EntryDTO> getNotifications(Integer page, Integer pageSize) {
        final List<JpaEntry> entries = notificationDao.list(page, pageSize);

        return notificationMapper.toEntryList(entries);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EntryDTO> getNotificationsBySourceAndCustomAttribute(String source, String attributeName, String attributeValue) {
        Validate.notBlank(source, "Argument 'source' cannot be blank");
        Validate.notBlank(attributeName, "Argument 'attributeName' cannot be blank");
        // Does it make sense to allow blank attributeValue?

        logger.debug("Invoking getNotificationsBySourceAndCustomAttribute with "
                + "source='{}', attributeName='{}', attributeValue='{}'",
                source, attributeName, attributeValue);

        final List<JpaEntry> entries = notificationDao.getNotificationsBySourceAndCustomAttribute(source, attributeName, attributeValue);
        logger.debug("Found the following {} entries:  {}", entries.size(), entries);

        return notificationMapper.toEntryList(entries);
    }

    @Override
    @Transactional
    public EntryDTO createNotification(EntryDTO entry) {
        Validate.isTrue(entry.getId() == 0, "Do not include an 'id' attribute when creating entries");

        final JpaEntry jpaEntry = notificationMapper.toJpaEntry(entry);
        logger.debug("notificationMapper produced the following JpaEntry:  {}", jpaEntry);

        final JpaEntry inserted = notificationDao.createOrUpdateEntry(jpaEntry);

        return notificationMapper.toEntry(inserted);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<AddresseeDTO> getAddressees(long notificationId) {
        final JpaEntry jpaEntry = notificationDao.getFullEntry(notificationId);
        if (jpaEntry == null) {
            return null;
        }

        return notificationMapper.toAddresseeSet(jpaEntry.getAddressees());
    }

    @Override
    @Transactional(readOnly = true)
    public AddresseeDTO getAddressee(long addresseeId) {
        final JpaAddressee addr = notificationDao.getAddressee(addresseeId);
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

        final JpaEntry entry = notificationDao.getEntry(notificationId);
        if (entry == null) {
            return null;
        }

        final JpaAddressee jpa = notificationMapper.toJpaAddressee(addressee);
        entry.addAddressee(jpa);

        final JpaAddressee persisted = notificationDao.createOrUpdateAddressee(jpa);
        return notificationMapper.toAddressee(persisted);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventDTO> getEventsByNotification(long notificationId) {
        final List<JpaEvent> events = notificationDao.getEvents(notificationId);

        return notificationMapper.toEventList(events);
    }

    /**
     * Provides a complete transaction log for a notification and a single
     * recipient <strong>in chronological order</strong>.
     */
    @Override
    @Transactional(readOnly = true)
    public List<EventDTO> getEventsByNotificationAndUser(long notificationId, String username) {
        final List<JpaEvent> events = notificationDao.getEvents(notificationId, username);

        return notificationMapper.toEventList(events);
    }

    @Override
    @Transactional(readOnly = true)
    public EventDTO getEvent(long eventId) {
        final JpaEvent event = notificationDao.getEvent(eventId);
        if (event == null) {
            return null;
        }

        return notificationMapper.toEvent(event);
    }

    @Override
    @Transactional
    public EventDTO createEvent(long notificationId, EventDTO event) {
        Validate.isTrue(event.getId() == 0, "Do not include an 'id' attribute when creating events");

        final JpaEntry entry = notificationDao.getEntry(notificationId);
        final JpaEvent jpa = notificationMapper.toJpaEvent(event);
        jpa.setEntry(entry);

        final JpaEvent jpaResult = notificationDao.createOrUpdateEvent(jpa);
        return notificationMapper.toEvent(jpaResult);
    }

}
