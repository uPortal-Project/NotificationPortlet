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
package org.jasig.portlet.notice.service.rome;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.commons.lang3.StringUtils;
import org.jasig.portlet.notice.NotificationAttribute;
import org.jasig.portlet.notice.NotificationCategory;
import org.jasig.portlet.notice.NotificationError;
import org.jasig.portlet.notice.NotificationResponse;
import org.jasig.portlet.notice.service.AbstractNotificationService;
import org.jasig.portlet.notice.util.UsernameFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;

public final class RomeNotificationService extends AbstractNotificationService {

    private static final String URLS_PREFERENCE = "RomeNotificationService.urls";

    private static final Locale locale = Locale.getDefault(Locale.Category.FORMAT);
    private static DateFormat DATE_FORMAT = DateFormat.getDateInstance(DateFormat.SHORT, locale);

    @Autowired
    private MessageSource messages;

    /**
     * Service URLs defined in an external file, in the post-Portlet API style.
     * Comma-separated list.
     */
    @Value("${RomeNotificationService.feedUrls:}")
    private String feedUrlsProperty;

    private List<String> feedUrlsList = Collections.emptyList();

    private Cache cache;

    @Autowired
    private UsernameFinder usernameFinder;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource(name="RomeNotificationService.feedCache")
    public void setCache(Cache cache) {
        this.cache = cache;
    }

    @PostConstruct
    public void init() {
        final String dateFormatString = messages.getMessage("notice.date.format", null, locale);
        logger.info("locale: {}", locale);
        logger.info("date format: {}", dateFormatString);
        DATE_FORMAT = new SimpleDateFormat(dateFormatString);
        if (!StringUtils.isEmpty(feedUrlsProperty)) {
            feedUrlsList =
                    Collections.unmodifiableList(Arrays.asList(feedUrlsProperty.split(",")));
        }
    }

    @Override
    public void invoke(final ActionRequest req, final ActionResponse res, final boolean refresh) {

        if (logger.isDebugEnabled()) {
            logger.debug("Performing RomeNotificationService.invoke() for user='"
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

        final PortletPreferences prefs = req.getPreferences();
        final String[] feedUrls = prefs.getValues(URLS_PREFERENCE, new String[0]);
        final String username = usernameFinder.findUsername(req);
        return doFetch(Arrays.asList(feedUrls), username);

    }

    @Override
    public NotificationResponse fetch(HttpServletRequest req) {

        final String username = usernameFinder.findUsername(req);
        return doFetch(feedUrlsList, username);

    }

    /*
     * Implementation
     */

    private NotificationResponse doFetch(List<String> feedUrls, String username) {

        final List<NotificationCategory> categories = new ArrayList<>();
        final List<NotificationError> errors = new ArrayList<>();

        for (String item : feedUrls) {

            // It's okay to pull a response from cache, if we have one, since refresh happens in invoke()
            final Element m = cache.get(item);
            if (m != null) {
                // ## CACHE HIT ##
                if (logger.isDebugEnabled()) {
                    logger.debug("Feed cache HIT for url:  " + item);
                }
                final NotificationCategory category = (NotificationCategory) m.getObjectValue();
                categories.add(category);
            } else {
                // ## CACHE MISS ##
                if (logger.isDebugEnabled()) {
                    logger.debug("Feed cache MISS for url:  " + item);
                    logger.debug("Checking the following feed URL for notifications for user '"
                            + username + "' -- " + item);
                }
                final NotificationCategory category = fetchFromSourceUrl(item);
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

    private NotificationCategory fetchFromSourceUrl(final String url) {

        NotificationCategory rslt = null;  // default

        XmlReader reader = null;
        try {

            final URL u = new URL(url);

            reader = new XmlReader(u);
            final SyndFeedInput input = new SyndFeedInput();
            final SyndFeed feed = input.build(reader);

            rslt = new NotificationCategory();
            rslt.setTitle(feed.getTitle());

            final List<TimestampNotificationEntry> entries = new ArrayList<>();

            @SuppressWarnings("unchecked")
            final List<SyndEntry> list = feed.getEntries();
            for (SyndEntry y : list) {

                if (logger.isTraceEnabled()) {
                    logger.trace("Processing SyndEntry:  \n" + y.toString());
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
                    final String noInfo = messages.getMessage("notice.noinfo", null, locale);
                    body.append(noInfo);
                }
                entry.setBody(body.toString());

                // Attributes -- TODO:  These labels should be internationalized in messages.properties
                final List<NotificationAttribute> attributes = new ArrayList<>();
                final String author = y.getAuthor();
                if (StringUtils.isNotBlank(author)) {
                    final String authorLabel = messages.getMessage("notice.author", null, locale);
                    attributes.add(new NotificationAttribute(authorLabel, author));
                }
                final Date publishedDate = y.getPublishedDate();
                if (publishedDate != null) {
                    final String dateLabel = messages.getMessage("notice.date.published", null, locale);
                    attributes.add(new NotificationAttribute(dateLabel, DATE_FORMAT.format(publishedDate)));
                }
                final Date updatededDate = y.getUpdatedDate();
                if (updatededDate != null) {
                    final String dateLabel = messages.getMessage("notice.date.updated", null, locale);
                    attributes.add(new NotificationAttribute(dateLabel, DATE_FORMAT.format(updatededDate)));
                }
                entry.setAttributes(attributes);

                entries.add(entry);

            }

            // Items should be in reverse chronological order
            Collections.sort(entries);
            Collections.reverse(entries);

            rslt.setEntries(new ArrayList<>(entries));

        } catch (Exception e) {
            final String msg = "Unable to read the specified feed:  " + url;
            logger.error(msg, e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ioe) {
                    final String msg = "Unable to close the XmlReader";
                    logger.error(msg, ioe);
                }
            }
        }

        return rslt;

    }

}
