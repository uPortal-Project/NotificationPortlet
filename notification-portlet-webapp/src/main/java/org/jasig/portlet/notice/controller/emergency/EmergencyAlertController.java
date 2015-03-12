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
package org.jasig.portlet.notice.controller.emergency;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.notice.INotificationService;
import org.jasig.portlet.notice.util.UsernameFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

@RequestMapping("VIEW")
public final class EmergencyAlertController {

    public static final String VIEW_SHOW_ALERTS = "show-alerts";

    private static final String USE_PPORTAL_JS_LIBS_PREFERENCE = "EmergencyAlertController.usePortalJsLibs";
    private static final String PPORTAL_JS_NAMESPACE_PREFERENCE = "portalJsNamespace";
    private static final String AUTO_ADVANCE_PREFERENCE = "EmergencyAlertController.autoAdvance";

    private final Log log = LogFactory.getLog(getClass());

    @Resource(name="rootNotificationService")
    private INotificationService notificationService;

    @Autowired
    private UsernameFinder usernameFinder;

    @RenderMapping()
    public ModelAndView showAlert(PortletRequest req) {

        final Map<String,Object> model = new HashMap<String,Object>(); 
        final PortletPreferences prefs = req.getPreferences();   

        final boolean usePortalJsLibs = Boolean.valueOf(prefs.getValue(USE_PPORTAL_JS_LIBS_PREFERENCE, "true"));  // default is true
        model.put("usePortalJsLibs", usePortalJsLibs);

        final String portalJsNamespace = prefs.getValue(PPORTAL_JS_NAMESPACE_PREFERENCE, "up");  // Matches the current convention in uPortal
        model.put("portalJsNamespace", portalJsNamespace);

        final boolean autoAdvance = Boolean.valueOf(prefs.getValue(AUTO_ADVANCE_PREFERENCE, null));  // default is false
        model.put("autoAdvance", autoAdvance);

        if (log.isTraceEnabled()) {
            log.trace("Showing alerts, usePortalJsLibs=" + usePortalJsLibs 
                            + ", portalJsNamespace=" + portalJsNamespace 
                            + ", autoAdvance=" + autoAdvance);
        }

        final UUID uuid = UUID.randomUUID();
        model.put("uuid", uuid);

        return new ModelAndView(VIEW_SHOW_ALERTS, model);

    }

}
