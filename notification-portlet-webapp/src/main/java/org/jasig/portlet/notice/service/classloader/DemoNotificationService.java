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

import java.io.File;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.portlet.ResourceRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.jasig.portlet.notice.IInvalidatingNotificationService;
import org.jasig.portlet.notice.NotificationCategory;
import org.jasig.portlet.notice.NotificationEntry;
import org.jasig.portlet.notice.NotificationResponse;
import org.jasig.portlet.notice.service.AbstractNotificationService;
import org.springframework.beans.factory.annotation.Required;

/**
 * This is a simple demo service provider that reads notifications from a file.  
 * It provides both an 'enableDemo' preference (default is false) for toggling 
 * at deploy/publish time, and a <code>setActive()</code> method (default is 
 * true) for toggling at runtime.  The service must be both <i>enabled</i> and 
 * <i>active</i> to function.
 */
public final class DemoNotificationService extends AbstractNotificationService implements IInvalidatingNotificationService {
    
    public static final String ENABLE_DEMO_PREFERENCE = "DemoNotificationService.enableDemo";

    private static final int MIN_DAY_DELTA = 1;
    private static final int MAX_DAY_DELTA = 14;
    private static final int BLUE_SHIFT = -7;

    private final NotificationResponse EMPTY_RESPONSE = new NotificationResponse();
    private NotificationResponse nonemptyResponse = new NotificationResponse();
    private final ObjectMapper mapper = new ObjectMapper();
    private boolean active = true;
    private final Log log = LogFactory.getLog(getClass());

	/**
	 * Set the filename of the demo data.
	 * @param filename Location of the demo data file
	 */
    @Required
	public void setFilename(String filename) {
        nonemptyResponse = readFromFile(filename);
	}
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public boolean isActive() {
        return active;
    }

    @Override
    public NotificationResponse fetch(ResourceRequest req) {
        
        // Are we active?
        if (!active) {
            log.debug("Sending an empty response because we are INACTIVE");
            return EMPTY_RESPONSE;
        }
        
        // Are we enabled?
        String enabled = req.getPreferences().getValue(ENABLE_DEMO_PREFERENCE, "false");
        if (!Boolean.valueOf(enabled)) {
            log.debug("Sending an empty response because we are DISABLED");
            return EMPTY_RESPONSE;
        }
        
        // We are both active & enabled;  go ahead and send the notifications.
        log.debug("Sending a non-empty response because we are both ACTIVE and ENABLED");
        return nonemptyResponse;

    }
    
    @Override
    public boolean isValid(ResourceRequest req, NotificationResponse previousResponse) {
        
        // Assertions.
        if (previousResponse == null) {
            String msg = "Argument 'previousResponse' cannot be null";
            throw new IllegalArgumentException(msg);
        }
        
        final NotificationResponse currentResponse = fetch(req); 
        return previousResponse.equals(currentResponse);
        
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
            rslt =  mapper.readValue(f, NotificationResponse.class);
        } catch (Exception e) {
            String msg = "Failed to read the demo data file:  " + location;
            throw new RuntimeException(msg, e);
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
