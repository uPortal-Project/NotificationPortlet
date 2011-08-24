package org.jasig.portlet.notice.mvc.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("VIEW")
public class NotificationController {

	private Log log = LogFactory.getLog(getClass());

	@RequestMapping
	public String showNotificationsList() {
	    log.trace("In showNotificationsList");
		return "notificationsList";
	}
	
}
