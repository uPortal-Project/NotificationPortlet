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
package org.jasig.portlet.notice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang.builder.ToStringBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * This class represents a single notification.  It defines a handful of 
 * strongly-typed members, plus a few open-ended collections.  These collections
 * are for <em>attributes</em>, <em>actions</em>, and <em>states</em>.
 * Strongly-typed items are chosen primarily because they require
 * framework-level special handling.  The open-ended collections make the
 * platform <em>extensible</em>.  Please add additional members only as a last
 * resort.
 * 
 * <p>The {@link NotificationCategory} 
 * class contains all the entries for the same category title.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@XmlAccessorType(XmlAccessType.FIELD)
public class NotificationEntry implements Serializable, Cloneable {

    /**
     * The <code>priority</code> field of a {@link NotificationEntry} will contain this value when
     * the priority is unspecified by the source.
     *
     * @since 3.1
     */
    public static final int PRIORITY_UNSPECIFIED = 0;

    private static final long serialVersionUID = 1L;

    /*
     * Strongly-typed members
     */

    private String    source;
    private String    id;
    private String    title;
    private String    url;
    private String    linkText;
    private int       priority;
    private Date      dueDate;
    private String    image;
    private String    body;

    /*
     * Weakly-typed, open-ended attributes collection
     */
    private List<NotificationAttribute> attributes = Collections.emptyList();

    /*
     * Operations that a user may perform on this notification
     */
    private List<NotificationAction> availableActions = Collections.emptyList();

    /*
     * Representation of where this notification is in applicable workflow(s).
     */
    private Map<NotificationState,Date> states = Collections.emptyMap();

    /**
     * Provides the human-readable name for the source of this notification.
     * E.g. 'Office of the Registrar'
     */
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier for this notification.  Ids are not required 
     * <strong>except for notifications that support actions</strong>.  The 
     * value of the id field can be any javascript-safe String, but must be 
     * unique within a user's collection of notifications.
     * <code>UUID.randomUUID()</code> could be a good way to generate them.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Required one-liner version of this notification.  E.g. 'You have 3
     * overdue library books.'
     */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Optional web address where the user can deal with or learn more about the
     * details of this notification.
     */
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Optional text of the anchor tag linking to <code>url</code>.
     */
    public String getLinkText() {
        return linkText;
    }

    public void setLinkText(String linkText) {
        this.linkText = linkText;
    }

    /**
     * Optional numeric representation of the relative importance of the
     * notification.  Vales range from 1 (most important) to 5 (least important).
     */
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * Optional date/time indicating by when the action described in this
     * notification must be completed.
     */
    @JsonSerialize(using=JsonDateSerializer.class)
    public Date getDueDate() {
        return dueDate;
    }

    @JsonDeserialize(using=JsonDateDeserializer.class)
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Optional URL of an image file associated with this notification.  Might
     * be the logo of the source system or something more specific.
     */
    public String getImage() {
        return image;
    }

    public void setImage(String imageUrl) {
        this.image = imageUrl;
    }

    /**
     * Optional body text for this notification.  Supports HTML tags.
     */
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Open-ended collection of notification metadata.  The presumption is that
     * attributes will be visible to users.
     */
    @JsonSerialize(using=JsonAttributesSerializer.class)
    public List<NotificationAttribute> getAttributes() {
        return Collections.unmodifiableList(attributes);
    }

    @JsonDeserialize(using=JsonAttributesDeserializer.class)
    public void setAttributes(List<NotificationAttribute> attributes) {
        this.attributes = new ArrayList<>(attributes);  // defensive copy
    }

    /**
     * Convenience method for obtaining the attributes in a more usable collection.
     */
    @JsonIgnore
    public Map<String,List<String>> getAttributesMap() {
        Map<String,List<String>> rslt = new HashMap<>();
        for (NotificationAttribute a : attributes) {
            rslt.put(a.getName(), a.getValues());
        }
        return rslt;
    }

    /**
     * Open-ended collection of behaviors the user (recipient) can invoke upon
     * the notification.  E.g. 'hide' or 'mark as done'.  Different Notification
     * UIs may support different behaviors (viz. not all behaviors will be
     * supported by all view JSPs).
     */
    public List<NotificationAction> getAvailableActions() {
        return Collections.unmodifiableList(availableActions);
    }

    public void setAvailableActions(List<NotificationAction> availableActions) {
        this.availableActions = new ArrayList<>();  // defensive copy
        for (NotificationAction action : availableActions) {
            // We must make ourself the target of any 
            // action at the time it becomes attached
            action.setTarget(this);
            this.availableActions.add(action);
        }
    }

    /**
     * Open-ended Map of states that currently apply to this notification.  Keys
     * are the states themselves, while values provide a way to indicate the
     * moment when the state was applied.  Data sources may leave values
     * <code>null</code>.
     *
     * @since 2.2
     */
    public Map<NotificationState,Date> getStates() {
        return Collections.unmodifiableMap(states);
    }

    /**
     * @since 2.2
     */
    public void setStates(Map<NotificationState,Date> states) {
        this.states = new HashMap<>(states);  // defensive copy
    }

    /**
     * Implements deep-copy clone.
     * 
     * @throws CloneNotSupportedException Not really, but it's on the method 
     * signature we're overriding.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {

        // Start with superclass impl (handles immutables and primitives)
        final NotificationEntry rslt = (NotificationEntry) super.clone();

        // Adjust to satisfy deep-copy strategy
        List<NotificationAttribute> atrList = new ArrayList<>(attributes.size());
        for (NotificationAttribute attr : attributes) {
            atrList.add((NotificationAttribute) attr.clone());
        }
        rslt.setAttributes(atrList);
        List<NotificationAction> actList = new ArrayList<>(availableActions.size());
        for (NotificationAction action : availableActions) {
            actList.add((NotificationAction) action.clone());
        }
        rslt.setAvailableActions(actList);

        return rslt;

    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
