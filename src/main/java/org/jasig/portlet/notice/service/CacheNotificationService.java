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
import javax.portlet.PortletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.notice.response.NotificationResponse;
import org.jasig.portlet.notice.service.exceptions.NotificationServiceException;
import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.KeyGenerator;
import com.googlecode.ehcache.annotations.PartialCacheKey;
import com.googlecode.ehcache.annotations.Property;
import com.googlecode.ehcache.annotations.TriggersRemove;

/**
 * This class contains all the notification service providers. It implements
 * the EHCache implementation. An instance of this class is called from the
 * DataController in order to retrieve the notifications for a given user.
 */
public class CacheNotificationService implements INotificationService {

    private final Log log = LogFactory.getLog(getClass());

    /**
	 * Returns the name of the service.
	 * @return String.
	 */
	public String getName()
	{
		return "CacheService";
	}

    @Cacheable(cacheName="notificationCache",
        keyGenerator = @KeyGenerator (
            name = "HashCodeCacheKeyGenerator",
            properties = @Property(name="includeMethod", value="false")
        )
    )
    @Override
    public NotificationResponse getNotifications(@PartialCacheKey String notificationsContextName, 
            @PartialCacheKey String remoteUser, PortletRequest req) throws NotificationServiceException {
        // The point of providing this implementation (instead of extending 
        // AbstractNotificationService) is for the caching annotations
        return this.fetchNotificationsFromSource(req);
    }
    
    @TriggersRemove(cacheName="notificationCache",
        keyGenerator = @KeyGenerator (
            name = "HashCodeCacheKeyGenerator",
            properties = @Property(name="includeMethod", value="false")
        )
    )
    @Override
    public void refreshNotifications(String notificationsContextName, String remoteUser) {
        // This method exists for its annotations.
    }

	@Override
	public NotificationResponse fetchNotificationsFromSource(PortletRequest req)
		throws NotificationServiceException
	{
	    log.debug("Invoking embedded notification services...");
	    
		NotificationResponse masterResponse = new NotificationResponse();

		for(INotificationService notificationService: embeddedServices)
		{
		    NotificationResponse response = notificationService.fetchNotificationsFromSource(req);
		    masterResponse.addResponseData(response);
		}
		
		return masterResponse;
	}

	private List<INotificationService> embeddedServices;
    public void setEmbeddedServices(List<INotificationService> embeddedServices) {
        this.embeddedServices = embeddedServices;
    }

}
