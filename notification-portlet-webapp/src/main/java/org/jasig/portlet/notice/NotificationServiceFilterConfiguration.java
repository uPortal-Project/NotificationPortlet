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
package org.jasig.portlet.notice;

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

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceFilterConfiguration.class);

    @Bean("notificationRepository")
    public INotificationRepository notificationRepository() {
        return new NotificationRepositoryImpl(filters, services);
    }

    /*
     * Nested Types
     */

    private static class NotificationRepositoryImpl implements INotificationRepository {

        private final List<INotificationServiceFilter> sortedFilters;
        private final Set<INotificationService> services;

        /* package-private */ NotificationRepositoryImpl(List<INotificationServiceFilter> filters, Set<INotificationService> services) {

            // Prep the filters collection
            List<INotificationServiceFilter> filtersCopy = new ArrayList<>(filters);
            Collections.sort(filtersCopy);
            logger.info("Found the following INotificationServiceFilter beans in the following sequence:  {}", filtersCopy);
            Collections.reverse(filtersCopy);
            this.sortedFilters = Collections.unmodifiableList(filtersCopy);

            // Services
            logger.info("Found the following INotificationService beans:  {}", services);
            this.services = services;

        }

        @Override
        public void refresh(HttpServletRequest request, HttpServletResponse response) {
            for (INotificationService service : services) {
                logger.debug("Refreshing INotificationService bean '{}'", service.getName());
                service.refresh(request, response);
            }
        }

        @Override
        public NotificationResponse fetch(HttpServletRequest request) {

            /*
             * The end of the line:  the INotificationServiceFilterChain that wraps the collection
             * of INotificationService beans.
             */
            INotificationServiceFilterChain chain = () -> {
                NotificationResponse rslt = new NotificationResponse();
                for (INotificationService service : services) {
                    logger.debug("Processing INotificationService bean '{}'", service.getName());
                    NotificationResponse response = service.fetch(request);
                    rslt = rslt.combine(response);
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
            logger.debug("Processing INotificationServiceFilter bean:  {}", filter);
            return filter.doFilter(request, nextLink);
        }
    }

}
