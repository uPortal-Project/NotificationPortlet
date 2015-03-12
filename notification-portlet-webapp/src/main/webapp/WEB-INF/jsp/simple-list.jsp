<%--

    Licensed to Apereo under one or more contributor license
    agreements. See the NOTICE file distributed with this work
    for additional information regarding copyright ownership.
    Apereo licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License.  You may obtain a
    copy of the License at the following location:

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.

--%>
<jsp:directive.include file="/WEB-INF/jsp/include.jsp"/>

<c:set var="n"><portlet:namespace/></c:set>

<portlet:actionURL var="invokeNotificationServiceUrl" escapeXml="false">
    <portlet:param name="uuid" value="${uuid}"/>
    <portlet:param name="action" value="invokeNotificationService"/>
</portlet:actionURL>
<portlet:actionURL var="invokeActionUrlTemplate" escapeXml="false">
    <portlet:param name="notificationId" value="NOTIFICATIONID"/>
    <portlet:param name="actionId" value="ACTIONID"/>
</portlet:actionURL>

<script src="<rs:resourceURL value="/rs/jquery/1.10.2/jquery-1.10.2.min.js"/>" type="text/javascript"></script>
<rs:aggregatedResources path="/simpleListLocalResources.xml"/>

<style type="text/css">
#${n}notificationListView .hidden { display: none; }
</style>

<div id="${n}notificationListView" class="notification-list-view" style="display: none;">

    <ul class="notification-list">
        <li class="notification-list-item portlet-msg-alert template hidden">
            <ul class="notification-actions hidden">
                <li class="action-template hidden"><a class="button" href="javascript:void(0);"></a></li>
            </ul>
            <div class="notification-text">
                <span class="completed-badge">&#10004;</span>
                <a class="link" href=""><span class="title"></span></a>
            </div>
        </li>
    </ul>

</div>

<script type="text/javascript">

    var ${n} = ${n} || {};
    ${n}.jQuery = jQuery.noConflict(true);

    ${n}.jQuery(function(){
        var $ = ${n}.jQuery;

        var container = $("#${n}notificationListView");

        upnotice.show($, container, { 
            invokeNotificationServiceUrl: '${invokeNotificationServiceUrl}',
            getNotificationsUrl: '<portlet:resourceURL id="GET-NOTIFICATIONS-UNCATEGORIZED"/>',
            invokeActionUrlTemplate: '${invokeActionUrlTemplate}'
        });

    });

</script>

