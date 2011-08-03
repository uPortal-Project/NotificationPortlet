package org.jasig.portlet.notice.mvc.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.notice.NotificationData;
import org.jasig.portlet.notice.servicerequests.iface.NotificationRequestService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.mvc.AbstractController;


@Controller
@RequestMapping("VIEW")
public class NotificationController {

	private static Log log = LogFactory.getLog(NotificationController.class);

	
	/* (non-Javadoc)
	 * @see org.springframework.web.portlet.mvc.AbstractController#handleRenderRequestInternal(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
	 */
	@RequestMapping
	protected ModelAndView handleRenderRequestInternal(RenderRequest request,
			RenderResponse response) throws Exception {

		log.debug("In handleRenderRequestInternal");
		
		Map userInfo = (Map) request.getAttribute(PortletRequest.USER_INFO);
		String umanPersonID = (String)userInfo.get("umanPersonID");
		
		Map<String, String> myUserInfo = new HashMap<String, String>();
		myUserInfo.put("umanPersonID", umanPersonID);
		myUserInfo.put("username", request.getRemoteUser());
		PortletSession session = request.getPortletSession(true);
		session.setAttribute("myUserInfo",myUserInfo,PortletSession.APPLICATION_SCOPE);
		log.debug("Added userInfo to sesssion (e.g. "+(String) myUserInfo.get("username")+")");
		
		List<NotificationData> serviceDataList = new ArrayList<NotificationData>();
		
		for(Map.Entry<String,NotificationRequestService> entry : services.entrySet()) {
			String serviceName = entry.getKey();
			NotificationRequestService service = entry.getValue(); 

			NotificationData sd = new NotificationData();
			sd.setServiceKey(serviceName);
			
		    serviceDataList.add(sd);
		}
		
		
		return new ModelAndView("/index_VIEW_Normal", "serviceDataList", serviceDataList);
	}

	private Map<String,NotificationRequestService> services;
	public void setServices(Map<String, NotificationRequestService> services) {
		this.services = services;
	}
	
}
