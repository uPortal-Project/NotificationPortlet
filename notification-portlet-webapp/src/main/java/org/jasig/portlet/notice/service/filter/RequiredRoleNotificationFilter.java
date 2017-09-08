/**
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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jasig.portlet.notice.NotificationCategory;
import org.jasig.portlet.notice.NotificationEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This concrete implementation of {@link INotificationFilter} filters notices based on role.  The
 * way it works is perhaps more complicated than other filters:  it must be defined with portlet
 * preferences (like other filters), but it will only exclude notices that have a fully-qualified
 * <code>requiredRole</code> attribute defined.  (See <code>REQUIRED_ROLE_ATTRIBUTE_NAME</code>
 * below.)
 *
 * @since 3.1
 */
/* package-private */ class RequiredRoleNotificationFilter implements INotificationFilter {

    public static final String REQUIRED_ROLE_ATTRIBUTE_NAME =
            RequiredRoleNotificationFilter.class.getName() + ".requiredRole";

    private final Set<String> userRolesLowerCase;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public RequiredRoleNotificationFilter(Set<String> userRoles) {
        this.userRolesLowerCase = userRoles.stream()
                .map(role -> role.toLowerCase())
                .collect(Collectors.toSet());
    }

    @Override
    public boolean doFilter(NotificationCategory category, NotificationEntry entry) {


        final List<String> requiredRoleValues = entry.getAttributesMap().get(REQUIRED_ROLE_ATTRIBUTE_NAME);
        if (requiredRoleValues == null || requiredRoleValues.size() == 0) {
            // The entry doesn't specify any roles, so we don't need any checking
            return true;
        } else {
            // We're interested in whether there is intersection between the 2 collections
            boolean rslt = requiredRoleValues.stream()
                    .anyMatch(role -> userRolesLowerCase.contains(role.toLowerCase()));
            logger.debug("Filtering entry '{}'... requiredRoleValues='{}', userRolesLowerCase='{}', decision='{}'",
                    entry.getTitle(), requiredRoleValues, userRolesLowerCase, rslt);
            return rslt;
        }


    }

}
