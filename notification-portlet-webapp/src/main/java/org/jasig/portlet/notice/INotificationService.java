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

package org.jasig.portlet.notice;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletRequest;

/**
 * This is the central interface of the Notifications API.  It is used to
 * retrieve notifications from data sources.
 */
public interface INotificationService {

    /**
     * Returns the name of the service, which should be unique in the portlet app.
     *
     * @return A unique name for this service
     */
    String getName();

    /**
     * This method 'primes the pump' for {@link INotificationService} 
     * implementations.  Not all concrete services will need this method, and 
     * those that don't can safely make it a no-op.  But those that do need it 
     * can rely on receiving a call to <code>invoke()</code> before receiving a 
     * call to <code>fetch()</code>.
     * 
     * @param req The current ActionRequest
     * @param res The current ActionResponse
     * @param refresh If true, the service should expire any cached data
     */
    void invoke(ActionRequest req, ActionResponse res, boolean refresh);

    /**
     * This method allows {@link INotificationService} implementations that 
     * receive portlet events to collect notifications from other portlets in 
     * the portal.  Not all concrete services will need this method, and those 
     * that don't can safely make it a no-op.
     * 
     * @param req The current EventRequest
     * @param res The current EventResponse
     */
    void collect(EventRequest req, EventResponse res);

    /**
     * Provide the current collection of Notifications information for the user 
     * represented by the <code>PortletRequest</code>.
     *
     * @param req The <code>PortletRequest</code>
     * @return A collection of notifications and/or errors
     */
    NotificationResponse fetch(PortletRequest req);

    /**
     * Indicates whether a previous (presumably cached) {@link NotificationResponse} 
     * is still valid.  A few service implementations will be able to make this 
     * determination without contacting a remote service or doing anything 
     * computationally expensive.  Those that can't should just return 
     * <code>true</code>;  they will have the chance to update their responses 
     * when the cached version expires or when the user clicks refresh.
     * 
     * @param req The current request
     * @param previousResponse A response provided by this service at an earlier 
     * point
     * @return <code>true</code> if the earlier response is still acceptable for 
     * the present
     */
    boolean isValid(PortletRequest req, NotificationResponse previousResponse);

}
