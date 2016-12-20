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

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.dozer.Mapper;
import org.jasig.portlet.notice.rest.AddresseeDTO;
import org.jasig.portlet.notice.rest.EntryDTO;
import org.jasig.portlet.notice.rest.EventDTO;
import org.jasig.portlet.notice.rest.RecipientDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Josh Helmer, jhelmer.unicon.net
 * @since 3.0
 */
@Service
public class NotificationDTOMapper implements INotificationDTOMapper {
    @Autowired
    private Mapper dozerBeanMapper;


    @Override
    public List<EntryDTO> toEntryList(List<JpaEntry> entries) {
        return Lists.transform(entries, new Function<JpaEntry, EntryDTO>() {
            @Override
            public EntryDTO apply(JpaEntry jpaEntry) {
                return toEntry(jpaEntry);
            }
        });
    }


    @Override
    public List<JpaEntry> toJpaEntryList(List<EntryDTO> entries) {
        return Lists.transform(entries, new Function<EntryDTO, JpaEntry>() {
            @Override
            public JpaEntry apply(EntryDTO entry) {
                return toJpaEntry(entry);
            }
        });
    }


    @Override
    public EntryDTO toEntry(JpaEntry entry) {
        return dozerBeanMapper.map(entry, EntryDTO.class);
    }


    @Override
    public JpaEntry toJpaEntry(EntryDTO entry) {
        return dozerBeanMapper.map(entry, JpaEntry.class);
    }


    @Override
    public Set<AddresseeDTO> toAddresseeSet(Set<JpaAddressee> addressees) {
        Set<AddresseeDTO> set = new HashSet<>();
        for (JpaAddressee addr : addressees) {
            AddresseeDTO dto = toAddressee(addr);
            set.add(dto);
        }

        return set;
    }


    @Override
    public AddresseeDTO toAddressee(JpaAddressee addressee) {
        return dozerBeanMapper.map(addressee, AddresseeDTO.class);
    }


    @Override
    public JpaAddressee toJpaAddressee(AddresseeDTO addressee) {
        return dozerBeanMapper.map(addressee, JpaAddressee.class);
    }


    @Override
    public List<RecipientDTO> toRecipientList(List<JpaRecipient> recipients) {
        return Lists.transform(recipients, new Function<JpaRecipient, RecipientDTO>() {
            @Override
            public RecipientDTO apply(JpaRecipient jpaRecipient) {
                return toRecipient(jpaRecipient);
            }
        });
    }


    @Override
    public RecipientDTO toRecipient(JpaRecipient recipient) {
        return dozerBeanMapper.map(recipient, RecipientDTO.class);
    }


    @Override
    public List<EventDTO> toEventList(List<JpaEvent> events) {
        return Lists.transform(events, new Function<JpaEvent, EventDTO>() {
            @Override
            public EventDTO apply(JpaEvent jpaEvent) {
                return toEvent(jpaEvent);
            }
        });
    }


    @Override
    public List<JpaEvent> toJpaEventList(List<EventDTO> events) {
        return Lists.transform(events, new Function<EventDTO, JpaEvent>() {
            @Override
            public JpaEvent apply(EventDTO eventDTO) {
                return toJpaEvent(eventDTO);
            }
        });
    }


    @Override
    public EventDTO toEvent(JpaEvent event) {
        return dozerBeanMapper.map(event, EventDTO.class);
    }


    @Override
    public JpaEvent toJpaEvent(EventDTO event) {
        return dozerBeanMapper.map(event, JpaEvent.class);
    }
}
