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
package org.jasig.portlet.notice.filter;

import org.jasig.portlet.notice.NotificationAction;

/**
 * Class which signifies that subclasses should be considered actions that handle read state updates.
 *
 * Concretely, these classes will replace the default {MarkAsReadAndRedirectAction} added to all notices
 * by {@ReadStateSupportFilter}
 *
 * @since 4.5.4
 */
public abstract class ReadStateAction extends NotificationAction {
}
