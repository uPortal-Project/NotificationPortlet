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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Represents an extra, weakly-typed piece of meta data attached to a 
 * {@link NotificationEntry}.  These attributes are normally displayed, 
 * as-provided, on the details screen. 
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@XmlAccessorType(XmlAccessType.FIELD)
public class NotificationAttribute implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    private String name;
    private List<String> values = Collections.emptyList();

    public NotificationAttribute() {}

    /**
     * Shortcut constructor.
     */
    public NotificationAttribute(String name, String value) {
        this(name, Arrays.asList(new String[] { value }));
    }

    public NotificationAttribute(String name, List<String> values) {
        this.name = name;
        this.values = new ArrayList<String>(values);  // defensive copy
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getValues() {
        return Collections.unmodifiableList(values);
    }

    public void setValues(List<String> values) {
        this.values = new ArrayList<String>(values);  // defensive copy
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
        final NotificationAttribute rslt = (NotificationAttribute) super.clone();

        // Adjust to satisfy deep-copy strategy
        rslt.setValues(new ArrayList<String>(values));

        return rslt;

    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (obj == null){
            return false;
        }
        if (getClass() != obj.getClass()){
            return false;
        }
        if(!this.getName().equalsIgnoreCase(((NotificationAttribute) obj).getName())){
            return false;
        }
        if(this.getValues().size()!=((NotificationAttribute)obj).getValues().size()){
            return false;
        }
        for(int i=0; i<this.getValues().size(); i++){
            if(!this.getValues().get(i).equals(((NotificationAttribute)obj).getValues().get(i))){
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("NotificationAttribute [name=");
        builder.append(name);
        builder.append(", values=");
        builder.append(values);
        builder.append("]");
        return builder.toString();
    }

}
