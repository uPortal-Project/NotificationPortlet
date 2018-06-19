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
package org.jasig.portlet.notice.service.rest;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

/**
 * Beans that implement this interface process replacement tokens in (primarily) urls.
 *
 * @since 3.1
 */
public interface IParameterEvaluator {

    /**
     * The replacement token that this {@link IParameterEvaluator} instance will perform replacement
     * on.  Services that use parameter evaluators will place these onjects into the evaluation
     * context based on the key they specify.
     *
     * @return The token this evaluator replaces
     */
    String getToken();

    /**
     * Provides a value for the configured parameter, or <code>null</code> if 
     * none is available.
     * 
     * @param req The current, active PortletRequest
     * @return A valid value or <code>null</code>
     * @deprecated Prefer interactions that are not based on the Portlet API
     */
    @Deprecated
    String evaluate(PortletRequest req);

    /**
     * Provides a value for the configured parameter, or <code>null</code> if
     * none is available.
     *
     * @param req The current, active <code>HttpServletRequest</code>
     * @return A valid value or <code>null</code>
     * @since 4.0
     */
    String evaluate(HttpServletRequest req);

}
