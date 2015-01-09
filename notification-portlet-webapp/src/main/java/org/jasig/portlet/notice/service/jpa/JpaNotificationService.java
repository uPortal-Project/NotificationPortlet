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

package org.jasig.portlet.notice.service.jpa;

import javax.portlet.PortletRequest;

import org.jasig.portlet.notice.NotificationResponse;
import org.jasig.portlet.notice.service.AbstractNotificationService;

/**
 * Implementation of {@link INotificationService} backed by a Spring-/JPA-managed
 * relational database schema.
 * 
 * @author drewwills
 */
public class JpaNotificationService extends AbstractNotificationService {

    /**
     * This prefix helps to keep the Notification table together (in an
     * alphabetized list) and provides clarity to their origin and purpose.
     */
    public static final String TABLENAME_PREFIX = "NOTICE_";

    @Override
    public NotificationResponse fetch(PortletRequest req) {
        throw new UnsupportedOperationException();
    }

}
