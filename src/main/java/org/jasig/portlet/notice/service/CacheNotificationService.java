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

import java.util.List;

import javax.annotation.Resource;
import javax.portlet.PortletRequest;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.notice.INotificationService;
import org.jasig.portlet.notice.NotificationResponse;
import org.jasig.portlet.notice.util.UsernameFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

/**
 * This class contains all the notification service providers. It implements
 * the EHCache implementation. An instance of this class is called from the
 * DataController in order to retrieve the notifications for a given user.
 */
public class CacheNotificationService extends AbstractNotificationService {

    @Autowired
    private UsernameFinder usernameFinder;

    private List<INotificationService> embeddedServices;
    private Cache cache;
    private final Log log = LogFactory.getLog(getClass());
    
    @Required
    public void setEmbeddedServices(List<INotificationService> embeddedServices) {
        this.embeddedServices = embeddedServices;
    }

    @Resource(name="notificationCache")
    public void setCache(Cache cache) {
        this.cache = cache;
    }

    @Override
    public NotificationResponse getNotifications(PortletRequest req, boolean refresh) {
        
        final String username = usernameFinder.findUsername(req);
        if (log.isDebugEnabled()) {
            log.debug("Notifications requested for user:  " + username + ";  refresh=" + refresh);
        }

        NotificationResponse rslt = new NotificationResponse();
        String cacheKey = createCacheKey(req);
        
        if (!refresh) {
            // It's okay to pull a response from cache, if we have one
            Element m = cache.get(cacheKey);
            if (m != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Cache HIT for user:  " + username);
                }
                rslt = (NotificationResponse) m.getObjectValue();
                return rslt;
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Cache MISS for user:  " + username);
                }
            }
        }
        
        // For whatever reason we can't pull from cache;  we need to hit 
        // the underlying data sources, then cache what we receive
        for(INotificationService notificationService: embeddedServices) {
            NotificationResponse nr = notificationService.getNotifications(req, refresh);
            rslt = rslt.combine(nr);
        }        
        cache.put(new Element(cacheKey, rslt));

        return rslt;

    }
    
    /*
     * Implementation
     */

    private String createCacheKey(PortletRequest req) {
        // Use the username until we discover a reason that's not good enough
        return usernameFinder.findUsername(req);
    }



}
