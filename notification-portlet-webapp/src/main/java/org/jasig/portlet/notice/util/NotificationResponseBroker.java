/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jasig.portlet.notice.util;

import javax.portlet.ActionRequest;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.ResourceRequest;

import org.jasig.portlet.notice.NotificationResponse;
import org.springframework.stereotype.Component;

@Component
public class NotificationResponseBroker {

    public static final String NOTIFICATION_RESPONSE_KEY_PREFIX = 
            NotificationResponseBroker.class.getName() + ".notificationResponse";

    public void storeNotificationResponse(final ActionRequest req, final NotificationResponse notificationResponse) {
        final String key = evaluateSessionKey(req);
        req.getPortletSession().setAttribute(key, notificationResponse);
    }

    public NotificationResponse retrieveNotificationResponse(final ResourceRequest req) {
        final PortletSession session = req.getPortletSession();
        final String key = evaluateSessionKey(req);
        final NotificationResponse rslt = (NotificationResponse) session.getAttribute(key);
        session.removeAttribute(key);
        return rslt;
    }
    
    /*
     * Implementation
     */

    private String evaluateSessionKey(final PortletRequest req) {
        return NOTIFICATION_RESPONSE_KEY_PREFIX + "|" + req.getWindowID();
    }
    
}
