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
import org.jasig.portlet.notice.action.read.ReadAction;
import org.jasig.portlet.notice.rest.EventDTO;
import org.jasig.portlet.notice.util.IJpaServices;
import org.jasig.portlet.notice.util.UsernameFinder;
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
    public static final NotificationAttribute READ_ATTRIBUTE =
            new NotificationAttribute(READ_ATTRIBUTE_NAME, Boolean.TRUE.toString());

    @Autowired
    private UsernameFinder usernameFinder;

    @Autowired
    private IJpaServices jpaServices;

    public ReadStateSupportFilter() {
        super(AbstractNotificationServiceFilter.ORDER_NORMAL);
    }

    @Override
    public NotificationResponse doFilter(HttpServletRequest request, INotificationServiceFilterChain chain) {

        final NotificationResponse response = chain.doFilter();

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
                final boolean isRead = history.stream()
                        .anyMatch(event -> NotificationState.READ.equals(event.getState()));
                if (isRead) {
                    final List<NotificationAttribute> attributes = new ArrayList<>(entry.getAttributes());
                    attributes.add(READ_ATTRIBUTE);
                    entry.setAttributes(attributes);
                }

                /*
                 * Decorate with READ behavior, but only if the entry does not have a ReadAction
                 * already
                 */
                final List<NotificationAction> currentActions = entry.getAvailableActions();
                boolean hasReadActionAlready = currentActions.stream()
                        .anyMatch(action -> ReadAction.class.isInstance(action));
                if (!hasReadActionAlready) {
                    final List<NotificationAction> replacementList = new ArrayList<>(currentActions);
                    boolean isMarkedRead = entry.getAttributes().contains(READ_ATTRIBUTE);
                    replacementList.add(!isMarkedRead ?
                            ReadAction.createReadInstance() : ReadAction.createUnReadInstance());
                    entry.setAvailableActions(replacementList);
                }
            }
        }

        return rslt;

    }

}
