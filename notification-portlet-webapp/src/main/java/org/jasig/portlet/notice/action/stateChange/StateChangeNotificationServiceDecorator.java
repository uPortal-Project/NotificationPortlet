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

package org.jasig.portlet.notice.action.stateChange;

import java.util.ArrayList;
import java.util.List;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.notice.INotificationService;
import org.jasig.portlet.notice.NotificationAction;
import org.jasig.portlet.notice.NotificationCategory;
import org.jasig.portlet.notice.NotificationEntry;
import org.jasig.portlet.notice.NotificationResponse;

/**
 *
 * @author mglazier
 */
public class StateChangeNotificationServiceDecorator implements INotificationService {

	public static final String STATE_CHANGE_ENABLED_PREFERENCE = "StateChangeNotificationServiceDecorator.enabled";
    public static final String DEFAULT_STATE_CHANGE_BEHAVIOR = "false";  // The feature is disabled by default

    // Instance members
    private INotificationService enclosedNotificationService;
    private final Log log = LogFactory.getLog(getClass());
	
	public void setEnclosedNotificationService(INotificationService enclosedNotificationService) {
        this.enclosedNotificationService = enclosedNotificationService;
    }
	
	@Override
	public String getName() {
		return enclosedNotificationService.getName();	
	}

	@Override
	public void invoke(ActionRequest req, ActionResponse res, boolean refresh) {
		enclosedNotificationService.invoke(req, res, refresh);
	}

	@Override
	public void collect(EventRequest req, EventResponse res) {
		enclosedNotificationService.collect(req, res);
	}

	@Override
	public NotificationResponse fetch(PortletRequest req) {
		// Just pass through the enclosed collection if this feature is disabled
        if (!stateChangeEnabled(req)) {
            return enclosedNotificationService.fetch(req);
        }
		
		// Build a fresh NotificationResponse based on a deep-copy of the one we enclose
        final NotificationResponse sourceResponse = enclosedNotificationService.fetch(req);
        NotificationResponse rslt = sourceResponse.cloneIfNotCloned();
		
        for (NotificationCategory category : rslt.getCategories()) {
            for (NotificationEntry entry : category.getEntries()) {
				// only decorate if there is a URL
				if (StringUtils.isNotBlank(entry.getUrl())) {
					final List<NotificationAction> currentList = entry.getAvailableActions();

					if (StringUtils.isNotBlank(entry.getId())) {
						final List<NotificationAction> replacementList = new ArrayList<>(currentList);
						replacementList.add( new StateChangeAction());
						entry.setAvailableActions( replacementList);
					}
				}
            }
        }
		return rslt;
	}

	@Override
	public boolean isValid(PortletRequest req, NotificationResponse previousResponse) {
		return enclosedNotificationService.isValid(req, previousResponse);
	}
	
	private boolean stateChangeEnabled(PortletRequest request) {
        PortletPreferences prefs = request.getPreferences();
        return Boolean.valueOf(prefs.getValue(STATE_CHANGE_ENABLED_PREFERENCE, DEFAULT_STATE_CHANGE_BEHAVIOR));
    }
}
