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

import javax.xml.namespace.QName;

/**
 * Constants related to notification-portlet-api
 */
public final class NotificationConstants {

    public static final String NOTIFICATION_NAMESPACE = "https://source.jasig.org/schemas/portlet/notification";

    public static final String NOTIFICATION_QUERY_LOCAL_NAME = "NotificationQuery";
    public static final QName NOTIFICATION_QUERY_QNAME = new QName(NOTIFICATION_NAMESPACE, NOTIFICATION_QUERY_LOCAL_NAME);
    public static final String NOTIFICATION_QUERY_QNAME_STRING = "{" + NOTIFICATION_NAMESPACE + "}" + NOTIFICATION_QUERY_LOCAL_NAME;

    public static final String NOTIFICATION_RESULT_LOCAL_NAME = "NotificationResult";
    public static final QName NOTIFICATION_RESULT_QNAME = new QName(NOTIFICATION_NAMESPACE, NOTIFICATION_RESULT_LOCAL_NAME);
    public static final String NOTIFICATION_RESULT_QNAME_STRING = "{" + NOTIFICATION_NAMESPACE + "}" + NOTIFICATION_RESULT_LOCAL_NAME;

}
