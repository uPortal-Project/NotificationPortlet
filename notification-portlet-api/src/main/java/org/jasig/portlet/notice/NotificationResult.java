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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class NotificationResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private String queryWindowId;
    private String resultWindowId;
    private NotificationResponse notificationResponse;

    public String getQueryWindowId() {
        return queryWindowId;
    }

    public void setQueryWindowId(final String queryWindowId) {
        this.queryWindowId = queryWindowId;
    }
    
    public String getResultWindowId() {
        return resultWindowId;
    }

    public void setResultWindowId(final String resultWindowId) {
        this.resultWindowId = resultWindowId;
    }

    public NotificationResponse getNotificationResponse() {
        return notificationResponse;
    }
    
    public void setNotificationResponse(final NotificationResponse notificationResponse) {
        this.notificationResponse = notificationResponse;
    }

}
