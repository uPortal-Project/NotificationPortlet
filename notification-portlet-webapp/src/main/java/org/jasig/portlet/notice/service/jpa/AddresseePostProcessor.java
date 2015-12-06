package org.jasig.portlet.notice.service.jpa;

import org.jasig.portlet.notice.rest.AddresseeDTO;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Post processor for Handling dozer mappings from AddresseeDTO -> JpaAddressee.
 * Just ensures that the addresseeId value is set in the JPA version.
 *
 * @author Josh Helmer, jhelmer.unicon.net
 * @since 3.0
 */
public class AddresseePostProcessor implements IMappedClassPostProcessor<JpaAddressee, AddresseeDTO> {

    @Override
    public void process(JpaAddressee mappedObject, AddresseeDTO srcObject) {
        for (JpaRecipient recipient : mappedObject.getRecipients()) {
            recipient.setAddresseeId(mappedObject.getId());
        }
    }
}
