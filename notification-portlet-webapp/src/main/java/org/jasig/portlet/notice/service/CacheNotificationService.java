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

package org.jasig.portlet.notice.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletRequest;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.notice.INotificationService;
import org.jasig.portlet.notice.NotificationResponse;
import org.springframework.beans.factory.annotation.Required;

/**
 * This class contains all the notification service providers. It implements
 * the EHCache implementation. An instance of this class is called from the
 * DataController in order to retrieve the notifications for a given user.
 */
public final class CacheNotificationService extends AbstractNotificationService {

    private final Map<String,INotificationService> servicesMap = new HashMap<String,INotificationService>();
    private Cache cache;
    private final Log log = LogFactory.getLog(getClass());

    @Required
    public void setEmbeddedServices(final List<INotificationService> services) {
        servicesMap.clear();  // reset the Map
        for (INotificationService s : services) {
            if (servicesMap.containsKey(s.getName())) {
                // This is a configuration error
                String msg = "Notification services names must be unique;  duplicate detected for name:  " + s.getName();
                throw new IllegalArgumentException(msg);
            }
            servicesMap.put(s.getName(), s);
        }
    }

    @Resource(name="notificationResponseCache")
    public void setCache(Cache cache) {
        this.cache = cache;
    }

    @Override
    public void invoke(final ActionRequest req, final ActionResponse res, final boolean refresh) {

        if (log.isTraceEnabled()) {
            final String username = usernameFinder.findUsername(req);
            log.trace("Processing invoke() for user ''" + username + "' refresh=" + refresh);
        }

        if (refresh) {
            // Make certain we have a cache MISS on fetch()
            final String cacheKey = createServiceUserWindowSpecificCacheKey(req);
            cache.remove(cacheKey);
        }

        for (INotificationService service : servicesMap.values()) {
            service.invoke(req, res, refresh);
        }

    }

    @Override
    public void collect(final EventRequest req, final EventResponse res) {

        for (INotificationService service : servicesMap.values()) {
            service.collect(req, res);
        }

    }

    @Override
    public NotificationResponse fetch(final PortletRequest req) {

        final String username = usernameFinder.findUsername(req);
        if (log.isDebugEnabled()) {
            log.debug("Notifications requested for user='" + username + "' and windowId=" + req.getWindowID());
        }

        NotificationResponse rslt = new NotificationResponse();
        final String cacheKey = createServiceUserWindowSpecificCacheKey(req);
//        final Element m = cache.get(cacheKey);
        final Element m = null;
        if (m != null) {
            if (log.isDebugEnabled()) {
                log.debug("Cache HIT for user='" + username 
                        + "' and windowId=" + req.getWindowID());
            }
            // We have a cached element, but it could be 
            // PARTIALLY invalid; make sure it's fresh
            CacheTuple tuple = (CacheTuple) m.getObjectValue();
            Map<String,NotificationResponse> iterable = 
                    new HashMap<String,NotificationResponse>(tuple.getResponses());  // Can't iterate & modify the same collection
            for (Map.Entry<String,NotificationResponse> entry : iterable.entrySet()) {
                INotificationService service = servicesMap.get(entry.getKey());
                if (service == null) {
                    // This is perplexing -- should not happen
                    log.warn("Unmatched NotificationResponse in CacheTuple;  service.name()='" 
                                    + entry.getKey() + "' and user='" + username + "'");
                    tuple.getResponses().remove(entry.getKey());
                }
                // Refresh if needed
                if (!service.isValid(req, entry.getValue())) {
                    final NotificationResponse freshResponse = getResponseFromService(req, service);
                    tuple.getResponses().put(entry.getKey(), freshResponse);
                }
            }
            // Construct a new NotificationResponse from constituent parts...
            for (Map.Entry<String,NotificationResponse> entry : tuple.getResponses().entrySet()) {
                rslt = rslt.combine(entry.getValue());
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Cache MISS for user='" + username 
                        + "' and windowId=" + req.getWindowID());
            }
            // For whatever reason we can't pull from cache;  we need to hit
            // the underlying data sources, then cache what we receive
            CacheTuple tuple = new CacheTuple();
            for(INotificationService service : servicesMap.values()) {
                final NotificationResponse nr = getResponseFromService(req, service);
                tuple.getResponses().put(service.getName(), nr);
            }
            cache.put(new Element(cacheKey, tuple));

            // Construct a new NotificationResponse from constituent parts...
            for (Map.Entry<String,NotificationResponse> entry : tuple.getResponses().entrySet()) {
                rslt = rslt.combine(entry.getValue());
            }
        }

        return rslt;

    }

    /*
     * Implementation
     */

    private final NotificationResponse getResponseFromService(final PortletRequest req, final INotificationService service) {
        NotificationResponse rslt = null;
        try {
            rslt = service.fetch(req);
        } catch (Exception e) {
            String msg = "Failed to invoke the specified service:  " + service.getName();
            log.error(msg, e);
            rslt = prepareErrorResponse(getName(), msg);
        }
        return rslt;
    }

    /*
     * Nested Types
     */

    private static final class CacheTuple {

        // Instance members
        private final Map<String,NotificationResponse> responses = new HashMap<String,NotificationResponse>();

        public Map<String,NotificationResponse> getResponses() {
            return this.responses;
        }

    }

}
