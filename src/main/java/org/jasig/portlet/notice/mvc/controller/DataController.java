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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.jasig.portlet.notice.response.NotificationResponse;
import org.jasig.portlet.notice.service.INotificationService;
import org.jasig.web.service.AjaxPortletSupportService;

@Controller
@RequestMapping("VIEW")
public class DataController {
    
    public static final String ATTRIBUTE_HIDDEN_ERRORS = DataController.class.getName() + ".ATTRIBUTE_HIDDEN_ERRORS";

	private Log log = LogFactory.getLog(getClass());
	
	@Resource
	private String notificationsContextName;

	@Autowired(required=true)
    private AjaxPortletSupportService ajaxPortletSupportService;

    @Autowired(required=true)
	private INotificationService notificationService;
    
    @RequestMapping(params="action=getNotifications")
	public void getNotifications(ActionRequest req, ActionResponse res, @RequestParam("refresh") String doRefresh) throws IOException {

	    // RequestParam("key") String key, HttpServletRequest request, ModelMap model
		log.trace("In getNotifications");

        Map<String, Object> model = new HashMap<String, Object>();
        try {
            
            // Figure out if we're here because of a refresh click and act accordingly
            if (Boolean.valueOf(doRefresh)) {
                notificationService.refreshNotifications(notificationsContextName, req.getRemoteUser());
            }

        	//get the notifications and any data retrieval errors
            NotificationResponse notificationResponse = notificationService.getNotifications(notificationsContextName, req.getRemoteUser(), req);

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
            ajaxPortletSupportService.redirectAjaxResponse("ajax/json", model, req, res);

        } catch (Exception ex) {
            /* ********************************************************
                In the case of an unknown error we want to send the
                exception's message back to the portlet. This will
                let implementers write specific instructions for
                their service desks to follow for specific errors.
            ******************************************************** */
            log.error( "Unanticipated Error", ex);
            model.put("errorMessage", ex.getMessage());
            ajaxPortletSupportService.redirectAjaxResponse("ajax/json", model, req, res);
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
