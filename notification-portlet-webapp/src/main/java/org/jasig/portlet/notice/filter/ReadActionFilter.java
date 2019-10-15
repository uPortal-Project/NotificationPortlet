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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.jasig.portlet.notice.*;
import org.jasig.portlet.notice.rest.EventDTO;
import org.jasig.portlet.notice.util.IJpaServices;
import org.jasig.portlet.notice.util.UsernameFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReadActionFilter extends AbstractNotificationServiceFilter {

    public static final String READ_ATTRIBUTE_NAME = "READ";

    @Autowired
    private UsernameFinder usernameFinder;

    @Autowired
    private IJpaServices jpaServices;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected ReadActionFilter() {
        super(AbstractNotificationServiceFilter.ORDER_LAST);
    }

    @Override
    public NotificationResponse doFilter(
            HttpServletRequest request, INotificationServiceFilterChain chain) {
        final NotificationResponse response = chain.doFilter();

        final NotificationResponse rslt = response.cloneIfNotCloned();

        for (NotificationCategory category : rslt.getCategories()) {

            for (NotificationEntry entry : category.getEntries()) {

                if (StringUtils.isBlank(entry.getId())) {
                    continue;
                }

                final String username = usernameFinder.findUsername(request);
                final List<EventDTO> history = jpaServices.getHistory(entry, username);

                logger.trace(
                        "Found the following history for username='{}' and entryId='{}': {}",
                        username,
                        entry.getId(),
                        history);

                final List<NotificationAttribute> attributes =
                        new ArrayList<>(entry.getAttributes());
                final boolean isRead =
                        history.stream()
                                .anyMatch(event -> NotificationState.READ.equals(event.getState()));

                if (isRead) {
                    final List<NotificationAction> filteredActions =
                            entry.getAvailableActions()
                                    .stream()
                                    .filter(action -> !ReadStateAction.class.isInstance(action))
                                    .collect(Collectors.toList());
                    entry.setAvailableActions(filteredActions);
                }
            }
        }

        return rslt;
    }
}
