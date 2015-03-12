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
