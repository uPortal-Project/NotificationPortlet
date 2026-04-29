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

<%--

NOTE: the modal.jsp display strategy is based on Bootstrap, which should not be loaded on the page
more that once.  This display strategy *requires* usePortalJSLibs=true (which is both the
recommended and more common approach).

--%>

<script src="<c:url value="/scripts/modal-notice.js"/>" type="text/javascript"></script>

<style>
#${n} .modal-body {
    max-height: 40rem;
    overflow-y: scroll;
}
#${n} .btn-close {
    background: transparent url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 16 16' fill='%23000'%3e%3cpath d='M.293.293a1 1 0 0 1 1.414 0L8 6.586 14.293.293a1 1 0 1 1 1.414 1.414L9.414 8l6.293 6.293a1 1 0 0 1-1.414 1.414L8 9.414l-6.293 6.293a1 1 0 0 1-1.414-1.414L6.586 8 .293 1.707a1 1 0 0 1 0-1.414z'/%3e%3c/svg%3e") center/1em auto no-repeat;
}
#${n}.modal {
    z-index: 1055;
}
</style>

<%-- HTML Fragment --%>

<div id="${n}" class="modal fade" role="dialog" data-bs-backdrop="static" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content np-content">
            <div class="modal-header">
                <h2 class="np-title text-center" role="heading"></h2>
                <!-- Default method of closing the  dialog, in case the notice doesn't define one -->
                <button type="button" class="btn-close np-close" data-bs-dismiss="modal" aria-label="Close" style="display: none;"></button>
            </div>
            <div class="modal-body" role="main">
                <!-- Use an HTML from in case the body of the notice contains form fields -->
                <form method="POST" class="np-action-form">
                    <p class="np-body"></p>
                    <a class="np-link" href=""></a>
                    <ul class="np-actions list-inline text-center">
                        <!-- Template HTML for actions defined on the notice -->
                        <li class="np-action-template d-none">
                            <a class="btn" href="javascript:void(0);"></a>
                        </li>
                    </ul>
                </form>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
(function($) {

    $(function() {
        var container = $("#${n}");

        upmodal_notice.launch($, container, {
            invokeNotificationServiceUrl: '${invokeNotificationServiceUrl}',
            invokeActionUrlTemplate: '${invokeActionUrlTemplate}',
            getNotificationsUrl: '<portlet:resourceURL id="GET-NOTIFICATIONS-UNCATEGORIZED"/>'
        });

    });

})(up.jQuery);
</script>
