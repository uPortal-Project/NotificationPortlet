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

package org.jasig.portlet.notice.controller.emergency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import org.jasig.portlet.notice.INotificationService;
import org.jasig.portlet.notice.NotificationCategory;
import org.jasig.portlet.notice.NotificationEntry;
import org.jasig.portlet.notice.NotificationResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

@Controller
@RequestMapping("VIEW")
public final class EmergencyAlertController {

    @Resource(name="rootNotificationService")
    private INotificationService notificationService;
    
    /*
     * Public API.
     */

    public static final String VIEW_NO_CURRENT_ALERT = "no-alert";
    public static final String VIEW_SHOW_CURRENT_ALERT = "show-alert";

    private static final String AUTO_ADVANCE_PREFERENCE = "EmergencyAlertController.autoAdvance";
    
    @RenderMapping()
    public ModelAndView showAlert(PortletRequest req) {
        
        final NotificationResponse notifications = notificationService.getNotifications(req, false);
        
        // Combine all categories into 1 list
        final List<NotificationEntry> allEntries = new ArrayList<NotificationEntry>();
        for (final NotificationCategory y : notifications.getCategories()) {
            allEntries.addAll(y.getEntries());
        }

        if (!allEntries.isEmpty()) {
            final Map<String,Object> model = new HashMap<String,Object>(); 
            model.put("feed", allEntries);
            final PortletPreferences prefs = req.getPreferences();            
            final boolean autoAdvance = Boolean.valueOf(prefs.getValue(AUTO_ADVANCE_PREFERENCE, null));  // default is false
            model.put("autoAdvance", autoAdvance);
            return new ModelAndView(VIEW_SHOW_CURRENT_ALERT, model);
        }
        
        return new ModelAndView(VIEW_NO_CURRENT_ALERT);  // default

    }

}
