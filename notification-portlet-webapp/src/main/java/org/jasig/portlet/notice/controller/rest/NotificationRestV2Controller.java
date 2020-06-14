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
package org.jasig.portlet.notice.controller.rest;

import org.apereo.portal.soffit.Headers;
import org.jasig.portlet.notice.INotificationRepository;
import org.jasig.portlet.notice.NotificationAction;
import org.jasig.portlet.notice.NotificationEntry;
import org.jasig.portlet.notice.NotificationResponse;
import org.jasig.portlet.notice.filter.ApiUrlSupportFilter;
import org.jasig.portlet.notice.util.NotificationResponseFlattener;
import org.jasig.portlet.notice.util.UsernameFinder;
import org.jasig.portlet.notice.util.sort.Sorting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * New REST API for the Notification project suitable for next-generation content objects.  This
 * controller (1) is Servlet API-based and (2) provides access to notifications from any data
 * source.
 *
 * @since 4.0
 */
@RestController
@RequestMapping(NotificationRestV2Controller.API_ROOT)
public class NotificationRestV2Controller {

    public static final String API_ROOT = "/api/v2";

    @Autowired
    private INotificationRepository repository;

    @Autowired
    private NotificationResponseFlattener notificationResponseFlattener;

    @Autowired
    private UsernameFinder usernameFinder;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/notifications", method = RequestMethod.GET)
    public List<NotificationEntry> fetchNotifications(HttpServletRequest request) {
        final NotificationResponse response = repository.fetch(request);
        final List<NotificationEntry> rslt = notificationResponseFlattener.flatten(response);
        logger.debug("result count = {}", rslt.size());
        logger.debug("result = {}", );
        return Sorting.sort(request, rslt);
    }

    @RequestMapping(value = "/action/{actionId}/{notificationId}", method = RequestMethod.POST)
    public Map<String,Object> invokeAction(HttpServletRequest request, HttpServletResponse response,
                            @PathVariable("actionId") String actionId,
                            @PathVariable("notificationId") String notificationId) {

        /*
         * NOTE:  The request may be coming from a <form> submission, and (therefore) may not have
         * an Authorization header.  Check for an _authorization parameter.  (The function that
         * builds URLs for invoking this handler method bakes them into the query string.)
         */
        final HttpServletRequest decoratedRequest = decorateWithAuthorizationIfPresent(request);

        // Obtain the collection
        final NotificationResponse notifications = repository.fetch(decoratedRequest);

        // Find the relevant action
        NotificationAction target = null;
        final NotificationEntry entry = notifications.findNotificationEntryById(notificationId);
        if (entry != null) {
            for (NotificationAction a : entry.getAvailableActions()) {
                if (actionId.equals(a.getId())) {
                    target = a;
                    break;
                }
            }
        } else {
            logger.warn("Notification not found for notificationId='{}'", notificationId);
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return null;
        }

        // We must have a target to proceed
        if (target != null) {
            try {
                target.invoke(decoratedRequest, response);
            } catch (IOException e) {
                final String username = usernameFinder.findUsername(decoratedRequest);
                logger.error("Failed to invoke action '{}' on entry '{}' for user '{}'",
                        target.getLabel(), entry.getId(), username, e);
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                return null;
            }
            // It's reasonable to assume we need to purge
            // caches for this user after invoking his action
            repository.refresh(decoratedRequest, response);
        } else {
            logger.warn("Target action not found for notificationId='{}' and actionId='{}'",
                    notificationId, actionId);
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return null;
        }

        return Collections.singletonMap("success", true);

    }

    private HttpServletRequest decorateWithAuthorizationIfPresent(HttpServletRequest request) {
        return new HttpServletRequestWrapper(request) {
            @Override
            public String getHeader(String name) {
                if (HttpHeaders.AUTHORIZATION.equalsIgnoreCase(name)
                        && request.getParameterMap().containsKey(ApiUrlSupportFilter.AUTHORIZATION_PARAMETER_NAME)) {
                    return Headers.BEARER_TOKEN_PREFIX + request.getParameter(ApiUrlSupportFilter.AUTHORIZATION_PARAMETER_NAME);
                } else {
                    return super.getHeader(name);
                }
            }

            @Override
            public Enumeration<String> getHeaders(String name) {
                if (HttpHeaders.AUTHORIZATION.equalsIgnoreCase(name)
                        && request.getParameterMap().containsKey(ApiUrlSupportFilter.AUTHORIZATION_PARAMETER_NAME)) {
                    return Collections.enumeration(
                            Collections.singletonList(Headers.BEARER_TOKEN_PREFIX + request.getParameter(ApiUrlSupportFilter.AUTHORIZATION_PARAMETER_NAME))
                    );
                } else {
                    return super.getHeaders(name);
                }
            }

            @Override
            public Enumeration<String> getHeaderNames() {
                if (request.getParameterMap().containsKey(ApiUrlSupportFilter.AUTHORIZATION_PARAMETER_NAME)) {
                    final Vector<String> vector = new Vector<>(Collections.list(getHeaderNames()));
                    vector.add(HttpHeaders.AUTHORIZATION);
                    return vector.elements();
                } else {
                    return super.getHeaderNames();
                }
            }
        };
    }

}
