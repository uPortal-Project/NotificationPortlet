/*
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
package org.jasig.portlet.notice.util;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("usernameFinder")
public final class UsernameFinder {

    @Value("${UsernameFinder.unauthenticatedUsername}")
    private String unauthenticatedUsername = "guest";

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @deprecated Prefer interactions that are not based on the Portlet API
     */
    @Deprecated
    public String findUsername(PortletRequest req) {
        return req.getRemoteUser() != null
                ? req.getRemoteUser()
                : unauthenticatedUsername;
    }

    /**
     * @since 4.0
     */
    public String findUsername(HttpServletRequest request) {

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.trace("Processing the following Authentication object:  {}", authentication);

        final String rslt = (String) authentication.getPrincipal();

        logger.debug("Found username '{}' based on the contents of the SecurityContextHolder", rslt);

        // Identification based on Spring Security is required to access Servlet-based APIs
        if (rslt == null) {
            throw new SecurityException("User not identified");
        }

        return rslt;

    }

    /**
     * @deprecated Prefer interactions that are not based on the Portlet API
     */
    @Deprecated
    public boolean isAuthenticated(PortletRequest req) {
        return !findUsername(req).equalsIgnoreCase(unauthenticatedUsername);
    }

    public boolean isAuthenticated(HttpServletRequest request) {

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.trace("Processing the following Authentication object:  {}", authentication);

        return authentication != null && authentication.isAuthenticated();

    }

}
