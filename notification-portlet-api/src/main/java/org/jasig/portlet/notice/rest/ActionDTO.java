package org.jasig.portlet.notice.rest;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

/**
 * @author Josh Helmer, jhelmer.unicon.net
 * @since 3.0
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class ActionDTO implements Serializable {
    private static final long serialVersionUid = 1l;

    private long id;
    private String label;
    private String clazz;


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public String getLabel() {
        return label;
    }


    public void setLabel(String label) {
        this.label = label;
    }


    public String getClazz() {
        return clazz;
    }


    public void setClazz(String clazz) {
        this.clazz = clazz;
    }
}
