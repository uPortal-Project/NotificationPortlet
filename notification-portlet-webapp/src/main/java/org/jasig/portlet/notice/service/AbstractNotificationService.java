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
package org.jasig.portlet.notice.service;

import java.util.Arrays;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jasig.portlet.notice.INotificationService;
import org.jasig.portlet.notice.NotificationError;
import org.jasig.portlet.notice.NotificationResponse;
import org.jasig.portlet.notice.util.UsernameFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

public abstract class AbstractNotificationService implements INotificationService {

    private String name;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public String getName() {
        return name;
    }

    @Required
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Subclasses of {@link AbstractNotificationService} that need to perform 
     * some logic within this method should override it.
     */
    @Override
    public void invoke(final ActionRequest req, final ActionResponse res, final boolean refresh) { /* no-op */ }

    /**
     * Subclasses of {@link AbstractNotificationService} that need to perform 
     * some logic within this method should override it.
     */
    @Override
    public void collect(final EventRequest req, final EventResponse res) { /* no-op */ }

    /**
     * Returns <code>true</code>.  Subclasses of {@link AbstractNotificationService} 
     * that can better answer this question should override this method.
     */
    @Override
    public boolean isValid(final PortletRequest req, final NotificationResponse previousResponse) {
        return true;
    }

    /**
     * Returns an empty collection and logs the event.  All concrete implementations of
     * {@link INotificationService} should override this method.  The log entries this method
     * produces are an indication that the service subclass is not ready for the post-Portlet API
     * world.
     */
    @Override
    public NotificationResponse fetch(HttpServletRequest request) {
        logger.warn("Notification service '{}' was invoked by the portlet-agnostic API, but it" +
                "doesn't override fetch(HttpServletRequest)");
        return NotificationResponse.EMPTY_RESPONSE;
    }

    /*
     * Implementation
     */

    @Autowired
    protected UsernameFinder usernameFinder;

    protected final String createServiceUserWindowSpecificCacheKey(PortletRequest req) {
        /*
         * Cache keys are specific to (all of the below)...
         *
         *   - Service Name
         *   - Username
         *   - Portlet WindowId
         */
        final StringBuilder rslt = new StringBuilder();
        rslt.append(getName()).append("|").append(usernameFinder.findUsername(req))
                                        .append("|").append(req.getWindowID());
        return rslt.toString();
    }

    protected final NotificationResponse prepareErrorResponse(final String source, final String message) {
        final NotificationError error = new NotificationError();
        error.setSource(source);
        error.setError(message);
        final NotificationResponse rslt =  new NotificationResponse();
        rslt.setErrors(Arrays.asList(new NotificationError[] { error }));
        return rslt;
    }

}
