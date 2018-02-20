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
package org.jasig.portlet.notice.controller.notification;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

/**
 * Renders a small notification icon with an ajax-based badge that shows the
 * current number of botifications.  Clicking on the icon brings up the regular
 * Notification portlet.
 *
 * @author awills
 */
@RequestMapping("VIEW")
public final class NotificationIconController {

    private static final String USE_PPORTAL_JS_LIBS_PREFERENCE = "usePortalJsLibs";
    private static final String PPORTAL_JS_NAMESPACE_PREFERENCE = "portalJsNamespace";
    private static final String ICON_PREFERENCE = "NotificationIconController.faIcon";
    private static final String ICON_DEFAULT = "fa-bell";
    private static final String ACTIVE_COLOR_PREFERENCE = "NotificationIconController.activeColor";
    private static final String ACTIVE_COLOR_DEFAULT = "#d50000";
    private static final String URL_PREFERENCE = "NotificationIconController.url";
    private static final String URL_DEFAULT = "/uPortal/p/notification";
    private static final String VIEW_NAME = "icon";

    @RenderMapping()
    public ModelAndView display(RenderRequest req) {

        final Map<String,Object> model = new HashMap<String,Object>();
        final PortletPreferences prefs = req.getPreferences();

        final boolean usePortalJsLibs = Boolean.valueOf(prefs.getValue(USE_PPORTAL_JS_LIBS_PREFERENCE, "true"));  // default is true
        model.put("usePortalJsLibs", usePortalJsLibs);

        final String portalJsNamespace = prefs.getValue(PPORTAL_JS_NAMESPACE_PREFERENCE, "up");  // Matches the current convention in uPortal
        model.put("portalJsNamespace", portalJsNamespace);

        final String faIcon = prefs.getValue(ICON_PREFERENCE, ICON_DEFAULT);
        model.put("faIcon", faIcon);

        final String activeNotificationColor = prefs.getValue(ACTIVE_COLOR_PREFERENCE, ACTIVE_COLOR_DEFAULT);
        model.put("activeNotificationColor", activeNotificationColor);

        final String url = prefs.getValue(URL_PREFERENCE, URL_DEFAULT);
        model.put("url", url);

        final UUID uuid = UUID.randomUUID();
        model.put("uuid", uuid);

        return new ModelAndView(VIEW_NAME, model);

    }

}
