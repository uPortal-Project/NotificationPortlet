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
package org.jasig.portlet.notice.service.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletRequest;

import org.jasig.portlet.notice.INotificationService;
import org.jasig.portlet.notice.NotificationCategory;
import org.jasig.portlet.notice.NotificationEntry;
import org.jasig.portlet.notice.NotificationResponse;
import org.jasig.portlet.notice.service.AbstractNotificationServiceDecorator;
import org.jasig.portlet.notice.util.PortletXmlRoleService;
import org.jasig.portlet.notice.util.UsernameFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This {@link INotificationService} reduces the number of notices shown based on filtering rules.
 * For example, 'show only notices of priority 1' or 'show only notices in the Library category'.
 *
 * @since 3.1
 * @deprecated The entire notion of Portlet API-based decorators is deprecated
 */
public class FilteringNotificationServiceDecorator extends AbstractNotificationServiceDecorator {

    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    private static final String PORTLET_XML_ROLE_SERVICE_ATTRIBUTE =
            "FilteringNotificationServiceDecorator.portletXmlRoleService";

    private enum FilterMapper {

        MINIMUM_PRIORITY {
            private static final String PREFERENCE_NAME = "FilteringNotificationServiceDecorator.minPriority";

            @Override
            Set<INotificationFilter> fromPortletRequest(PortletRequest req) {
                final String[] preferenceValues = req.getPreferences().getValues(PREFERENCE_NAME, EMPTY_STRING_ARRAY);
                if (preferenceValues.length != 0) {
                    // We only support the first value...
                    int priority = Integer.valueOf(preferenceValues[0]);
                    return Collections.singleton(new MinimumPriorityNotificationFilter(priority));
                } else {
                    return Collections.emptySet();
                }
            }
        },

        MAXIMUM_PRIORITY {
            private static final String PREFERENCE_NAME = "FilteringNotificationServiceDecorator.maxPriority";

            @Override
            Set<INotificationFilter> fromPortletRequest(PortletRequest req) {
                final String[] preferenceValues = req.getPreferences().getValues(PREFERENCE_NAME, EMPTY_STRING_ARRAY);
                if (preferenceValues.length != 0) {
                    // We only support the first value...
                    int priority = Integer.valueOf(preferenceValues[0]);
                    return Collections.singleton(new MaximumPriorityNotificationFilter(priority));
                } else {
                    return Collections.emptySet();
                }
            }
        },

        REQUIRED_ROLE {
            private static final String PREFERENCE_NAME = "FilteringNotificationServiceDecorator.requiredRole";

            @Override
            Set<INotificationFilter> fromPortletRequest(PortletRequest req) {
                final String[] preferenceValues = req.getPreferences().getValues(PREFERENCE_NAME, EMPTY_STRING_ARRAY);
                // We only support the first value, and we're looking for 'true' (ignoring case)...
                if (preferenceValues.length != 0 && Boolean.valueOf(preferenceValues[0])) {
                    // Gather the user's roles...
                    final Set<String> userRoles = new HashSet<>();
                    PortletXmlRoleService portletXmlRoleService =
                            (PortletXmlRoleService) req.getAttribute(PORTLET_XML_ROLE_SERVICE_ATTRIBUTE);
                    for (String definedRole : portletXmlRoleService.getAllRoles()) {
                        if (req.isUserInRole(definedRole)) {
                            userRoles.add(definedRole);
                        }
                    }
                    return Collections.singleton(new RequiredRoleNotificationFilter(userRoles));
                }
                return Collections.emptySet();
            }
        },

        TITLE_REGEX {
            private static final String PREFERENCE_NAME = "FilteringNotificationServiceDecorator.titleRegex";

            @Override
            Set<INotificationFilter> fromPortletRequest(PortletRequest req) {
                final String[] preferenceValues = req.getPreferences().getValues(PREFERENCE_NAME, EMPTY_STRING_ARRAY);
                if (preferenceValues.length != 0) {
                    // We only support the first value...
                    final String regex = preferenceValues[0];
                    return Collections.singleton(new TitleTextNotificationFilter(regex));
                } else {
                    return Collections.emptySet();
                }
            }
        },

