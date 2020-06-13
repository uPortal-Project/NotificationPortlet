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
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import lombok.extern.slf4j.Slf4j;
import org.jasig.portlet.notice.INotificationServiceFilterChain;
import org.jasig.portlet.notice.NotificationAction;
import org.jasig.portlet.notice.NotificationCategory;
import org.jasig.portlet.notice.NotificationEntry;
import org.jasig.portlet.notice.NotificationResponse;
import org.jasig.portlet.notice.action.acknowledge.AcknowledgeAction;
import org.springframework.stereotype.Component;

/**
 * {@code NotificationServiceFilter} that implements per session "read" of notices
 * that have a {@code AcknowledgeAction} in their action lists.
 *
 * This class also provides filtering for acknowledged or un-acknowledged notices
 * with {@code AcknowledgeAction} actions.
 *
 * @since 4.6.0
 */
@Slf4j
@Component
public class AcknowledgePerSessionFilter extends AbstractNotificationServiceFilter {

    private static final String REQ_ACK_PARAM = "ack";

    public AcknowledgePerSessionFilter() {
        super(AbstractNotificationServiceFilter.ORDER_EARLY);
    }

    @Override
    public NotificationResponse doFilter(HttpServletRequest request, INotificationServiceFilterChain chain) {

        log.debug("{}.doFilter called", AcknowledgePerSessionFilter.class.getCanonicalName());

        final String ackParameter = request.getParameter(REQ_ACK_PARAM);

        final HttpSession session = request.getSession(true);
        log.debug("session id = {}", session.getId());
        final NotificationResponse response = chain.doFilter().cloneIfNotCloned();
        log.debug("response = {}", response);

        if (ackParameter == null) {
            // Don't filter entries, just the acknowledge actions
            log.debug("{} was not found in the parameter list", REQ_ACK_PARAM);
            return removeAckActionsWhenAck(response, session);
        }

        final boolean filterAck = Boolean.parseBoolean(ackParameter);
        log.debug("{} parsed to {} in request", REQ_ACK_PARAM, filterAck);

        for (NotificationCategory category : response.getCategories()) {
            // new entry list
            final List<NotificationEntry> newEntries = new ArrayList<>();
            for (NotificationEntry entry : category.getEntries()) {
                log.debug("entry: {}", entry);

                final List<NotificationAction> currentActions = entry.getAvailableActions();
                final Optional<NotificationAction> acknowledgeActionOptional = currentActions.stream()
                        .filter(AcknowledgeAction.class::isInstance)
                        .findFirst();
                final AcknowledgeAction ackAction = (AcknowledgeAction) acknowledgeActionOptional.orElse(null);
                assert (ackAction == null) || entry.equals(ackAction.getTarget());
                final boolean isAck = ackAction.isAck(session);
                log.debug("{} acknowledge state = {}", entry.getId(), isAck);
                if (filterAck && isAck) {
                    log.debug("entry {} ack and {}=true", entry.getId(), REQ_ACK_PARAM);
                    newEntries.add(entry);
                } else if (!filterAck && (ackAction == null || !isAck)) {
                    log.debug("entry {} not ack and {}=false", entry.getId(), REQ_ACK_PARAM);
                    newEntries.add(entry);
                } else {
                    log.debug("entry {} not selected when {}={} ... removed", entry.getId(), REQ_ACK_PARAM, filterAck);
                }
            }
            // replace entries in current category
            category.setEntries(newEntries);
        }

        return removeAckActionsWhenAck(response, session);
    }

    /**
     * Remove {@code AcknowledgeAction} actions from entries in returned {@code NotificationResponse}.
     *
     * @param resp response with entries that will have their acknowledge actions removed
     * @param session session to check for acknowledgements
     * @return processed response that is the same as the parameter
     */
    private NotificationResponse removeAckActionsWhenAck(NotificationResponse resp, HttpSession session) {
        // perform in-place removal of AcknowledgeActions
        for (NotificationCategory category : resp.getCategories()) {
            for (NotificationEntry entry : category.getEntries()) {
                log.debug("entry {} actions filtered", entry.getId());
                List<NotificationAction> list = entry.getAvailableActions().stream()
                        .filter(a -> !AcknowledgeAction.class.isInstance(a)
                                || !((AcknowledgeAction) a).isAck(session))
                        .collect(Collectors.toList());
                entry.setAvailableActions(list);
            }
        }
        log.debug("updated response = {}", resp);
        return resp;
    }
}
