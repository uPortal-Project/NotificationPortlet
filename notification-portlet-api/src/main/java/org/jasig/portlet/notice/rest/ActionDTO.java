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
package org.jasig.portlet.notice.rest;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * @author Josh Helmer, jhelmer.unicon.net
 * @since 3.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ActionDTO implements Serializable {
    private static final long serialVersionUid = 1l;

    private long id;
    private String label;
    private String clazz;


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public String getLabel() {
        return label;
    }


    public void setLabel(String label) {
        this.label = label;
    }


    public String getClazz() {
        return clazz;
    }


    public void setClazz(String clazz) {
        this.clazz = clazz;
    }
}
