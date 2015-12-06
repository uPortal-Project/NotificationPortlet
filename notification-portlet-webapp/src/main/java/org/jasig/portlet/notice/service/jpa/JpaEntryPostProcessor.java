package org.jasig.portlet.notice.service.jpa;

import org.jasig.portlet.notice.rest.EntryDTO;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Mapping to fix the attribute/entry relationship after the attributes have been converted.
 * Since the DTO objects don't have the link back to the entry, this doesn't get set.  Could
 * make dozer do the addAttribute() call instead of setting the list directly, but that
 * requires that the class no longer be package private.
 *
 * @author Josh Helmer, jhelmer@unicon.net
 * @since 3.0
 */
public class JpaEntryPostProcessor implements IMappedClassPostProcessor<JpaEntry, EntryDTO> {
    private AddresseePostProcessor addresseePostProcessor = new AddresseePostProcessor();


    @Autowired
    public void setAddresseePostProcessor(AddresseePostProcessor addresseePostProcessor) {
        this.addresseePostProcessor = addresseePostProcessor;
    }


    @Override
    public void process(JpaEntry mappedObject, EntryDTO src) {
        for (JpaAttribute attr : mappedObject.getAttributes()) {
            attr.setEntryId(mappedObject.getId());
        }

        for (JpaAddressee addr : mappedObject.getAddressees()) {
            addr.setEntryId(mappedObject.getId());
            addresseePostProcessor.process(addr, null);
        }

        for (JpaAction action : mappedObject.getActions()) {
            action.setEntryId(mappedObject.getId());
        }
    }
}
