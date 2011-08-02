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
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.mvc.AbstractController;

import org.jasig.portlet.notice.servicerequests.ServiceRequest;
import org.jasig.portlet.notice.ServiceData;
import org.jasig.portlet.notice.servicerequests.iface.ServiceRequestService;

public class ToDoController extends AbstractController {

	private static Log log = LogFactory.getLog(ToDoController.class);

	
	/* (non-Javadoc)
	 * @see org.springframework.web.portlet.mvc.AbstractController#handleRenderRequestInternal(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
	 */
	@Override
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
		
		ModelAndView mav = new ModelAndView();

		List<ServiceData> serviceDataList = new ArrayList<ServiceData>();
		
		for(Map.Entry<String,ServiceRequestService> entry : services.entrySet()) {
			String serviceName = entry.getKey();
			ServiceRequestService service = entry.getValue(); 

			ServiceData sd = new ServiceData();
			sd.setServiceKey(serviceName);
			
		    serviceDataList.add(sd);
		}
		
		mav.addObject("serviceDataList", serviceDataList);
		mav.setViewName("/index_VIEW_Normal");
		
		return mav;
	}

	private Map<String,ServiceRequestService> services;
	public void setServices(Map<String, ServiceRequestService> services) {
		this.services = services;
	}
	
}
