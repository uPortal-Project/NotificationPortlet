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

import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
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
import org.jasig.portlet.notice.NotificationResponse;
import org.jasig.portlet.notice.service.AbstractNotificationService;
import org.jasig.portlet.notice.util.UsernameFinder;
import org.springframework.beans.factory.annotation.Autowired;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class RomeNotificationService extends AbstractNotificationService {

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
    public NotificationResponse getNotifications(PortletRequest req, boolean refresh) {
        
        final List<NotificationCategory> categories = new ArrayList<NotificationCategory>();

        final PortletPreferences prefs = req.getPreferences();
        final String[] urls = prefs.getValues(URLS_PREFERENCE, new String[0]);
        for (String item : urls) {
            
            if (!refresh) {
                // It's okay to pull a response from cache, if we have one
                Element m = cache.get(item);
                if (m != null) {
                    if (log.isDebugEnabled()) {
                        log.debug("Feed cache HIT for url:  " + item);
                    }
                    NotificationCategory category = (NotificationCategory) m.getObjectValue();
                    categories.add(category);
                    continue; // move on to the next feed url
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("Feed cache MISS for url:  " + item);
                    }
                }
            }
            
            if (log.isDebugEnabled()) {
                log.debug("Checking the following feed URL for notifications for user '" 
                                + usernameFinder.findUsername(req) + "' -- " + item);
            }
            
            try {

                final URL url = new URL(item);
                
                final XmlReader reader = new XmlReader(url);
                final SyndFeedInput input = new SyndFeedInput();
                final SyndFeed feed = input.build(reader);
                
                final NotificationCategory category = new NotificationCategory();
                category.setTitle(feed.getTitle());
                
                final List<TimestampNotificationEntry> entries = new ArrayList<TimestampNotificationEntry>();
                
                @SuppressWarnings("unchecked")
                final List<SyndEntry> list = feed.getEntries(); 
                for (final SyndEntry y : list) {
                    
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
                    StringBuilder body = new StringBuilder();
                    final SyndContent desc = y.getDescription();
                    if (desc != null) {
                        // Prefer description
                        body.append(desc.getValue());
                    }
                    if (body.length() == 0) {
                        // Fall back to contents
                        @SuppressWarnings("unchecked")
                        final List<SyndContent> contents = y.getContents();
                        for (final SyndContent c : contents) {
                            body.append(c.getValue());
                        }
                    }
                    if (body.length() == 0) {
                        // Last resort... 
                        body.append(NO_FURTHER_INFORMATION);
                    }
                    entry.setBody(body.toString());

                    // Attributes -- TODO:  These labels should be internationalized in messages.properties
                    List<NotificationAttribute> attributes = new ArrayList<NotificationAttribute>();
                    String author = y.getAuthor();
                    if (StringUtils.isNotBlank(author)) {
                        attributes.add(new NotificationAttribute("Author", author));
                    }
                    Date publishedDate = y.getPublishedDate();
                    if (publishedDate != null) {
                        attributes.add(new NotificationAttribute("Published date", DATE_FORMAT.format(publishedDate)));
                    }
                    Date updatededDate = y.getUpdatedDate();
                    if (updatededDate != null) {
                        attributes.add(new NotificationAttribute("Updated date", DATE_FORMAT.format(updatededDate)));
                    }
                    entry.setAttributes(attributes);
                    
                    entries.add(entry);

                }
                
                // Items should be in reverse chronological order
                Collections.sort(entries);
                Collections.reverse(entries);
                
                category.setEntries(new ArrayList<NotificationEntry>(entries));
                cache.put(new Element(item, category));
                categories.add(category);

            } catch (Exception e) {
                String msg = "Unable to read the specified feed:  " + item;
                log.error(msg, e);
            }

        }
        
        NotificationResponse rslt = new NotificationResponse();
        rslt.setCategories(categories);
        return rslt;

    }

}
