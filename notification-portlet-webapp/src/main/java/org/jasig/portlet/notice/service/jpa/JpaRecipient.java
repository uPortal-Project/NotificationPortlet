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
package org.jasig.portlet.notice.service.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Represents an individual who receives a notification.
 *
 * @since 3.0
 * @author drewwills
 */
@Entity
@Table(name=JpaNotificationService.TABLENAME_PREFIX + "RECIPIENT")
/* package-private */ class JpaRecipient {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="ID", nullable = false)
    private long id;

    @ManyToOne
    @JoinColumn(name="ADDRESSEE_ID")
    private JpaAddressee addressee;

    @Column(name="USERNAME", nullable=false)
    private String username;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public JpaAddressee getAddressee() {
        return addressee;
    }

    public void setAddressee(JpaAddressee addressee) {
        this.addressee = addressee;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "JpaRecipient [id=" + id + ", username=" + username + "]";
    }

}
