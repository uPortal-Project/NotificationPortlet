package org.jasig.portlet.notice.mvc.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.jasig.portlet.notice.servicerequests.NotificationRequest;
import org.jasig.portlet.notice.NotificationData;
import org.jasig.portlet.notice.servicerequests.iface.NotificationRequestService;

@Controller
public class DataController {

	private static Log log = LogFactory.getLog(DataController.class);
	
	@RequestMapping("/data")
	public String getData(@RequestParam("key") String key, HttpServletRequest request, ModelMap model) {
		log.debug("getData");

		HttpSession session = request.getSession();
		Map<String, String> userData = (Map<String, String>) session.getAttribute("myUserInfo");

		String umanPersonID = userData.get("umanPersonID");
		String username = userData.get("username");
		
		String serviceName = key;
		NotificationRequestService service = services.get(key); 

		NotificationData sd = new NotificationData();
		sd.setServiceKey(serviceName);
			
		List<NotificationRequest> requests = service.getCurrentRequests(umanPersonID, username);
			
	    Map<String,String> columns = additionalInformation.get(serviceName);

	    for(Map.Entry<String, String> e : columns.entrySet()) { // move horizontally across the table columns
	        String theProperty = e.getKey();
	        String theTitle = e.getValue();
	        sd.addColumnToHeaderRow(theTitle); // setting the title TH
	    }
		    
	    for(NotificationRequest r: requests) { // for each request
	    	List<String> td = new ArrayList<String>(); // add row to table
	    	
	    	Map<String,String> map = r.toMap();
    		
	    	for(Map.Entry<String, String> e : columns.entrySet()) { // move horizontally across the table columns
	    		String theProperty = e.getKey();
	    		String theTitle = e.getValue();
	    		
	    		td.add(map.get(theProperty));
	    	}
	    	
	    	sd.addDataRow(td);
	    }
		
		model.put("serviceData", sd);
		
		model.put("namespace", request.getParameter("namespace"));
		return "/data_AJAX";
	}

	
	private Map<String,NotificationRequestService> services;
	public void setServices(Map<String, NotificationRequestService> services) {
		this.services = services;
	}
	
	private Map<String,Map<String,String>> additionalInformation;
	public void setAdditionalInformation(Map<String,Map<String,String>> additionalInformation) {
		this.additionalInformation = additionalInformation;
	}
}
