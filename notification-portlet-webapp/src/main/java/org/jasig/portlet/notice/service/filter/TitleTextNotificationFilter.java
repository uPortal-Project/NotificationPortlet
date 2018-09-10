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

import org.jasig.portlet.notice.NotificationCategory;
import org.jasig.portlet.notice.NotificationEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This concrete implementation of {@link INotificationFilter} filters out notices that have a title
 * that matches the regex value.
 *
 * @since 4.3
 */
/* package-private */ class TitleTextNotificationFilter extends TextNotificationFilter {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public TitleTextNotificationFilter(String regex) {
        super(regex);
    }

    @Override
    public boolean doFilter(NotificationCategory category, NotificationEntry entry) {
        assert entry != null;
        if (entry.getTitle() == null || entry.getTitle().isEmpty()) {
            // nothing to check
            return false;
        }
        final boolean match = pattern.matcher(entry.getTitle()).matches();
        logger.debug("Testing against title: {} = {}", entry.getTitle(), match);
        return !match;
    }
}