        BODY_REGEX {
            private static final String PREFERENCE_NAME = "FilteringNotificationServiceDecorator.bodyRegex";

            @Override
            Set<INotificationFilter> fromPortletRequest(PortletRequest req) {
                final String[] preferenceValues = req.getPreferences().getValues(PREFERENCE_NAME, EMPTY_STRING_ARRAY);
                if (preferenceValues.length != 0) {
                    // We only support the first value...
                    final String regex = preferenceValues[0];
                    return Collections.singleton(new BodyTextNotificationFilter(regex));
                } else {
                    return Collections.emptySet();
                }
            }
        };

        abstract Set<INotificationFilter> fromPortletRequest(PortletRequest req);

    }

    // Instance members
    private INotificationService enclosedNotificationService;

    private UsernameFinder usernameFinder;

    private PortletXmlRoleService portletXmlRoleService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public void setUsernameFinder(UsernameFinder usernameFinder) {
        this.usernameFinder = usernameFinder;
    }

    @Autowired
    public void setPortletXmlRoleService(PortletXmlRoleService portletXmlRoleService) {
        this.portletXmlRoleService = portletXmlRoleService;
    }

    public void setEnclosedNotificationService(INotificationService enclosedNotificationService) {
        this.enclosedNotificationService = enclosedNotificationService;
    }

    @Override
    public String getName() {
        return enclosedNotificationService.getName();
    }

    @Override
    public void invoke(ActionRequest req, ActionResponse res, boolean refresh) {
        enclosedNotificationService.invoke(req, res, refresh);
    }

    @Override
    public void collect(EventRequest req, EventResponse res) {
        enclosedNotificationService.collect(req, res);
    }


    @Override
    public NotificationResponse fetch(PortletRequest req) {

        final NotificationResponse unfilteredResponse = enclosedNotificationService.fetch(req);

        // Gather the filters...
        final Set<INotificationFilter> filters = new HashSet<>();
        req.setAttribute(PORTLET_XML_ROLE_SERVICE_ATTRIBUTE, portletXmlRoleService);
        for (FilterMapper mapper : FilterMapper.values()) {
            filters.addAll(mapper.fromPortletRequest(req));
        }

        if (filters.size() == 0) {
            // Probably the most common scenario...
            logger.debug("No INotificationFilter objects apply to notifications for "
                    + "username='{}' and windowID='{}'",
                    usernameFinder.findUsername(req), req.getWindowID());
            return unfilteredResponse;
        } else {
            logger.debug("Found the following INotificationFilter objects for username='{}' "
                    + "and windowID='{}':  {}", usernameFinder.findUsername(req),
                    req.getWindowID(), filters);
            /*
             * We need to slice, dice, and reconstruct the response.  This process involves a
             * number of nested loops, and may not scale well.  The output should probably be
             * cached.
             */
            final List<NotificationCategory> filteredCategories = new ArrayList<>();
            for (NotificationCategory unfilteredCategory : unfilteredResponse.getCategories()) {
                final List<NotificationEntry> filteredEntries = new ArrayList<>();
                for (NotificationEntry entry : unfilteredCategory.getEntries()) {
                    boolean excluded = false; // until we know otherwise...
                    for (INotificationFilter filter : filters) {
                        if (!filter.doFilter(unfilteredCategory, entry)) {
                            excluded = true;
                            break;
                        }
                    }
                    if (!excluded) {
                        filteredEntries.add(entry);
                    }
                }
                if (filteredEntries.size() != 0) {
                    final NotificationCategory category = new NotificationCategory();
                    category.setTitle(unfilteredCategory.getTitle());
                    category.setEntries(filteredEntries);
                    filteredCategories.add(category);
                }
            }
            final NotificationResponse rslt = new NotificationResponse();
            rslt.setCategories(filteredCategories);
            rslt.setErrors(unfilteredResponse.getErrors());
            return rslt;
        }

    }

    @Override
    public boolean isValid(PortletRequest req, NotificationResponse previousResponse) {
        return enclosedNotificationService.isValid(req, previousResponse);
    }

}
