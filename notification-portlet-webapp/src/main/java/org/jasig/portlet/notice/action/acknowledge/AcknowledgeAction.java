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
package org.jasig.portlet.notice.action.acknowledge;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lombok.extern.slf4j.Slf4j;
import org.jasig.portlet.notice.filter.ReadStateAction;

/**
 * Acknowledge action class that represents a per session "read" of a notice.
 *
 * This class depends on {@code ApiUrlSupportFilter} (or other external code)
 * to set it's apiUrl attribute for callback.
 *
 * @since 4.6.0
 */
@Slf4j
public class AcknowledgeAction extends ReadStateAction {

    private static final String SESSION_ATTR_PREFIX = AcknowledgeAction.class.getName() + ".";

    @Override
    public void invoke(ActionRequest req, ActionResponse res) throws IOException {
        log.debug("Calling {} portlet invoke() for notice {}", AcknowledgeAction.class.getCanonicalName(), getTarget().getId());
        final PortletSession session = req.getPortletSession(false);
        log.debug("session id = {}", session.getId());
        // Don't care the value so we will just use current time
        session.setAttribute(getSessionAttrName(), System.currentTimeMillis(), PortletSession.APPLICATION_SCOPE);
    }

    @Override
    public void invoke(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.debug("Calling {} REST invoke() for notice {}", AcknowledgeAction.class.getCanonicalName(), getTarget().getId());
        final HttpSession session = request.getSession(false);
        log.debug("session id = {}", session.getId());
        // Don't care the value so we will just use current time
        session.setAttribute(getSessionAttrName(), System.currentTimeMillis());
    }

    private String getSessionAttrName() {
        assert getTarget() != null;
        // Target is the notice. It's ID should be unique across all sources, but we will add source anyway.
        return SESSION_ATTR_PREFIX + getTarget().getSource() + "." + getTarget().getId();
    }

    public boolean isAck(HttpSession session) {
        assert session != null;
        log.debug("session id = {}", session.getId());
        log.debug("session value for '{}' is {}", getSessionAttrName(), session.getAttribute(getSessionAttrName()));
        return session.getAttribute(getSessionAttrName()) != null;
    }

    public boolean isAck(PortletSession session) {
        assert session != null;
        log.debug("session id = {}", session.getId());
        log.debug("session value for '{}' is {}", getSessionAttrName(), session.getAttribute(getSessionAttrName()));
        return session.getAttribute(getSessionAttrName()) != null;
    }
}
