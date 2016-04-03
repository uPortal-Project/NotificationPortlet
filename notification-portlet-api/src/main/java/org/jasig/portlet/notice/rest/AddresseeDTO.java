package org.jasig.portlet.notice.rest;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Josh Helmer, jhelmer.unicon.net
 * @since 3.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AddresseeDTO implements Serializable {
    private static final long serialVersionUid = 1l;

    private long id;
    private String name;
    private RecipientType type;
    private Set<RecipientDTO> recipients = new HashSet<>();


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public RecipientType getType() {
        return type;
    }


    public void setType(RecipientType type) {
        this.type = type;
    }


    public Set<RecipientDTO> getRecipients() {
        return recipients;
    }


    public void setRecipients(Set<RecipientDTO> recipients) {
        this.recipients = recipients;
    }
}
