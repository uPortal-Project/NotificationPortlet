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

import org.jasig.portlet.notice.response.NotificationResponse;
import org.jasig.portlet.notice.service.iface.INotificationService;
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
		
		Map<String, String> userInfo = new HashMap<String, String>();
		userInfo.put("id", "demo");
        NotificationResponse notifications = aggregationService.getNotifications(userInfo);
       
        byte[] jsonData = notifications.toJson().getBytes();
        response.setContentType("application/json;charset=UTF-8");
        response.setContentLength(jsonData.length);
        
        InputStream stream = new ByteArrayInputStream(jsonData);
	
        OutputStream output = response.getOutputStream();
        OutputStreamWriter out = new OutputStreamWriter(output , "UTF-8");
        
        IOUtils.copy(stream, output);
        
        out.flush();
        out.close();
	}
    
	public void setAjaxPortletSupportService(AjaxPortletSupportService ajaxPortletSupportService) {
        this.ajaxPortletSupportService = ajaxPortletSupportService;
    }
	
	private INotificationService aggregationService;
	@Autowired
	public void setNotificationService(INotificationService aggregationService) {
		this.aggregationService = aggregationService;
	}
}
