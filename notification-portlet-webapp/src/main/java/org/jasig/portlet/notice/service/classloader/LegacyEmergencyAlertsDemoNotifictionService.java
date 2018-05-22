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
package org.jasig.portlet.notice.service.classloader;

import org.jasig.portlet.notice.INotificationService;
import org.jasig.portlet.notice.NotificationResponse;

import javax.servlet.http.HttpServletRequest;

/**
 * The legacy EmergencyAlerts portlet uses a second bean based on {@link DemoNotificationService}.
 * This setup cannot handle the fact that classpath locations are configured globally in
 * PORTAL_HOME, instead of per-portlet.  This class is a specialized {@link DemoNotificationService}
 * that the new Notification model (based on NotificationRestV2Controller) will ignore.
 *
 * @deprecated The entire notion of Portlet API-based decorators is deprecated
 */
@Deprecated
public class LegacyEmergencyAlertsDemoNotifictionService extends DemoNotificationService {

    /**
     * This {@link INotificationService} does not participate in the REST-based components.
     */
    @Override
    public NotificationResponse fetch(HttpServletRequest req) {
        return NotificationResponse.EMPTY_RESPONSE;
    }

}
