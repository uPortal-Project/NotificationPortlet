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
package org.jasig.portlet.notice.service.event;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.jasig.portlet.notice.INotificationService;
import org.jasig.portlet.notice.NotificationConstants;
import org.jasig.portlet.notice.NotificationQuery;
import org.jasig.portlet.notice.NotificationResponse;
import org.jasig.portlet.notice.NotificationResult;
import org.jasig.portlet.notice.controller.NotificationLifecycleController;
import org.jasig.portlet.notice.service.AbstractNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Concrete {@link INotificationService} class that gathers notifications from other portlets based
 * on portlet events.
 *
 * @deprecated Prefer interactions that are not based on the Portlet API
 */
@Deprecated
public final class PortletEventNotificationService extends AbstractNotificationService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Cache cache;

    @Resource(name="PortletEventNotificationService.responseCache")
    public void setCache(Cache cache) {
        this.cache = cache;
    }

    @Override
    public void invoke(final ActionRequest req, final ActionResponse res, final boolean refresh) {

        // Since this behavior is potentially a great deal of work for the
        // portal, there is a portlet preference required to turn it on -- even
        // if the bean is configured in the context.
        final PortletPreferences prefs = req.getPreferences();
        final boolean doEvents = Boolean.parseBoolean(prefs.getValue(NotificationLifecycleController.DO_EVENTS_PREFERENCE, "false"));
        if (doEvents) {

            // Find the cached notifications, if any
            final String cacheKey = createServiceUserWindowSpecificCacheKey(req);

            // Send a request event?
            boolean sendRequestEvent = false;  // default
            if (refresh) {
                // Yes!
                cache.remove(cacheKey);
                sendRequestEvent = true;
            } else {
                // Not unless...
                final Element m = cache.get(cacheKey);
                if (m == null) {
                    // Yes!
                    sendRequestEvent = true;
                }
            }

            if (sendRequestEvent) {
                if (logger.isDebugEnabled()) {
                    logger.debug("REQUESTING Notifications events for user='"
                                        + usernameFinder.findUsername(req)
                                        + "' and windowId=" + req.getWindowID());
                }
                NotificationQuery query = new NotificationQuery();
                query.setQueryWindowId(req.getWindowID());
                res.setEvent(NotificationConstants.NOTIFICATION_QUERY_QNAME, query);
            }

        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public void collect(final EventRequest req, final EventResponse res) {

        if (logger.isDebugEnabled()) {
            logger.debug("RECEIVING Notifications events for user='"
                                + usernameFinder.findUsername(req)
                                + "' and windowId=" + req.getWindowID());
        }

        // Implements Serializable
        HashMap<String,NotificationResponse> responses;

        final String cacheKey = createServiceUserWindowSpecificCacheKey(req);

        final Element m = cache.get(cacheKey);
        if (m == null) {
            // Start fresh...
            responses = new HashMap<String,NotificationResponse>();
        } else {
            responses = (HashMap<String,NotificationResponse>) m.getObjectValue();
        }

        final NotificationResult notificationResult = (NotificationResult) req.getEvent().getValue();
        responses.put(notificationResult.getResultWindowId(), notificationResult.getNotificationResponse());
        cache.put(new Element(cacheKey, responses));

    }

    @Override
    public NotificationResponse fetch(final PortletRequest req) {

        NotificationResponse rslt = NotificationResponse.EMPTY_RESPONSE;  // default is empty

        final String cacheKey = createServiceUserWindowSpecificCacheKey(req);
        final Element m = cache.get(cacheKey);
        if (m != null) {
            @SuppressWarnings("unchecked")
            final Map<String,NotificationResponse> map = (Map<String,NotificationResponse>) m.getObjectValue();
            for (NotificationResponse response : map.values()) {
                rslt = rslt.combine(response);
            }
        }

        return rslt;

    }

    /**
     * This {@link INotificationService} is fundamentally about Java Portlets, and there is nothing
     * it can offer to this overload of <code>fetch</code>.
     *
     * @since 4.0
     */
    @Override
    public NotificationResponse fetch(HttpServletRequest request) {
        logger.trace("{} invoked for user '{}', but there is nothing to do",
                getClass().getSimpleName(), usernameFinder.findUsername(request));
        return NotificationResponse.EMPTY_RESPONSE;
    }

}
