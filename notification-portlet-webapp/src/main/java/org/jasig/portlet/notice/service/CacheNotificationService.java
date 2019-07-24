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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.jasig.portlet.notice.INotificationService;
import org.jasig.portlet.notice.NotificationResponse;
import org.jasig.portlet.notice.service.filter.FilteringNotificationServiceDecorator;
import org.jasig.portlet.notice.service.jdbc.AbstractJdbcNotificationService;
import org.jasig.portlet.notice.util.PortletXmlRoleService;
import org.jasig.portlet.notice.util.UsernameFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;

/**
 * This class decorates and aggregates all the notification service providers. It also provides
 * caching via EHCache. Each child context (e.g. portlet) has it's own instance of this class.
 */
public final class CacheNotificationService extends AbstractNotificationService {

    // Wired by Spring
    private ApplicationContext applicationContext;
    private List<INotificationService> embeddedServices;
    private Cache cache;

    // Some portlets want to ONLY use explicit services, e.g. Emergency Alerts
    private boolean useDiscoverable = true;

    @Autowired(required=false)
    public void setUseDiscoverable(String useDisc) {
        this.useDiscoverable = Boolean.valueOf(useDisc);
    }

    // Managed internally
    private final Map<String,INotificationService> servicesMap = new HashMap<>();
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Required
    public void setEmbeddedServices(List<INotificationService> embeddedServices) {
        this.embeddedServices = embeddedServices;
    }

    @Resource(name="notificationResponseCache")
    public void setCache(Cache cache) {
        this.cache = cache;
    }

    @PostConstruct
    public void init() {
        servicesMap.clear();  // reset the Map

        logger.info("Configuring CacheNotificationService with name='{}'", getName());

        // Load the services provided explicitly
        for (INotificationService s : embeddedServices) {
            if (servicesMap.containsKey(s.getName())) {
                // This is a configuration error
                final String msg = "Notification services names must be unique;  duplicate detected for name:  " + s.getName();
                throw new IllegalArgumentException(msg);
            }
            servicesMap.put(s.getName(), s);
            logger.info("Added configured Notification Service:  {}", s.getName());
        }

        if (useDiscoverable) {
            // Discover services provided by implementors (TODO: we should move everything to discovery)
            final Map<String,AbstractJdbcNotificationService> discoverableServices =
                    BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext,
                            AbstractJdbcNotificationService.class);
            if (discoverableServices.size() != 0) {
                /*
                 * To match the behavior/capabilities of INotificationService beans declared in the
                 * applicationContext.xml, we need to decorate these beans in a
                 * FilteringNotificationServiceDecorator.  We're also obligated to provide the
                 * dependencies of FilteringNotificationServiceDecorator.
                 */
                final UsernameFinder usernameFinder =
                        (UsernameFinder) applicationContext.getBean("usernameFinder");
                final PortletXmlRoleService portletXmlRoleService =
                        (PortletXmlRoleService) applicationContext.getBean("portletXmlRoleService");

                for (AbstractJdbcNotificationService s : discoverableServices.values()) {
                    final FilteringNotificationServiceDecorator decorator = new FilteringNotificationServiceDecorator();
                    decorator.setEnclosedNotificationService(s);
                    decorator.setUsernameFinder(usernameFinder);
                    decorator.setPortletXmlRoleService(portletXmlRoleService);

                    servicesMap.put(decorator.getName(), decorator);
                    logger.info("Added discovered Notification Service:  {}", s.getName());
                }
            }
        }
    }

    @Override
    public void invoke(ActionRequest req, ActionResponse res, boolean refresh) {

        logger.trace("Processing invoke() for user '{}' refresh={}",
                usernameFinder.findUsername(req), refresh);

        if (refresh) {
            // Make certain we have a cache MISS on next fetch()
            final String cacheKey = createServiceUserWindowSpecificCacheKey(req);
            cache.remove(cacheKey);
        }

        for (INotificationService service : servicesMap.values()) {
            service.invoke(req, res, refresh);
        }

    }

    @Override
    public void collect(EventRequest req, EventResponse res) {

        for (INotificationService service : servicesMap.values()) {
            service.collect(req, res);
        }

    }

    @Override
    public NotificationResponse fetch(PortletRequest req) {

        final String username = usernameFinder.findUsername(req);
        logger.debug("Notifications requested for user='{}' and windowId={}", username, req.getWindowID());

        CacheTuple tuple;  // Existing or new?
        final String cacheKey = createServiceUserWindowSpecificCacheKey(req);
        final Element m = cache.get(cacheKey);
        if (m != null) {
            logger.debug("Cache HIT for user='{}' and windowId={}", username, req.getWindowID());

            // We have a cached element, but it could be
            // PARTIALLY invalid; make sure it's fresh
            tuple = (CacheTuple) m.getObjectValue();
            final Map<String,NotificationResponse> iterable =
                    new HashMap<>(tuple.getResponses());  // Can't iterate & modify the same collection
            for (Map.Entry<String,NotificationResponse> entry : iterable.entrySet()) {
                final INotificationService service = servicesMap.get(entry.getKey());
                if (service == null) {
                    // This is perplexing -- should not happen
                    logger.warn("Unmatched NotificationResponse in CacheTuple;  " +
                            "service.name()='{}' and user='{}'", entry.getKey(), username);
                    tuple.getResponses().remove(entry.getKey());
                    continue;
                }
                // Refresh if needed
                if (!service.isValid(req, entry.getValue())) {
                    final NotificationResponse freshResponse = getResponseFromService(req, service);
                    tuple.getResponses().put(entry.getKey(), freshResponse);
                }
            }
        } else {
            logger.debug("Cache MISS for user='{}' and windowId={}", username, req.getWindowID());

            // For whatever reason we can't pull from cache;  we need to hit
            // the underlying data sources, then cache what we receive
            tuple = new CacheTuple();
            for(INotificationService service : servicesMap.values()) {
                final NotificationResponse nr = getResponseFromService(req, service);
                tuple.getResponses().put(service.getName(), nr);
            }
            cache.put(new Element(cacheKey, tuple));
        }

        // Construct a new NotificationResponse from constituent parts...
        NotificationResponse rslt = new NotificationResponse();
        for (Map.Entry<String,NotificationResponse> entry : tuple.getResponses().entrySet()) {
            rslt = rslt.combine(entry.getValue());
        }

        return rslt;

    }

    /*
     * Implementation
     */

    private NotificationResponse getResponseFromService(PortletRequest req, INotificationService service) {
        NotificationResponse rslt;
        try {
            rslt = service.fetch(req);
        } catch (Exception e) {
            final String msg = "Failed to invoke the specified service:  " + service.getName();
            logger.error(msg, e);
            rslt = prepareErrorResponse(getName(), msg);
        }
        return rslt;
    }

    /*
     * Nested Types
     */

    private static final class CacheTuple {

        // Instance members
        private final Map<String,NotificationResponse> responses = new HashMap<>();

        public Map<String,NotificationResponse> getResponses() {
            return this.responses;
        }

    }

}
