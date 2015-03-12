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
package org.jasig.portlet.notice.service.ssp;

import javax.portlet.PortletRequest;


/**
 * @author Josh Helmer, jhelmer.unicon.net
 */
public interface ISSPPersonLookup {
    /**
     * Lookup the SSP studentId associated with a request.   This implementation
     * depends on the user having the schoolId available as an attribute on the
     * portal user record.
     *
     * @param request the portlet request
     * @return the studentId if available.  null if studentId can not
     * be found.
     */
    String lookupPersonId(PortletRequest request);
}
