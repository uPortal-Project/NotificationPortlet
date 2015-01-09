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

package org.jasig.portlet.notice.service.jpa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Supports open-ended metadata for notifications.  The attributes collection is
 * an associative array: String (key) to String[] (values).
 */
@Entity
@Table(name=JpaNotificationService.TABLENAME_PREFIX + "ATTRIBUTE")
/* package-private */ class JpaAttribute {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="ID", nullable = false)
    private long id;

    @Column(name="NAME", nullable=false)
    private String name;

    @ElementCollection(fetch=FetchType.EAGER)
    @CollectionTable(name=JpaNotificationService.TABLENAME_PREFIX + "ATTRIBUTE_VALUES")
    @Column(name="VAL")
    private List<String> values = new ArrayList<String>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Provides a read-only copy of this attribute's values.
     */
    public List<String> getValues() {
        return Collections.unmodifiableList(values);
    }

    /**
     * Replaces the current values with the contents of the provided list.
     */
    public void setValues(List<String> values) {
        this.values.clear();
        this.values.addAll(values);
    }

    /**
     * Inserts the specified value to the current list at the end.
     */
    public void addValue(String value) {
        values.add(value);
    }

    /**
     * Inserts the specified value at the specified position in the list.
     * 
     * @throws IndexOutOfBoundsException If the index is out of range (index < 0
     * || index > size())
     */
    public void add(int index, String value) {
        values.add(index, value);
    }

}
