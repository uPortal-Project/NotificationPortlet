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
import javax.portlet.ResourceRequest;


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
	 * @param refresh If true, the service should expire any cached data
	 */
    void invoke(ActionRequest req, Boolean refresh);

    /**
     * Provide the current collection of Notifications information for the user 
     * represented by the <code>PortletRequest</code>.
     *
     * @param req The <code>PortletRequest</code>
     * @return A collection of notifications and/or errors
     */
    NotificationResponse fetch(ResourceRequest req);


}
