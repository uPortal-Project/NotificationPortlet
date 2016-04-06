/**
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
package org.jasig.portlet.notice.controller.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Helper class that provides an additional security check to ensure that the REST API has
 * been enabled.  If not enabled, all calls to the REST API should return a 403.  I feel
 * like it should be possible to do this with pure spring-el, but didn't seem to work right.
 * In addition, this provides a place to potentially add hooks for adding auth hooks that
 * can look for a uPortal principal.
 *
 * @author Josh Helmer, jhelmer.unicon.net
 * @since 3.0
 */
@Service("restApiSecurityService")
public class RestApiSecurityService {
    @Value("${restApi.enabled:false}")
    private boolean restApiEnabled;


    public boolean isEnabled() {
        return restApiEnabled;
    }
}
