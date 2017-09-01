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

<%-- store a unique portlet id --%>
<c:set var="n"><portlet:namespace/></c:set>

<%-- determine the notification service url to pull the notices --%>
<portlet:actionURL var="invokeNotificationServiceUrl" escapeXml="false">
    <portlet:param name="uuid" value="${uuid}"/>
    <portlet:param name="action" value="invokeNotificationService"/>
</portlet:actionURL>
<portlet:actionURL var="invokeActionUrlTemplate" escapeXml="false">
    <portlet:param name="notificationId" value="NOTIFICATIONID"/>
    <portlet:param name="actionId" value="ACTIONID"/>
</portlet:actionURL>

<%-- are using our own JQuery libraries or uPortal --%>
<c:if test="${!usePortalJsLibs}">
    <rs:aggregatedResources path="/jQueryUIResources.xml"/>
</c:if>

<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">

<script src="<c:url value="/scripts/modal-notice.js"/>" type="text/javascript"></script>

<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">

<%-- HTML Fragment --%>

<div id="${n}emergencyAlert" class="modal fade" role="dialog" data-backdrop="static">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h2 class="title text-center" role="heading"></h2>
            </div>
            <div class="modal-body" role="main">
                <p class="body"></p>
                <a class="link" href=""></a>
                <ul class="notification-actions hidden list-inline text-center">
                  <li class="action-template hidden"><a class="btn btn-primary"
                      data-dismiss="modal" data-target="#${n}emergencyAlert"
                      href="javascript:void(0);"></a></li>
                </ul>
            </div>
        </div>
    </div>
</div>
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

        var container = $("#${n}emergencyAlert");

        upmodal_notice.show($, container, {
            invokeNotificationServiceUrl: '${invokeNotificationServiceUrl}',
            invokeActionUrlTemplate: '${invokeActionUrlTemplate}',
            getNotificationsUrl: '<portlet:resourceURL id="GET-NOTIFICATIONS-UNCATEGORIZED"/>',
            readyCallback: function() {
                if (feed && feed.length > 0) {
                    up.jQuery('#${n}emergencyAlert').on('hide.bs.modal', function (e) {
                        var link = up.jQuery('#${n}emergencyAlert').find('.notification-actions li.action a');
                        up.jQuery.get(link.attr('href'));
                    });
                    up.jQuery("#${n}emergencyAlert").modal("show");
                }
            }
        });

    });

</script>

