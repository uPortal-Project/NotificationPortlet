package org.jasig.portlet.notice.mvc.controller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.ModelAndView;

import org.jasig.portlet.notice.NotificationData;
import org.jasig.portlet.notice.serviceresponse.NotificationResponse;
import org.jasig.portlet.notice.serviceresponse.iface.NotificationResponseService;
import org.jasig.web.service.AjaxPortletSupportService;

@Controller
@RequestMapping("/data")
public class DataController {

	private static Log log = LogFactory.getLog(DataController.class);
	
	@Autowired(required=true)
    private AjaxPortletSupportService ajaxPortletSupportService;
	
	@RequestMapping(method = RequestMethod.GET)
	public void getData( HttpServletRequest request, HttpServletResponse response) throws Exception {
	    // RequestParam("key") String key, HttpServletRequest request, ModelMap model
		log.debug("getData");
		
	        //test code to be moved to DataController when it is working
	        NotificationResponse masterResponse = new NotificationResponse();
	        for(NotificationResponseService notificationService: notificationServices) {
	            List<NotificationResponse> responses = notificationService.getCurrentResponses("id", "testuser");
	    
	            for(NotificationResponse r: responses) { // for each request
	                masterResponse.addResponseData(r);
	            }
	        }
        byte[] jsonData = masterResponse.toJson().getBytes();
	        response.setContentType("application/json;charset=UTF-8");
	        response.setContentLength(jsonData.length);
	        
	    InputStream stream = new ByteArrayInputStream(jsonData);
		
	        OutputStream output = response.getOutputStream();
	        OutputStreamWriter out = new OutputStreamWriter(output , "UTF-8");
	        
	        IOUtils.copy(stream, output);
	        
	        out.flush();
	        out.close();
		
		
		
		
		//Map model = ajaxPortletSupportService.getAjaxModel(request, response);
		/*
		String key = new String("");
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
		*/
		//model.put("namespace", request.getParameter("namespace"));
		//return new ModelAndView("jsonView", model);
	}
    
	public void setAjaxPortletSupportService(AjaxPortletSupportService ajaxPortletSupportService) {
        this.ajaxPortletSupportService = ajaxPortletSupportService;
    }
	
	private List<NotificationResponseService> notificationServices;
	@Autowired
	public void setNotificationServices(List<NotificationResponseService> notificationServices) {
		this.notificationServices = notificationServices;
	}
	
	private Map<String,Map<String,String>> additionalInformation;
	public void setAdditionalInformation(Map<String,Map<String,String>> additionalInformation) {
		this.additionalInformation = additionalInformation;
	}
}
