package org.jasig.portlet.notice.mvc.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.jasig.portlet.notice.serviceresponse.NotificationResponse;
import org.jasig.portlet.notice.serviceresponse.iface.NotificationResponseService;
import org.jasig.web.service.AjaxPortletSupportService;

@Controller
@RequestMapping("VIEW")
public class DataController {

	private Log log = LogFactory.getLog(getClass());
	
	@Autowired(required=true)
    private AjaxPortletSupportService ajaxPortletSupportService;
    
    @Autowired(required=true)
	private NotificationResponseService notificationService;

    @RequestMapping(params="action=getNotifications")
	public void getNotifications(ActionRequest req, ActionResponse res) throws IOException {

	    // RequestParam("key") String key, HttpServletRequest request, ModelMap model
		log.trace("In getNotifications");
		
        @SuppressWarnings("rawtypes")
        Map userInfo = (Map) req.getAttribute(PortletRequest.USER_INFO);
        String login = (String) userInfo.get("user.login.id");
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("login", login);
        params.put("username", req.getRemoteUser());
        
        Map<String, Object> model = new HashMap<String, Object>();
        try {
            
            List<NotificationResponse> responses = notificationService.getCurrentResponses("id", "testuser");

            NotificationResponse notificationResponse = new NotificationResponse();
            for(NotificationResponse r: responses) { // for each request
                notificationResponse.addResponseData(r);
            }

            model.put("notificationResponse", notificationResponse);
            ajaxPortletSupportService.redirectAjaxResponse("ajax/json", model, req, res);

        } catch (Exception ex) {
            /* ********************************************************
                In the case of an unknown error we want to send the
                exception's message back to the portlet. This will
                let implementers write specific instructions for
                their service desks to follow for specific errors.
            ******************************************************** */
            model.put("errorMessage", ex.getMessage());
            ajaxPortletSupportService.redirectAjaxResponse("ajax/json", model, req, res);
            log.error( "Unanticipated Error", ex);
        }

	}

}
