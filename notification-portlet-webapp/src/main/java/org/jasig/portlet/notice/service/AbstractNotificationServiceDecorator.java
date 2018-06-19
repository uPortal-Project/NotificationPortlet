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

import org.jasig.portlet.notice.INotificationService;
import org.jasig.portlet.notice.NotificationResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Abstract base class for concrete {@link INotificationService} beans that are decorators.  The
 * functions historically performed by decorators are being moved.  This class implements methods
 * for the post-Portlet API model by throwing an exception.
 *
 * @since 4.0
 * @deprecated The entire notion of Portlet API-based decorators is deprecated
 */
@Deprecated
public abstract class AbstractNotificationServiceDecorator implements INotificationService {

    @Override
    public NotificationResponse fetch(HttpServletRequest request) {
        throw new UnsupportedOperationException("Decorator service implementations work only in portlets");
    }

}
