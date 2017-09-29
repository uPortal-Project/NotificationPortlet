/**
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.portlet.notice.service.classloader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import org.jasig.portlet.notice.NotificationAttribute;
import org.jasig.portlet.notice.NotificationCategory;
import org.jasig.portlet.notice.NotificationEntry;
import org.jasig.portlet.notice.NotificationResponse;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a simple demo service provider that reads notifications from a file.  
 * It provides a <code>setActive(boolean)</code> method (default is true) for 
 * toggling it on and off at runtime.
 */
public final class DemoNotificationService extends ClassLoaderResourceNotificationService {
    
    public static final String LOCATIONS_PREFERENCE = "DemoNotificationService.locations";

    private static final DateTimeFormatter DATE_PARSER = DateTimeFormat.forPattern("MM/dd/YYYY");

    private static final int MIN_DAY_DELTA = 1;
    private static final int MAX_DAY_DELTA = 14;
    private static final int BLUE_SHIFT = -7;

    private boolean active = true;  // Default
    private final Logger log = LoggerFactory.getLogger(getClass());
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public boolean isActive() {
        return active;
    }

    @Override
    public NotificationResponse fetch(PortletRequest req) {
        
        NotificationResponse rslt = NotificationResponse.EMPTY_RESPONSE;  // default
        
        // Are we active?
        if (active) {
            log.debug("Sending a non-empty response because we are ACTIVE");
            rslt = super.fetch(req);

            // A dash of post-processing to make the demo data more relevant.

            // Calculate days adjustment factor for all student jobs date values.  Use number of days from from
            // 05/15/2014 to the current date.
            int jobDaysAdjustment = (int) new Duration(new LocalDate(2014, 5, 15).toDateTimeAtStartOfDay(),
                    new LocalDate().toDateTimeAtStartOfDay()).getStandardDays();
            
            for (NotificationCategory nc : rslt.getCategories()) {
                for (NotificationEntry y : nc.getEntries()) {

                    // Make all the due dates at or near today
                    if (y.getDueDate() != null) {
                        // Just manipulate the ones that actually have 
                        // a due date;  leave the others blank
                        y.setDueDate(generateRandomDueDate());
                    }

                    // For student jobs demo data, set relevant postDate, dateClosed, and startDate values based on
                    // current data.
                    updateDateAttributeIfPresent(y.getAttributes(), "postDate", jobDaysAdjustment);
                    updateDateAttributeIfPresent(y.getAttributes(), "dateClosed", jobDaysAdjustment);
                    updateDateAttributeIfPresent(y.getAttributes(), "startDate", jobDaysAdjustment);
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
        return new ArrayList<String>(Arrays.asList(locations));
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

    private void updateDateAttributeIfPresent(List<NotificationAttribute> attributeList, String attributeName,
                                              int addDays) {
        for (NotificationAttribute attr : attributeList) {
            if (attr.getName().equals(attributeName)) {
                if (attr.getValues().size() == 1) {
                    // Allow parse errors to throw exception and stop the data file processing
                    LocalDate date = DATE_PARSER.parseLocalDate(attr.getValues().get(0));
                    attr.setValues(Arrays.asList(new String[]
                            { DATE_PARSER.print(date.plusDays(addDays).toDateTimeAtStartOfDay())}));
                } else if (attr.getValues().size() > 1) {
                    log.warn("Sample data for Notification Attribute {} has {} values; considering only 1st value",
                            attr.getName(), attr.getValues().size());
                } else {
                    log.warn("Sample data for Notification Attribute {} has no values");
                }
                String value = attr.getValues().size() > 0 ? attr.getValues().get(0) : "";
                attr.setValues(Arrays.asList(new String[] {value}));
            }
        }
    }

}
