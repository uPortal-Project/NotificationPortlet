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
package org.jasig.portlet.notice.service.jpa;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Corresponds to a {@code NotificationEntry} in the JPA flavor of notifications.
 * 
 * @since 3.0
 * @author drewwills
 */
@Entity
@Table(name=JpaNotificationService.TABLENAME_PREFIX + "ENTRY")
/* package-private */ public class JpaEntry {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="ID", nullable = false)
    private long id;

    @Column(name="TITLE", nullable=false)
    private String title;

    @Column(name="SOURCE", nullable=true)
    private String source;

    @Column(name="CATEGORY", nullable=true)
    private String category;

    @Column(name="URL", nullable=true)
    private String url;

    @Column(name="LINK_TEXT", nullable=true)
    private String linkText;

    @Column(name="PRIORITY", nullable=true)
    private int priority;

    @Column(name="DUE_DATE", nullable=true)
    private Timestamp dueDate;

    @Column(name="IMAGE", nullable=true)
    private String image;

    @Lob
    @Column(name="BODY", nullable=true)
    private String body;

    @OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinColumn(name="ENTRY_ID")
    private Set<JpaAttribute> attributes = new HashSet<>();

    @OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinColumn(name="ENTRY_ID")
    private Set<JpaAction> actions = new HashSet<>();

    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    @JoinColumn(name="ENTRY_ID")
    private Set<JpaAddressee> addressees = new HashSet<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public Timestamp getDueDate() {
        return dueDate;
    }

    public void setDueDate(Timestamp dueDate) {
        this.dueDate = dueDate;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Provides a read-only copy of this notification's attributes.
     */
    public Set<JpaAttribute> getAttributes() {
        return Collections.unmodifiableSet(attributes);
    }

    /**
     * Replaces the current attributes with the contents of the provided collection.
     */
    public void setAttributes(Set<JpaAttribute> attributes) {
        this.attributes.clear();
        for (JpaAttribute attr : attributes) {
            addAttribute(attr);
        }
    }

    /**
     * Adds the specified attribute to the current collection.
     */
    public void addAttribute(JpaAttribute attribute) {
        attributes.add(attribute);
        attribute.setEntryId(getId());
    }

    /**
     * Provides a read-only copy of this notification's actions.
     */
    public Set<JpaAction> getActions() {
        return Collections.unmodifiableSet(actions);
    }

    /**
     * Replaces the current actions with the contents of the provided collection.
     */
    public void setActions(Set<JpaAction> actions) {
        this.actions.clear();
        this.actions.addAll(actions);
    }

    /**
     * Adds the specified action to the current collection.
     */
    public void addAction(JpaAction attribute) {
        actions.add(attribute);
    }

    /**
     * Provides a read-only copy of this notification's addressees.
     */
    public Set<JpaAddressee> getAddressees() {
        return Collections.unmodifiableSet(addressees);
    }

    /**
     * Replaces the current addressees with the contents of the provided collection.
     */
    public void setAddressees(Set<JpaAddressee> addressees) {
        this.addressees.clear();
        for (JpaAddressee addressee : addressees) {
            addAddressee(addressee);
        }
    }

    /**
     * Adds the specified addressee to the current collection.
     */
    public void addAddressee(JpaAddressee addressee) {
        addressees.add(addressee);
        addressee.setEntryId(getId());
    }

}
