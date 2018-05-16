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
 * Concrete implementation of {@link IParameterEvaluator} based on request attributes.  Works with
 * both <code>PortletRequest</code> (though deprecated) and <code>HttpServletRequest</code>.
 *
 * @since 4.0
 */
public class RequestAttributeParameterEvaluator extends AbstractParameterEvaluator {
    protected String attributeKey;

    public void setAttributeKey(String attributeKey) {
        this.attributeKey = attributeKey;
    }

    @Override
    public String evaluate(PortletRequest req) {
        Object o = req.getAttribute(attributeKey);
        return o == null ? null : o.toString();
    }

    @Override
    public String evaluate(HttpServletRequest req) {
        Object o = req.getAttribute(attributeKey);
        return o == null ? null : o.toString();
    }
}
