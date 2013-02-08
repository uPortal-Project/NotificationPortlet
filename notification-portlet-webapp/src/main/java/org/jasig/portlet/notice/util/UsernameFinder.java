package org.jasig.portlet.notice.util;

import javax.portlet.PortletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public final class UsernameFinder {
    
    @Value("${UsernameFinder.unauthenticatedUsername}")
    private String unauthenticatedUsername = "guest"; 
    
    public String findUsername(PortletRequest req) {
        return req.getRemoteUser() != null
                ? req.getRemoteUser()
                : unauthenticatedUsername;
    }

}
