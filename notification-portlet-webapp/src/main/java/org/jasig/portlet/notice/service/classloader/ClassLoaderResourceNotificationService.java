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
import java.util.ArrayList;
import java.util.Arrays;

import javax.annotation.Resource;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.ResourceRequest;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.jasig.portlet.notice.NotificationResponse;
import org.jasig.portlet.notice.service.AbstractNotificationService;

/**
 * This is a simple service provider that reads notifications from one or more 
 * files in the classpath.  It's a handy way to implement, for example, an 
 * online reference or FAQ using the Notification portlet.  It also serves as 
 * the basis for the {@link DemoNotificationService}.
 */
public class ClassLoaderResourceNotificationService extends AbstractNotificationService {

    public static final String LOCATIONS_PREFERENCE = "ClassLoaderResourceNotificationService.locations";
    
    private static final NotificationResponse EMPTY_RESPONSE = new NotificationResponse();

    private final ObjectMapper mapper = new ObjectMapper();
    private final Log log = LogFactory.getLog(getClass());

    private Cache cache;

    @Resource(name="ClassLoaderResourceNotificationService.responseCache")
    public void setCache(Cache cache) {
        this.cache = cache;
    }

    @Override
    public NotificationResponse fetch(ResourceRequest req) {
        
        final ArrayList<String> locations = getLocations(req);
        
        if (locations.isEmpty()) {
        	log.debug("Locations is empty.");
            return EMPTY_RESPONSE;
        }

        NotificationResponse rslt = null;

        final Element m = cache.get(locations);
        if (m != null) {
            // ## CACHE HIT ##
            if (log.isDebugEnabled()) {
                log.debug("Locations cache HIT for collection:  " + locations);
            }
            rslt = (NotificationResponse) m.getObjectValue();
        } else {
            // ## CACHE MISS ##
            if (log.isDebugEnabled()) {
                log.debug("Locations cache MISS for collection:  " + locations);
            }
            rslt = new NotificationResponse();
            for (String loc : locations) {
                final NotificationResponse response = readFromFile(loc); 
                rslt = rslt.combine(response);
            }
            cache.put(new Element(locations, rslt));
        }
        
        return rslt;

    }

    @Override
    public final boolean isValid(ResourceRequest req, NotificationResponse previousResponse) {
        /*
         * This service impl privately caches responses across users and for 
         * longer periods, so there's no harm in calling fetch() whenever a 
         * response is needed.
         */
        return false;
    }

    /*
     * Implementation
     */
    
    /**
     * Returns a specific List impl (ArrayList) because it implements Serializable.
     */
    protected ArrayList<String> getLocations(PortletRequest req) {
        final PortletPreferences prefs = req.getPreferences();
        final String[] locations = prefs.getValues(LOCATIONS_PREFERENCE, new String[0]);
        final ArrayList<String> rslt = new ArrayList<String>(Arrays.asList(locations));
        return rslt;
    }

    /**
     * Deserialize the given JSON formatted file back into a object.
     *
     * @param filename The path and name of the file to be read.
     * @return NotificationRequest, null if the de-serialization fails.
     */
    private NotificationResponse readFromFile(String filename) {
    	
    	log.debug("Reading notifications in file named: " + filename);
        
        NotificationResponse rslt = null;

        URL location = getClass().getClassLoader().getResource(filename);
        if (location != null) {
            try {
                File f = new File(location.toURI());
                rslt =  mapper.readValue(f, NotificationResponse.class);
            } catch (Exception e) {
                String msg = "Failed to read the data file:  " + location;
                log.error(msg, e);
                rslt = prepareErrorResponse(getName(), msg);
            }
        } else {
            String msg = "Data file not found:  " + filename;
            rslt = prepareErrorResponse(getName(), msg);
        }

        return rslt;

    }

}
