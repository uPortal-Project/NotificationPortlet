/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jasig.portlet.notice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

/**
 * This class represents a single notification within the portlet.  It defines a 
 * handful of strongly-typed members, plus an open-ended collection of 
 * attributes.  Strongly-typed items are chosen primarily because they support 
 * special handling in the UI (i.e. clickable URL).
 * 
 * <p>The {@link NotificationCategory} 
 * class contains all the entries for the same category title.
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY)
@XmlAccessorType(XmlAccessType.FIELD)
public class NotificationEntry implements Serializable, Cloneable {

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
    private boolean   favorite;

    /*
     * Weakly-typed, open-ended attributes collection
     */

    private List<NotificationAttribute> attributes = Collections.emptyList();

    /*
     * Operations that a user may perform on this notification
     */

    private List<NotificationAction> availableActions = Collections.emptyList();

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLinkText() {
        return linkText;
    }

    public void setLinkText(String linkText) {
        this.linkText = linkText;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @JsonSerialize(using=JsonDateSerializer.class)
    public Date getDueDate() {
        return dueDate;
    }

    @JsonDeserialize(using=JsonDateDeserializer.class)
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String imageUrl) {
        this.image = imageUrl;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @JsonSerialize(using=JsonAttributesSerializer.class)
    public List<NotificationAttribute> getAttributes() {
        return Collections.unmodifiableList(attributes);
    }

    @JsonDeserialize(using=JsonAttributesDeserializer.class)
    public void setAttributes(List<NotificationAttribute> attributes) {
        this.attributes = new ArrayList<NotificationAttribute>(attributes);  // defensive copy
    }

    public List<NotificationAction> getAvailableActions() {
        return Collections.unmodifiableList(availableActions);
    }

    public void setAvailableActions(List<NotificationAction> availableActions) {
        this.availableActions = new ArrayList<NotificationAction>();  // defensive copy
        for (NotificationAction action : availableActions) {
            // We must make ourself the target of any 
            // action at the time it becomes attached
            action.setTarget(this);
            this.availableActions.add(action);
        }
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
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
        List<NotificationAttribute> atrList = new ArrayList<NotificationAttribute>(attributes.size());
        for (NotificationAttribute attr : attributes) {
            atrList.add((NotificationAttribute) attr.clone());
        }
        rslt.setAttributes(atrList);
        List<NotificationAction> actList = new ArrayList<NotificationAction>(availableActions.size());
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
