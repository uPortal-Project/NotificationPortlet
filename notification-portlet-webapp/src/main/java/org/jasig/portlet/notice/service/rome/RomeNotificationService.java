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

package org.jasig.portlet.notice.service.rome;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.notice.NotificationAttribute;
import org.jasig.portlet.notice.NotificationCategory;
import org.jasig.portlet.notice.NotificationEntry;
import org.jasig.portlet.notice.NotificationError;
import org.jasig.portlet.notice.NotificationResponse;
import org.jasig.portlet.notice.service.AbstractNotificationService;
import org.jasig.portlet.notice.util.UsernameFinder;
import org.springframework.beans.factory.annotation.Autowired;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public final class RomeNotificationService extends AbstractNotificationService {

    public static final String URLS_PREFERENCE = "RomeNotificationService.urls";

    private static final Object NO_FURTHER_INFORMATION = "No further information is available.";
    private static final DateFormat DATE_FORMAT = DateFormat.getDateInstance(DateFormat.SHORT);

    private Cache cache;

    @Autowired
    private UsernameFinder usernameFinder;

    @Resource(name="RomeNotificationService.feedCache")
    public void setCache(Cache cache) {
        this.cache = cache;
    }

    private final Log log = LogFactory.getLog(getClass());

    @Override
    public void invoke(final ActionRequest req, final ActionResponse res, final boolean refresh) {

        if (log.isDebugEnabled()) {
            log.debug("Performing RomeNotificationService.invoke() for user='" 
                                    + usernameFinder.findUsername(req) 
                                    + "' refresh=" + refresh);
        }

        if (refresh) {
            final PortletPreferences prefs = req.getPreferences();
            final String[] urls = prefs.getValues(URLS_PREFERENCE, new String[0]);
            for (String item : urls) {
                cache.remove(item);
            }
        }

    }

    @Override
    public NotificationResponse fetch(final PortletRequest req) {
        
        final List<NotificationCategory> categories = new ArrayList<NotificationCategory>();
        final List<NotificationError> errors = new ArrayList<NotificationError>();

        final PortletPreferences prefs = req.getPreferences();
        final String[] urls = prefs.getValues(URLS_PREFERENCE, new String[0]);
        for (String item : urls) {
            
            // It's okay to pull a response from cache, if we have one, since refresh happens in invoke()
            final Element m = cache.get(item);
            if (m != null) {
                // ## CACHE HIT ##
                if (log.isDebugEnabled()) {
                    log.debug("Feed cache HIT for url:  " + item);
                }
                final NotificationCategory category = (NotificationCategory) m.getObjectValue();
                categories.add(category);
            } else {
                // ## CACHE MISS ##
                if (log.isDebugEnabled()) {
                    log.debug("Feed cache MISS for url:  " + item);
                    log.debug("Checking the following feed URL for notifications for user '" 
                            + usernameFinder.findUsername(req) + "' -- " + item);
                }
                final NotificationCategory category = fetchFromSource(item);
                if (category != null) {
                    cache.put(new Element(item, category));
                    categories.add(category);
                } else {
                    final NotificationError error = new NotificationError();
                    error.setError("Service Unavailable");
                    error.setSource(getName());
                    errors.add(error);
                }
            }

        }

        final NotificationResponse rslt = new NotificationResponse();
        rslt.setCategories(categories);
        rslt.setErrors(errors);
        return rslt;

    }
    
    /*
     * Implementation
     */
    
    private NotificationCategory fetchFromSource(final String url) {
        
        NotificationCategory rslt = null;  // default
        
        XmlReader reader = null;
        try {

            final URL u = new URL(url);
            
            reader = new XmlReader(u);
            final SyndFeedInput input = new SyndFeedInput();
            final SyndFeed feed = input.build(reader);
            
            rslt = new NotificationCategory();
            rslt.setTitle(feed.getTitle());
            
            final List<TimestampNotificationEntry> entries = new ArrayList<TimestampNotificationEntry>();
            
            @SuppressWarnings("unchecked")
            final List<SyndEntry> list = feed.getEntries(); 
            for (SyndEntry y : list) {
                
                if (log.isTraceEnabled()) {
                    log.trace("Processing SyndEntry:  \n" + y.toString());
                }
                
                final long timestamp = y.getPublishedDate().getTime();
                final TimestampNotificationEntry entry = new TimestampNotificationEntry(timestamp);

                // Strongly-typed members
                entry.setSource(feed.getAuthor());
                entry.setTitle(y.getTitle());
                entry.setUrl(y.getLink());
                // No priority
                // No due date
                // No image

                // Body
                final StringBuilder body = new StringBuilder();
                final SyndContent desc = y.getDescription();
                if (desc != null) {
                    // Prefer description
                    body.append(desc.getValue());
                }
                if (body.length() == 0) {
                    // Fall back to contents
                    @SuppressWarnings("unchecked")
                    final List<SyndContent> contents = y.getContents();
                    for (SyndContent c : contents) {
                        body.append(c.getValue());
                    }
                }
                if (body.length() == 0) {
                    // Last resort... 
                    body.append(NO_FURTHER_INFORMATION);
                }
                entry.setBody(body.toString());

                // Attributes -- TODO:  These labels should be internationalized in messages.properties
                final List<NotificationAttribute> attributes = new ArrayList<NotificationAttribute>();
                final String author = y.getAuthor();
                if (StringUtils.isNotBlank(author)) {
                    attributes.add(new NotificationAttribute("Author", author));
                }
                final Date publishedDate = y.getPublishedDate();
                if (publishedDate != null) {
                    attributes.add(new NotificationAttribute("Published date", DATE_FORMAT.format(publishedDate)));
                }
                final Date updatededDate = y.getUpdatedDate();
                if (updatededDate != null) {
                    attributes.add(new NotificationAttribute("Updated date", DATE_FORMAT.format(updatededDate)));
                }
                entry.setAttributes(attributes);
                
                entries.add(entry);

            }
            
            // Items should be in reverse chronological order
            Collections.sort(entries);
            Collections.reverse(entries);
            
            rslt.setEntries(new ArrayList<NotificationEntry>(entries));

        } catch (Exception e) {
            final String msg = "Unable to read the specified feed:  " + url;
            log.error(msg, e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ioe) {
                    final String msg = "Unable to close the XmlReader";
                    log.error(msg, ioe);
                }
            }
        }
        
        return rslt;

    }

}
