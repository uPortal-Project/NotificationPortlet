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

package org.jasig.portlet.notice.service;

import java.io.File;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.portlet.PortletRequest;

import org.apache.commons.io.FileUtils;
import org.jasig.portlet.notice.NotificationCategory;
import org.jasig.portlet.notice.NotificationEntry;
import org.jasig.portlet.notice.NotificationResponse;
import org.springframework.beans.factory.annotation.Required;

/**
 * This is a simple demo service provider. It reads data from
 * a file and returns it.
 */
public class DemoNotificationService extends AbstractNotificationService {
    
    private static final int MIN_DAY_DELTA = 1;
    private static final int MAX_DAY_DELTA = 14;
    private static final int BLUE_SHIFT = -7;

    private String demoFilename;

	/**
	 * Set the filename of the demo data.
	 * @param filename Location of the demo data file
	 */
    @Required
	public void setFilename(String filename) {
		demoFilename = filename;
	}

    @Override
    public NotificationResponse getNotifications(PortletRequest req, boolean refresh) {
        return readFromFile(demoFilename);
    }
    
    /*
     * Implementation
     */

    /**
     * Deserialize the given JSON formatted file back into a object.
     *
     * @param filename The path and name of the file to be read.
     * @return NotificationRequest, null if the de-serialization fails.
     */
    private NotificationResponse readFromFile(String filename) {
        
        URL location = getClass().getClassLoader().getResource(filename);
        
        if (location == null) {
            String msg = "Demo file not found:  " + filename;
            throw new RuntimeException(msg);
        }
        
        NotificationResponse rslt = null;

        try {
            File f = new File(location.toURI());
            String json = FileUtils.readFileToString(f, "UTF-8");
            rslt =  NotificationResponse.fromJson(json);
        } catch(Exception e) {
            String msg = "Failed to read the demo data file:  " + location;
            throw new RuntimeException(msg);
        }
        
        // A dash of post-processing:  let's make all the due dates at or near today
        for (NotificationCategory nc : rslt.getCategories()) {
            for (NotificationEntry y : nc.getEntries()) {
                if (y.getDueDate() != null) {
                    // Just manipulate the ones that actually have 
                    // a due date;  leave the others blank
                    y.setDueDate(generateRandomDueDate());
                }
            }
        }
        
        return rslt;

    }
    
    /*
     * Implementation
     */

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
