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
package org.jasig.portlet.notice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Implementing this interface in a concreete {@link INotificationService} or
 * {@link INotificationServiceFilter} class indicates that the bean manages some sort of state
 * (usually a cache) that may need to be updated when (1) the user takes some sort of action, or (2)
 * the user manually requests a refresh.
 *
 * @since 4.0
 */
public interface IRefreshable {

    /**
     * Drop cached data;  obtain from remote data sources on next fetch.
     *
     * @param request The REST request
     * @param response The REST response
     * @since 4.0
     */
    void refresh(HttpServletRequest request, HttpServletResponse response);

}
