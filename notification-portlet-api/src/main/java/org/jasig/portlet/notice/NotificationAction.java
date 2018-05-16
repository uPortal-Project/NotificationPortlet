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

import java.io.IOException;
import java.io.Serializable;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Represents a behavior that a user may invoke on a notification.  The
 * possibilities are open-ended, but some examples include Hide, Mark as done,
 * and Favorite.  Concrete service impls must provide the business logic to
 * perform the action, as well as implement the invoke method.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(using = JsonNotificationActionDeserializer.class)
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class NotificationAction implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    // Instance Members
    private NotificationEntry target;
    private String id = getClass().getSimpleName();
    private String label;
    private String apiUrl;

    public final String getClazz() {
        return getClass().getName();
    }

    public final String getId() {
        return id;
    }

    /**
     * Identifies the action, from among the available set of actions, when the
     * user invokes it.  Must be unique within the notification.  The default is
     * the simpleName of the implementing class, so subclasses that can appear
     *  more than once within a notification should override it.
     */
    public final void setId(String id) {
        this.id = id;
    }

    public final String getLabel() {
        return label;
    }

    public final void setLabel(String label) {
        this.label = label;
    }

    @JsonIgnore
    public final NotificationEntry getTarget() {
        return target;
    }

    /**
     * Complete URL (ncluding CSRF token, if appropriate) that can be used to invoke this action
     * through the <code>NotificationRestV2Controller</code>.
     */
    public String getApiUrl() {
        return apiUrl;
    }

    /**
     * Sets the URL for invoking this action through the <code>NotificationRestV2Controller</code>.
     * <em>This field should not be provided by the data source</em> (i.e. Notification Service).
     * It must be set by the API layer.
     */
    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    /**
     * Perform this action on the notification to which it is attached.
     *
     * @deprecated Prefer interactions that are not based on the Portlet API
     */
    @Deprecated
    public abstract void invoke(ActionRequest req, ActionResponse res) throws IOException;

    /**
     * Perform this action on the notification to which it is attached.
     */
    public abstract void invoke(HttpServletRequest request, HttpServletResponse response) throws IOException;

    /**
     * Implements deep-copy clone.
     *
     * @throws CloneNotSupportedException Not really, but it's on the method
     * signature we're overriding.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {

        // Start with superclass impl (handles immutables and primitives)
        final NotificationAction rslt = (NotificationAction) super.clone();

        // Adjust to satisfy deep-copy strategy

        /*
         * NB:  We don't need to deep-copy the target because the target of an
         * action is always set as an action becomes attached to an entry --
         * which happens in this case as the owning entry gets cloned.
         */

        return rslt;

    }

    /*
     * Non-public API
     */

    /* package-private */ void setTarget(NotificationEntry target) {
        this.target = target;
    }

}
