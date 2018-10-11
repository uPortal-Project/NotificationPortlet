/*
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

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jasig.portlet.notice.NotificationResponse;
import org.jasig.portlet.notice.service.AbstractNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 * This is a simple service provider that reads notifications from one or more 
 * files in the classpath.  It's a handy way to implement, for example, an 
 * online reference or FAQ using the Notification portlet.  It also serves as 
 * the basis for the {@link DemoNotificationService}.
 */
public class ClassLoaderResourceNotificationService extends AbstractNotificationService {

    public static final String LOCATIONS_PREFERENCE = "ClassLoaderResourceNotificationService.locations";

    /**
     * Classpath locations defined in an external file, in the post-Portlet API style.
     * Comma-separated list.
     */
    @Value("${ClassLoaderResourceNotificationService.locations:}")
    private String locationsProperty;

    private List<String> locationsList = Collections.emptyList();
    private final ObjectMapper mapper = new ObjectMapper();
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Cache cache;

    @Resource(name="ClassLoaderResourceNotificationService.responseCache")
    public void setCache(Cache cache) {
        this.cache = cache;
    }

    @PostConstruct
    public void init() {
        if (!StringUtils.isEmpty(locationsProperty)) {
            locationsList =
                    Collections.unmodifiableList(Arrays.asList(locationsProperty.split(",")));
        }
    }

    @Override
    public NotificationResponse fetch(PortletRequest req) {
        
        final List<String> locations = getLocations(req);
        return fetchFromClasspath(locations);

    }

    @Override
    public final boolean isValid(PortletRequest req, NotificationResponse previousResponse) {
        /*
         * This service impl privately caches responses across users and for 
         * longer periods, so there's no harm in calling fetch() whenever a 
         * response is needed.
         */
        return false;
    }

    @Override
    public NotificationResponse fetch(HttpServletRequest request) {
        final List<String> locations = getLocations(request);
        return fetchFromClasspath(locations);
    }

    /*
     * Implementation
     */

    private NotificationResponse fetchFromClasspath(List<String> locations) {

        if (locations.isEmpty()) {
            return NotificationResponse.EMPTY_RESPONSE;
        }

        NotificationResponse rslt;

        final Element m = cache.get(locations);
        if (m != null) {
            // ## CACHE HIT ##
            rslt = (NotificationResponse) m.getObjectValue();
            logger.debug("Locations cache HIT for collection {};  size={}", locations, rslt.size());
        } else {
            // ## CACHE MISS ##
            rslt = new NotificationResponse();
            for (String loc : locations) {
                final NotificationResponse response = readFromFile(loc);
                rslt = rslt.combine(response);
            }
            logger.debug("Locations cache MISS for collection {};  size={}", locations, rslt.size());
            cache.put(new Element(locations, rslt));
        }

        return rslt;

    }

    @Deprecated
    protected List<String> getLocations(PortletRequest req) {
        final PortletPreferences prefs = req.getPreferences();
        final String[] locations = prefs.getValues(LOCATIONS_PREFERENCE, new String[0]);
        final ArrayList<String> rslt = new ArrayList<>(Arrays.asList(locations));
        return rslt;
    }

    /**
     * Access to the list of locations is wrapped in a method so that subclasses can override it.
     */
    protected List<String> getLocations(HttpServletRequest req) {
        return locationsList;
    }

    /**
     * Deserialize the given JSON formatted file back into a object.
     *
     * @param filename The path and name of the file to be read.
     * @return NotificationRequest, null if the de-serialization fails.
     */
    private NotificationResponse readFromFile(String filename) {
        
        NotificationResponse rslt;

        logger.debug("Preparing to read from file:  {}", filename);

        URL location = getClass().getClassLoader().getResource(filename);
        if (location != null) {
            try {
                File f = new File(location.toURI());
                rslt =  mapper.readValue(f, NotificationResponse.class);
            } catch (Exception e) {
                String msg = "Failed to read the data file:  " + location;
                logger.error(msg, e);
                rslt = prepareErrorResponse(getName(), msg);
            }
        } else {
            String msg = "Data file not found:  " + filename;
            rslt = prepareErrorResponse(getName(), msg);
        }

        return rslt;

    }

}
