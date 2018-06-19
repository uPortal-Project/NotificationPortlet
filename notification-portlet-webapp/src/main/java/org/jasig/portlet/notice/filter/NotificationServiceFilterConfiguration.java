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

import org.jasig.portlet.notice.INotificationRepository;
import org.jasig.portlet.notice.INotificationService;
import org.jasig.portlet.notice.INotificationServiceFilter;
import org.jasig.portlet.notice.INotificationServiceFilterChain;
import org.jasig.portlet.notice.IRefreshable;
import org.jasig.portlet.notice.NotificationResponse;
import org.jasig.portlet.notice.util.UsernameFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Configuration of {@link INotificationServiceFilter} and {@link INotificationServiceFilterChain}
 * instances in the application context.
 *
 * @since 4.0
 */
@Configuration
public class NotificationServiceFilterConfiguration {

    @Autowired
    private List<INotificationServiceFilter> filters;

    @Autowired
    private Set<INotificationService> services;

    @Autowired
    private UsernameFinder usernameFinder;

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceFilterConfiguration.class);

    @Bean("notificationRepository")
    public INotificationRepository notificationRepository() {
        return new NotificationRepositoryImpl(filters, services, usernameFinder);
    }

    /*
     * Nested Types
     */

    private static class NotificationRepositoryImpl implements INotificationRepository {

        private final List<INotificationServiceFilter> sortedFilters;
        private final Set<INotificationService> services;
        private final UsernameFinder usernameFinder;

        /* package-private */ NotificationRepositoryImpl(List<INotificationServiceFilter> filters, Set<INotificationService> services, UsernameFinder usernameFinder) {

            // Prep the filters collection
            List<INotificationServiceFilter> filtersCopy = new ArrayList<>(filters);
            Collections.sort(filtersCopy);
            logger.info("Found the following INotificationServiceFilter beans in the following sequence:  {}", filtersCopy);
            Collections.reverse(filtersCopy);
            this.sortedFilters = Collections.unmodifiableList(filtersCopy);

            // Services
            logger.info("Found the following INotificationService beans:  {}", services);
            this.services = services;

            // Etc.
            this.usernameFinder = usernameFinder;

        }

        @Override
        public void refresh(HttpServletRequest request, HttpServletResponse response) {
            for (INotificationService service : services) {
                if (IRefreshable.class.isInstance(service)) {
                    logger.debug("Refreshing INotificationService bean '{}'", service.getName());
                    ((IRefreshable) service).refresh(request, response);
                }
            }
            for (INotificationServiceFilter filter : sortedFilters) {
                if (IRefreshable.class.isInstance(filter)) {
                    logger.debug("Refreshing INotificationServiceFilter bean '{}'", filter);
                    ((IRefreshable) filter).refresh(request, response);
                }
            }
        }

        @Override
        public NotificationResponse fetch(HttpServletRequest request) {

            final String username = usernameFinder.findUsername(request);
            logger.debug("Fetching notifications on behalf of user '{}'", username);

            /*
             * The end of the line:  the INotificationServiceFilterChain that wraps the collection
             * of INotificationService beans.
             */
            INotificationServiceFilterChain chain = () -> {
                NotificationResponse rslt = new NotificationResponse();
                for (INotificationService service : services) {
                    NotificationResponse response = service.fetch(request);
                    rslt = rslt.combine(response);
                    logger.debug("Processed INotificationService bean '{}';  size={}", service.getName(), rslt.size());
                }
                return rslt;
            };

            /*
             * Build the rest of the INotificationServiceFilterChain.
             */
            for (INotificationServiceFilter filter : sortedFilters) {
                chain = new NotificationServiceFilterChainImpl(filter, request, chain);
            }

            return chain.doFilter();

        }

    }

    private static class NotificationServiceFilterChainImpl implements INotificationServiceFilterChain {

        private final INotificationServiceFilter filter;
        private final HttpServletRequest request;
        private final INotificationServiceFilterChain nextLink;

        /* package-private */ NotificationServiceFilterChainImpl(INotificationServiceFilter filter,
                HttpServletRequest request, INotificationServiceFilterChain nextLink) {

            this.filter = filter;
            this.request = request;
            this.nextLink = nextLink;

        }

        @Override
        public NotificationResponse doFilter() {
            final NotificationResponse rslt = filter.doFilter(request, nextLink);
            logger.debug("Processing INotificationServiceFilter bean '{}';  size={}", filter, rslt.size());
            return rslt;
        }
    }

}
