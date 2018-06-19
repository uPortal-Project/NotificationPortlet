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
package org.jasig.portlet.notice.controller.emergency;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.portlet.ActionRequest;
import javax.portlet.PortletRequest;

import org.jasig.portlet.notice.service.classloader.DemoNotificationService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

@RequestMapping("VIEW")
public final class EmergencyAlertAdminController {

    @Resource(name="demoEmergencyAlerts")
    private DemoNotificationService notificationService;  // Reference the concrete class b/c we need the extra methods
    
    /*
     * Public API.
     */

    public static final String VIEWNAME = "alert-admin";
    
    @RenderMapping()
    public ModelAndView showAdmin(PortletRequest req) {
        
        Map<String,Object> model = new HashMap<>();
        boolean enabled = notificationService.isActive();
        model.put("value", enabled ? "enabled" : "disabled");
        return new ModelAndView(VIEWNAME, model);

    }
    
    @ActionMapping
    public void toggleEnabled(ActionRequest req) {
        boolean current = notificationService.isActive();
        notificationService.setActive(!current);
    }

}
