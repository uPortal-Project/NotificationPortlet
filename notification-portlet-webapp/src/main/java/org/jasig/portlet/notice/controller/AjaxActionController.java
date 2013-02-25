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

package org.jasig.portlet.notice.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ActionMapping;

import org.jasig.portlet.notice.INotificationService;

/**
 * Gathering of notifications requires an action phase.  This controller serves 
 * that purpose. 
 */
@Controller
@RequestMapping("VIEW")
public class AjaxActionController {
    
    private static final String SUCCESS_PATH = "/scripts/success.json";
    
    @Resource(name="rootNotificationService")
    private INotificationService notificationService;

    @ActionMapping(params="action=invokeNotificationService")
    public void invokeNotificationService(final ActionRequest req, final ActionResponse res, 
            @RequestParam(value="refresh", required=false) final String doRefresh) 
            throws IOException {
        
        notificationService.invoke(req, Boolean.valueOf(doRefresh));

        // The real payload awaits a Render phase;  send a token response to 
        // avoid a full portlet request cycle.
        final String contextPath = req.getContextPath();
        res.sendRedirect(contextPath + SUCCESS_PATH);

    }

}
