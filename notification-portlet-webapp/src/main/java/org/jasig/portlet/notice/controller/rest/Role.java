package org.jasig.portlet.notice.controller.rest;

/**
 * @author Josh Helmer, jhelmer.unicon.net
 * @since 3.0
 */
public enum Role {
    REST_READ("ROLE_REST_READ"),
    REST_WRITE("ROLE_REST_WRITE");

    private String name;

    Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
