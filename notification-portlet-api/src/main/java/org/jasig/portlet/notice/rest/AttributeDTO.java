package org.jasig.portlet.notice.rest;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Josh Helmer, jhelmer.unicon.net
 * @since 3.0
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class AttributeDTO implements Serializable {
    private static final long serialVersionUid = 1l;
    private long id;
    private String name;
    private List<String> values = new ArrayList<>();


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


    public List<String> getValues() {
        return values;
    }


    public void setValues(List<String> values) {
        this.values = values;
    }
}
