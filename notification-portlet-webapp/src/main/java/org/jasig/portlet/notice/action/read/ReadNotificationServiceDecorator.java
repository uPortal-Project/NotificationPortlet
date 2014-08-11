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

package org.jasig.portlet.notice.action.read;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.jasig.portlet.notice.NotificationAttribute;
import org.jasig.portlet.notice.NotificationCategory;
import org.jasig.portlet.notice.NotificationEntry;
import org.jasig.portlet.notice.NotificationResponse;

public class ReadNotificationServiceDecorator implements INotificationService {

    public static final String READ_ENABLED_PREFERENCE = "ReadNotificationServiceDecorator.enabled";
    public static final String DEFAULT_READ_BEHAVIOR = "false";  // The feature is disabled by default
    public static final String READ_ATTRIBUTE_NAME="READ";

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
        if (!readEnabled(req)) {
            return enclosedNotificationService.fetch(req);
        }

        // Build a fresh NotificationResponse based on a deep-copy of the one we enclose
        final NotificationResponse sourceResponse = enclosedNotificationService.fetch(req);
        NotificationResponse rslt = sourceResponse.cloneIfNotCloned();

        final Set<String> readNotificationIds = ReadAction.READ.getReadNotices(req);
        Set<String> potentiallyMissingIds = new HashSet<String>(readNotificationIds);

        NotificationAttribute readAttribute = new NotificationAttribute();
        readAttribute.setName(READ_ATTRIBUTE_NAME);
        readAttribute.setValues(new ArrayList<String>(Arrays.asList((new Boolean(true)).toString())));
        
        // Add and implement the read behavior with our copy
        for (NotificationCategory category : rslt.getCategories()) {

            for (NotificationEntry entry : category.getEntries()) {

                final List<NotificationAction> currentList = entry.getAvailableActions();

                /*
                 * There are 2 requirements for an entry to be decorated with Read behavior:
                 * 
                 *   - (1) It must have an id set
                 *   - (2) It must not have a ReadAction already
                 */
                if (StringUtils.isNotBlank(entry.getId())) {
                    // If the id is in the reads list, set read=true and remove the ID from the potentially
                    // missing set.
                    if (readNotificationIds.contains(entry.getId())) {
                        
                        List<NotificationAttribute> attributes = new ArrayList<NotificationAttribute>(entry.getAttributes());
                        attributes.add(readAttribute);
                        entry.setAttributes(attributes);
                        potentiallyMissingIds.remove(entry.getId());
                    }
                    if (!currentList.contains(ReadAction.READ)) {
                        final List<NotificationAction> replacementList = new ArrayList<NotificationAction>(currentList);
                        boolean isMarkedRead = entry.getAttributes().contains(readAttribute);
                        replacementList.add(!isMarkedRead ?
                                ReadAction.createReadInstance() : ReadAction.createUnReadInstance());
                        entry.setAvailableActions(replacementList);
                    }
                }
            }
        }

        // If there were no errors from the sources and there were read IDs that were not in the results, remove
        // them so we don't have them build up over time if the user doesn't un-read an item that goes away.
        if (rslt.getErrors().isEmpty() && potentiallyMissingIds.size() > 0) {
            if (log.isDebugEnabled()) {
                log.debug("Removing " + potentiallyMissingIds.size() + " unreferenced reads for user "
                        + req.getRemoteUser());
            }
            ReadAction.READ.removeReadNotices(req, potentiallyMissingIds);
        }
        return rslt;

    }

    private boolean readEnabled(PortletRequest request) {
        PortletPreferences prefs = request.getPreferences();
        return Boolean.valueOf(prefs.getValue(READ_ENABLED_PREFERENCE, DEFAULT_READ_BEHAVIOR));
    }

    @Override
    public boolean isValid(PortletRequest req, NotificationResponse previousResponse) {
        return enclosedNotificationService.isValid(req, previousResponse);
    }

}
