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

<%-- HTML Fragment --%>

<div id="${n}" class="modal fade" role="dialog" data-backdrop="static">
    <div class="modal-dialog">
        <div class="modal-content np-content">
            <div class="modal-header">
                <!-- Default method of closing the  dialog, in case the notice doesn't define one -->
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h2 class="np-title text-center" role="heading"></h2>
            </div>
            <div class="modal-body" role="main">
                <p class="np-body"></p>
                <a class="np-link" href=""></a>
                <ul class="np-actions list-inline text-center">
                    <!-- Template HTML for actions defined on the notice -->
                    <li class="np-action-template hidden"><a class="btn btn-primary"
                        data-dismiss="modal" data-target="#${n}"
                        href="javascript:void(0);"></a></li>
                </ul>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
(function($) {

    $(function() {
        var container = $("#${n}");

        upmodal_notice.show($, container, {
            invokeNotificationServiceUrl: '${invokeNotificationServiceUrl}',
            invokeActionUrlTemplate: '${invokeActionUrlTemplate}',
            getNotificationsUrl: '<portlet:resourceURL id="GET-NOTIFICATIONS-UNCATEGORIZED"/>',
            readyCallback: function() {
                if (feed && feed.length > 0) {
                    $('#${n}').on('hide.bs.modal', function(e) {
                        var link = $('#${n}').find('.np-actions li.np-action a');
                        $.get(link.attr('href'));
                    });
                    $("#${n}").modal("show");
                }
            }
        });

    });

})(up.jQuery);
</script>

