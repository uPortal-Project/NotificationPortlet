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

import org.jasig.portlet.notice.rest.AddresseeDTO;
import org.jasig.portlet.notice.rest.EntryDTO;
import org.jasig.portlet.notice.rest.EventDTO;
import org.jasig.portlet.notice.rest.RecipientDTO;

import java.util.List;
import java.util.Set;

/**
 * Conversion util.   Class takes care of mapping entities
 * from JPA to DTO entities.
 *
 * @author Josh Helmer, jhelmer.unicon.net
 * @since 3.0
 */
public interface INotificationDTOMapper {
    List<EntryDTO> toEntryList(List<JpaEntry> entries);
    List<JpaEntry> toJpaEntryList(List<EntryDTO> entries);

    EntryDTO toEntry(JpaEntry entry);
    JpaEntry toJpaEntry(EntryDTO entry);

    Set<AddresseeDTO> toAddresseeSet(Set<JpaAddressee> addressees);
    AddresseeDTO toAddressee(JpaAddressee addressee);
    JpaAddressee toJpaAddressee(AddresseeDTO addressee);

    List<RecipientDTO> toRecipientList(List<JpaRecipient> recipients);
    RecipientDTO toRecipient(JpaRecipient recipient);

    List<EventDTO> toEventList(List<JpaEvent> events);
    List<JpaEvent> toJpaEventList(List<EventDTO> events);
    EventDTO toEvent(JpaEvent event);
    JpaEvent toJpaEvent(EventDTO event);
}
