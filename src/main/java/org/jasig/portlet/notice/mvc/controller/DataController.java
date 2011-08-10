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

import org.jasig.portlet.notice.NotificationData;
import org.jasig.portlet.notice.serviceresponse.NotificationResponse;
import org.jasig.portlet.notice.serviceresponse.iface.NotificationResponseService;

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
		NotificationResponseService service = services.get(key); 

		NotificationData sd = new NotificationData();
		sd.setServiceKey(serviceName);
			
		List<NotificationResponse> responses = service.getCurrentResponses(umanPersonID, username);
			
	    Map<String,String> columns = additionalInformation.get(serviceName);

	    for(Map.Entry<String, String> e : columns.entrySet()) { // move horizontally across the table columns
	        String theProperty = e.getKey();
	        String theTitle = e.getValue();
	        sd.addColumnToHeaderRow(theTitle); // setting the title TH
	    }
		    
	    for(NotificationResponse r: responses) { // for each request
	    	List<Object> td = new ArrayList<Object>(); // add row to table
	    	
	    	Map<String,Object> map = r.toMap();
    		
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

	
	private Map<String,NotificationResponseService> services;
	public void setServices(Map<String, NotificationResponseService> services) {
		this.services = services;
	}
	
	private Map<String,Map<String,String>> additionalInformation;
	public void setAdditionalInformation(Map<String,Map<String,String>> additionalInformation) {
		this.additionalInformation = additionalInformation;
	}
}
