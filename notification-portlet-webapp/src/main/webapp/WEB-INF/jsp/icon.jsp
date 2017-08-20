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

<c:if test="${!usePortalJsLibs}">
    <rs:aggregatedResources path="/jQueryResources.xml"/>
</c:if>
<rs:aggregatedResources path="/simpleListLocalResources.xml"/>

<style>
    #${n}notificationIcon .badge {
        transition: background-color 0.5s;
    }
    #${n}notificationIcon .badge.active {
        background-color: ${activeNotificationColor};
    }
</style>

<c:choose>
<c:when test="true">
<div id="${n}notificationIcon">
    <a href="javascript;" title="<spring:message code='view.notifications'/>"
        data-toggle="modal" data-target="#notifModal">
        <div class="badge" role="alert" aria-live="polite">
            <i class="fa ${faIcon}" aria-label="<spring:message code="notifications" />"></i>
            <span class="notification-count"></span>
        </div>
    </a>
</div>
</c:when>
<c:otherwise>
<div id="${n}notificationIcon">
    <a href="${url}" title="<spring:message code="view.notifications"/>">
        <div class="badge" role="alert" aria-live="polite">
            <i class="fa ${faIcon}" aria-label="<spring:message code="notifications" />"></i>
            <span class="notification-count"></span>
        </div>
    </a>
</div>
</c:otherwise>
</c:choose>

<script type="text/javascript">
    var ${n} = ${n} || {};
    <c:choose>
        <c:when test="${!usePortalJsLibs}">
            ${n}.jQuery = jQuery.noConflict(true);
        </c:when>
        <c:otherwise>
            <c:set var="ns"><c:if test="${ not empty portalJsNamespace }">${ portalJsNamespace }.</c:if></c:set>
            ${n}.jQuery = ${ ns }jQuery;
        </c:otherwise>
    </c:choose>

    ${n}.jQuery(function(){
        var $ = ${n}.jQuery;
        var count = upnotice.pullFeed($, {
            invokeNotificationServiceUrl: '${invokeNotificationServiceUrl}',
            getNotificationsUrl: '<portlet:resourceURL id="GET-NOTIFICATIONS-UNCATEGORIZED"/>'
        }, function(feed) {
            if (feed && feed.length > 0) {
                $('#${n}notificationIcon .notification-count').html(feed.length);
                $('#${n}notificationIcon .badge').addClass('active');
            } else {
                $('#${n}notificationIcon .notification-count').html('0');
                $('#${n}notificationIcon .badge').removeClass('active');
            }
        });

    });
</script>
