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
<portlet:actionURL var="hideErrorUrl" escapeXml="false">
    <portlet:param name="action" value="hideError"/>
    <portlet:param name="errorKey" value="ERRORKEY"/>
</portlet:actionURL>
<portlet:actionURL var="invokeActionUrlTemplate" escapeXml="false">
    <portlet:param name="notificationId" value="NOTIFICATIONID"/>
    <portlet:param name="actionId" value="ACTIONID"/>
</portlet:actionURL>

<c:if test="${portletPreferencesValues['usePortalJsLibs'][0] != 'true'}">
    <rs:aggregatedResources path="/accordianResources.xml"/>
</c:if>
<rs:aggregatedResources path="/accordianLocalResources.xml"/>

<div id="${n}container" class="notification-portlet">

    <!-- options menu -->
    <div class="notification-options" style="display: none;">
        <p class="notification-date-filter">
            View: <a class="all" href="#">All</a> | <a class="today active" href="#">Today</a>
        </p>
        <p class="notification-refresh"><a href="#">Refresh</a></p>
    </div>

    <!-- loading -->
    <div class="notification-loading"></div>

    <!-- notifications -->
    <div class="notification-portlet-wrapper" style="display: none;">

        <!-- accordion -->
        <div class="notification-container accordion"></div>

        <!-- detail view -->
        <div class="notification-detail-wrapper" style="display: none;">
            <div class="notification-actions"></div>
            <div class="notification-detail-container"></div>
        </div>

        <!-- errors -->
        <div class="notification-error-container" style="display: none;"></div>

    </div>

</div>

<!-- call ajax on dynamic portlet id -->
<script type="text/javascript">
    var ${n} = ${n} || {};
<c:choose>
    <c:when test="${portletPreferencesValues['usePortalJsLibs'][0] != 'true'}">
        ${n}.jQuery = jQuery.noConflict(true);
        ${n}.underscore = _.noConflict();
    </c:when>
    <c:otherwise>
        ${n}.jQuery = up.jQuery;
        ${n}.underscore = up._;
    </c:otherwise>
</c:choose>
    ${n}.jQuery(document).ready(
        notificationsPortletView(${n}.jQuery, "#${n}container", ${n}.underscore, {
            invokeNotificationServiceUrl: '${invokeNotificationServiceUrl}',
            invokeActionUrlTemplate: '${invokeActionUrlTemplate}',
            getNotificationsUrl: '<portlet:resourceURL id="GET-NOTIFICATIONS"/>',
            hideErrorUrl: '${hideErrorUrl}'
        })
    );
</script>
