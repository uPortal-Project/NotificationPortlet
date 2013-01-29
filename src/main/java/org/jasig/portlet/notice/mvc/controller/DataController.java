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

package org.jasig.portlet.notice.mvc.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
import javax.portlet.ResourceRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import org.jasig.portlet.notice.response.NotificationResponse;
import org.jasig.portlet.notice.service.INotificationService;

@Controller
@RequestMapping("VIEW")
public class DataController {
    
    public static final String ATTRIBUTE_HIDDEN_ERRORS = DataController.class.getName() + ".ATTRIBUTE_HIDDEN_ERRORS";

	private Log log = LogFactory.getLog(getClass());
	
	@Resource
	private String notificationsContextName;

    @Autowired(required=true)
	private INotificationService notificationService;
    
    @ResourceMapping("GET-NOTIFICATIONS")
	public ModelAndView getNotifications(ResourceRequest req, @RequestParam(value="refresh", required=false) String doRefresh) throws IOException {

	    // RequestParam("key") String key, HttpServletRequest request, ModelMap model
		log.trace("In getNotifications");

        Map<String, Object> model = new HashMap<String, Object>();
        try {

        	// Get the notifications and any data retrieval errors
            NotificationResponse notificationResponse = notificationService.getNotifications(req, Boolean.valueOf(doRefresh));

            //filter out any errors that have been hidden by the user
            PortletSession session = req.getPortletSession(true);
            @SuppressWarnings("unchecked")
            Set<Integer> hidden = (Set<Integer>) session.getAttribute(ATTRIBUTE_HIDDEN_ERRORS);
            if (hidden == null) {
                // Creates an empty set and puts it into session to get around null pointer exception.
                hidden = new HashSet<Integer>();
                session.setAttribute(ATTRIBUTE_HIDDEN_ERRORS, hidden);
            }
            notificationResponse.filterErrors(hidden);
            
            model.put("notificationResponse", notificationResponse);
            return new ModelAndView("json", model);

        } catch (Exception ex) {
            /* ********************************************************
                In the case of an unknown error we want to send the
                exception's message back to the portlet. This will
                let implementers write specific instructions for
                their service desks to follow for specific errors.
            ******************************************************** */
            log.error( "Unanticipated Error", ex);
            model.put("errorMessage", ex.getMessage());
            return new ModelAndView("json", model);
        }

	}

    @RequestMapping(params="action=hideError")
    public void hideError(ActionRequest req, ActionResponse res, @RequestParam("errorKey") String errorKey) throws IOException {
        PortletSession session = req.getPortletSession(true);
        @SuppressWarnings("unchecked")
        Set<Integer> hidden = (Set<Integer>) session.getAttribute(ATTRIBUTE_HIDDEN_ERRORS);
        if (hidden == null) {
            hidden = new HashSet<Integer>();
            session.setAttribute(ATTRIBUTE_HIDDEN_ERRORS, hidden);
        }
        int errorKeyInt =0;
        try {
            errorKeyInt = Integer.parseInt(errorKey);
        } catch (Exception e)
        {
            log.error(e);
        }
        hidden.add(errorKeyInt);
    }

}
