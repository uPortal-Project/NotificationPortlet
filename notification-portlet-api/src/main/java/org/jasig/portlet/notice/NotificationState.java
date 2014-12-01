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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Represents a state that a {@link NotificationEntry} is currently in, such as
 * <em>new</em>, <em>completed</em>, or <em>favorated</em>.
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY)
@JsonDeserialize(using = JsonNotificationStateDeserializer.class)
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class NotificationState implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    // Instance Members
    private String label;
    private long timestamp;

    public final String getClazz() {
        return getClass().getName();
    }

    /**
     * Label of this state in the user interface.  Implementations that need to
     * use the <code>payload</code> to generate the label should override this
     * method.
     */
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

}
