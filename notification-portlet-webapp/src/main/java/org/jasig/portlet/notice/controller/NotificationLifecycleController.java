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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.ResourceRequest;
import javax.xml.namespace.QName;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.EventMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.notice.INotificationService;
import org.jasig.portlet.notice.NotificationCategory;
import org.jasig.portlet.notice.NotificationEntry;
import org.jasig.portlet.notice.NotificationResponse;
import org.jasig.portlet.notice.NotificationResult;
import org.jasig.portlet.notice.util.UsernameFinder;

/**
 * Gathering of notifications requires action and sometimes event phases.  This 
 * controller serves that purpose. 
 */
@Controller
@RequestMapping("VIEW")
public class NotificationLifecycleController {
    
    public static final String DO_EVENTS_PREFERENCE = "NotificationLifecycleController.doEvents";
    
    public static final String NOTIFICATION_NAMESPACE = "https://source.jasig.org/schemas/portlet/notification";
    
    public static final String NOTIFICATION_QUERY_LOCAL_NAME = "NotificationQuery";
    public static final QName NOTIFICATION_QUERY_QNAME = new QName(NOTIFICATION_NAMESPACE, NOTIFICATION_QUERY_LOCAL_NAME);
    public static final String NOTIFICATION_QUERY_QNAME_STRING = "{" + NOTIFICATION_NAMESPACE + "}" + NOTIFICATION_QUERY_LOCAL_NAME;
    
    public static final String NOTIFICATION_RESULT_LOCAL_NAME = "NotificationResult";
    public static final QName NOTIFICATION_RESULT_QNAME = new QName(NOTIFICATION_NAMESPACE, NOTIFICATION_RESULT_LOCAL_NAME);
    public static final String NOTIFICATION_RESULT_QNAME_STRING = "{" + NOTIFICATION_NAMESPACE + "}" + NOTIFICATION_RESULT_LOCAL_NAME;
    
    private static final String SUCCESS_PATH = "/scripts/success.json";
    
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    private UsernameFinder usernameFinder;

    @Resource(name="rootNotificationService")
    private INotificationService notificationService;

    @ResourceMapping("GET-NOTIFICATIONS-UNCATEGORIZED")
    public ModelAndView getNotifications(final ResourceRequest req, final @RequestParam(value="refresh", required=false) String doRefresh) throws IOException {

        // RequestParam("key") String key, HttpServletRequest request, ModelMap model
        log.debug("Invoking getNotifications for user:  " + usernameFinder.findUsername(req));

        // Get the notifications and any data retrieval errors
        final NotificationResponse notifications = notificationService.fetch(req);
        if (notifications == null) {
            String msg = "Notifications have not been loaded for user:  " + usernameFinder.findUsername(req);
            throw new IllegalStateException(msg);
        }

        // Combine all categories into one list
        final List<NotificationEntry> allEntries = new ArrayList<NotificationEntry>();
        for (final NotificationCategory y : notifications.getCategories()) {
            allEntries.addAll(y.getEntries());
        }

        final Map<String,Object> model = new HashMap<String,Object>(); 
        model.put("feed", allEntries);
        return new ModelAndView("json", model);

    }

    @ActionMapping(params="action=invokeNotificationService")
    public void invokeNotificationService(final ActionRequest req, final ActionResponse res, 
            @RequestParam(value="refresh", required=false) final String doRefresh) 
            throws IOException {

        final PortletPreferences prefs = req.getPreferences();
        final boolean doEvents = Boolean.parseBoolean(prefs.getValue(DO_EVENTS_PREFERENCE, "false"));
        if (doEvents) {

            notificationService.invoke(req, res, Boolean.parseBoolean(doRefresh));

            /*
             * TODO:  I wish we didn't have to go through a whole render phase just 
             * to trigger the events-based features of the portlet, but atm I don't 
             * see a way around it, since..
             * 
             *   - (1) You can only start an event chain in the Action phase;  and
             *   - (2) You can only return JSON in a Resource phase;  and
             *   - (3) An un-redirected Action phase leads to a Render phase, not a 
             *     Resource phase :(
             * 
             * It would be awesome either (first choice) to do Action > Event > Resource, 
             * or Action > sendRedirect() followed by a Resource request.  
             * 
             * As it stands, this implementation will trigger a complete render on 
             * the portal needlessly.
             */

        } else {
            // The real payload awaits a Render phase;  send a token response to 
            // avoid a full portlet request cycle (since we can).
            final String contextPath = req.getContextPath();
            res.sendRedirect(contextPath + SUCCESS_PATH);
        }

    }

    @EventMapping(NOTIFICATION_RESULT_QNAME_STRING)
    public void collectNotifications(final EventRequest req, final EventResponse res) {

        final PortletPreferences prefs = req.getPreferences();
        final boolean doEvents = Boolean.parseBoolean(prefs.getValue(DO_EVENTS_PREFERENCE, "false"));
        if (!doEvents) {
            // Get out...
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("Processing event=" + NOTIFICATION_RESULT_QNAME_STRING +" for user='" 
                                + usernameFinder.findUsername(req) 
                                + "' and windowId=" + req.getWindowID());
        }

        // Ignore results from other notification portlets
        final NotificationResult notificationResult = (NotificationResult) req.getEvent().getValue();
        if (notificationResult != null && req.getWindowID().equals(notificationResult.getQueryWindowId())) {
            notificationService.collect(req, res);
        }

    }

}
