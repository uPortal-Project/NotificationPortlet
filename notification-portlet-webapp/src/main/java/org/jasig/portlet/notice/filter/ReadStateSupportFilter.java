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
package org.jasig.portlet.notice.filter;

import org.apache.commons.lang.StringUtils;
import org.jasig.portlet.notice.INotificationServiceFilter;
import org.jasig.portlet.notice.INotificationServiceFilterChain;
import org.jasig.portlet.notice.NotificationAction;
import org.jasig.portlet.notice.NotificationAttribute;
import org.jasig.portlet.notice.NotificationCategory;
import org.jasig.portlet.notice.NotificationEntry;
import org.jasig.portlet.notice.NotificationResponse;
import org.jasig.portlet.notice.NotificationState;
import org.jasig.portlet.notice.action.read.MarkAsReadAndRedirectAction;
import org.jasig.portlet.notice.action.read.ReadAction;
import org.jasig.portlet.notice.rest.EventDTO;
import org.jasig.portlet.notice.util.IJpaServices;
import org.jasig.portlet.notice.util.UsernameFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * For notifications based on the Servlet API, this {@link INotificationServiceFilter}
 * implementation provides support for <code>NotificationState.READ</code>.
 *
 * @since 4.0
 */
@Component
public class ReadStateSupportFilter extends AbstractNotificationServiceFilter {

    public static final String READ_ATTRIBUTE_NAME = "READ";
    public static final String READ_PARAMETER_NAME = "read";
    public static final NotificationAttribute READ_ATTRIBUTE =
            new NotificationAttribute(READ_ATTRIBUTE_NAME, Boolean.TRUE.toString());
    public static final NotificationAttribute UNREAD_ATTRIBUTE =
            new NotificationAttribute(READ_ATTRIBUTE_NAME, Boolean.FALSE.toString());

    @Autowired
    private UsernameFinder usernameFinder;

    @Autowired
    private IJpaServices jpaServices;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public ReadStateSupportFilter() {
        super(AbstractNotificationServiceFilter.ORDER_NORMAL);
    }

    @Override
    public NotificationResponse doFilter(HttpServletRequest request, INotificationServiceFilterChain chain) {

        final NotificationResponse response = chain.doFilter();

        final String readFilterParameter = request.getParameter(READ_PARAMETER_NAME);

        final NotificationResponse rslt = response.cloneIfNotCloned();

        // Add and implement the read behavior with our copy
        for (NotificationCategory category : rslt.getCategories()) {

            for (NotificationEntry entry : category.getEntries()) {

                /*
                 * Participation in READ behavior is 100%
                 * dependant on having an id set on the entry.
                 */
                if (StringUtils.isBlank(entry.getId())) {
                    continue;
                }

                /*
                 * Apply the READ attribute if the circumstances call for it.
                 */
                final String username = usernameFinder.findUsername(request);
                final List<EventDTO> history = jpaServices.getHistory(entry, username);

                logger.trace("Found the following history for username='{}' and entryId='{}': {}",
                        username, entry.getId(), history);

                final List<NotificationAttribute> attributes = new ArrayList<>(entry.getAttributes());
                final boolean isRead = history.stream()
                        .anyMatch(event -> NotificationState.READ.equals(event.getState()));
                attributes.add(isRead ? READ_ATTRIBUTE : UNREAD_ATTRIBUTE);
                entry.setAttributes(attributes);

                /*
                 * Decorate with READ behavior, but only if (1) the entry is unread and (2) the
                 * entry does not have a ReadAction already
                 */
                if (!isRead) {
                    final List<NotificationAction> currentActions = entry.getAvailableActions();
                    boolean hasReadActionAlready = currentActions.stream()
                            .anyMatch(action -> ReadAction.class.isInstance(action));
                    if (!hasReadActionAlready) {
                        final List<NotificationAction> replacementList = new ArrayList<>(currentActions);
                        replacementList.add(new MarkAsReadAndRedirectAction());
                        entry.setAvailableActions(replacementList);
                    }
                }
            }
        }

        if (StringUtils.isNotBlank(readFilterParameter)) {
            boolean readFilterValue = Boolean.parseBoolean(readFilterParameter);

            return rslt.filter(
                    entry -> {
                        boolean isRead =
                                entry.getAttributes()
                                        .stream()
                                        .anyMatch(attribute -> attribute.equals(READ_ATTRIBUTE));

                        return isRead == readFilterValue;
                    });
        }

        return rslt;
    }
}
