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

package org.jasig.portlet.notice.service.classloader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.ResourceRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.notice.NotificationCategory;
import org.jasig.portlet.notice.NotificationEntry;
import org.jasig.portlet.notice.NotificationResponse;

/**
 * This is a simple demo service provider that reads notifications from a file.  
 * It provides a <code>setActive(boolean)</code> method (default is true) for 
 * toggling it on and off at runtime.
 */
public final class DemoNotificationService extends ClassLoaderResourceNotificationService {
    
    public static final String LOCATIONS_PREFERENCE = "DemoNotificationService.locations";

    private static final int MIN_DAY_DELTA = 1;
    private static final int MAX_DAY_DELTA = 14;
    private static final int BLUE_SHIFT = -7;

    private final NotificationResponse EMPTY_RESPONSE = new NotificationResponse();
    private boolean active = true;  // Default
    private final Log log = LogFactory.getLog(getClass());
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public boolean isActive() {
        return active;
    }

    @Override
    public NotificationResponse fetch(ResourceRequest req) {
        
        NotificationResponse rslt = EMPTY_RESPONSE;  // default
        
        // Are we active?
        if (active) {

            log.debug("Sending a non-empty response because we are ACTIVE");

            rslt = super.fetch(req);
            
            log.debug("Number of notification categories are: " + rslt.getCategories().size() );
            
            // A dash of post-processing:  let's make all the due dates at or near today
            for (NotificationCategory nc : rslt.getCategories()) {
            	log.debug("Notification category is " + nc.getTitle() );
            	log.debug("Number of notifications for this category are " + nc.getEntries().size() );
                for (NotificationEntry y : nc.getEntries()) {
                    if (y.getDueDate() != null) {
                        // Just manipulate the ones that actually have 
                        // a due date;  leave the others blank
                        y.setDueDate(generateRandomDueDate());
                    }
                    log.debug("Notification: " + y.getTitle() );
                }
            }

        } else {
            log.debug("Sending an empty response because we are INACTIVE");
        }

        return rslt;

    }

    /*
     * Implementation
     */
    
    @Override
    protected ArrayList<String> getLocations(PortletRequest req) {
        final PortletPreferences prefs = req.getPreferences();
        final String[] locations = prefs.getValues(LOCATIONS_PREFERENCE, new String[0]);
        final ArrayList<String> rslt = new ArrayList<String>(Arrays.asList(locations));
        return rslt;
    }

    private Date generateRandomDueDate() {
        int randomDelta = MIN_DAY_DELTA 
                + (int)(Math.random() * ((MAX_DAY_DELTA - MIN_DAY_DELTA) + 1))
                + BLUE_SHIFT;  // Puts some dates in the past, some in the future
        Calendar rslt = new GregorianCalendar();
        rslt.setTimeInMillis(System.currentTimeMillis());
        rslt.add(Calendar.DATE, randomDelta);
        return rslt.getTime();
    }

}
