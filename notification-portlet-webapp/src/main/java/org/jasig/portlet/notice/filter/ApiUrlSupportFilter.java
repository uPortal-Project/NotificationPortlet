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

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apereo.portal.soffit.Headers;
import org.jasig.portlet.notice.INotificationServiceFilter;
import org.jasig.portlet.notice.INotificationServiceFilterChain;
import org.jasig.portlet.notice.NotificationAction;
import org.jasig.portlet.notice.NotificationCategory;
import org.jasig.portlet.notice.NotificationEntry;
import org.jasig.portlet.notice.NotificationResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Component;

/**
 * This {@link INotificationServiceFilter} sets the <code>apiUrl</code> property on
 * {@link NotificationAction} objects within the {@link NotificationResponse}.
 *
 * @since 4.0
 */
@Component
public class ApiUrlSupportFilter extends AbstractNotificationServiceFilter {

    public static final String AUTHORIZATION_PARAMETER_NAME = "_authorization";

    /**
     * The format strings are as follows:
     * <ul>
     *   <li>protocol, host, port (if applicable), and context</li>
     *   <li>action id</li>
     *   <li>notification id</li>
     *   <li>Spring CSRF token</li>
     * </ul>
     */
    private static final String REST_API_URL_FORMAT =
            "%s/api/v2/action/%s/%s?_csrf=%s&" + AUTHORIZATION_PARAMETER_NAME + "=%s";

    /**
     * This {@link INotificationServiceFilter} must do its work late in the chain because filters
     * commonly add actions.
     */
    public ApiUrlSupportFilter() {
        // needs to order after {@code ReadStateSupportFilter}
        super(AbstractNotificationServiceFilter.ORDER_VERY_LATE);
    }

    @Override
    public NotificationResponse doFilter(HttpServletRequest request, INotificationServiceFilterChain chain) {

        final NotificationResponse response = chain.doFilter();

        final NotificationResponse rslt = response.cloneIfNotCloned();

        // Add apiUrl values to actions with our copy
        for (NotificationCategory category : rslt.getCategories()) {

            for (NotificationEntry entry : category.getEntries()) {

                final List<NotificationAction> actions = entry.getAvailableActions().stream()
                        .peek(action -> {
                            if (StringUtils.isNotBlank(action.getId())
                                    && action.getTarget() != null
                                    && StringUtils.isNotBlank(action.getTarget().getId())) {
                                // Pick up scheme, host[, port,] and context from the request
                                final String requestUrl = request.getRequestURL().toString();
                                final String contextPath = request.getContextPath();
                                final String urlBase = requestUrl.substring(0,
                                        requestUrl.indexOf(contextPath)) + contextPath;
                                final CsrfToken csrf =
                                        (CsrfToken) request.getAttribute(CsrfToken.class.getName());
                                final String apiUrl = String.format(REST_API_URL_FORMAT,
                                        urlBase,
                                        action.getId(),
                                        action.getTarget().getId(),
                                        csrf != null ? csrf.getToken() : null,
                                        getBearerToken(request));
                                action.setApiUrl(apiUrl);
                            }
                        })
                        .collect(Collectors.toList());
                entry.setAvailableActions(actions);

            }

        }

        return rslt;

    }

    private String getBearerToken(HttpServletRequest request) {
        String rslt = ""; // default
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isNotBlank(authHeader)) {                       // Authorization header is present?
            if (authHeader.startsWith(Headers.BEARER_TOKEN_PREFIX)) {   // Authorization header is a Bearer token?
                rslt = authHeader.substring(Headers.BEARER_TOKEN_PREFIX.length());
            }
        }
        return rslt;
    }

}
