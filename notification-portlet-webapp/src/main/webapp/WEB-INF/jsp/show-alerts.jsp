<%--

    Licensed to Jasig under one or more contributor license
    agreements. See the NOTICE file distributed with this work
    for additional information regarding copyright ownership.
    Jasig licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License. You may obtain a
    copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on
    an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied. See the License for the
    specific language governing permissions and limitations
    under the License.

--%>

<jsp:directive.include file="/WEB-INF/jsp/include.jsp"/>

<c:set var="n"><portlet:namespace/></c:set>

<portlet:actionURL var="invokeNotificationServiceUrl" escapeXml="false">
    <portlet:param name="uuid" value="${uuid}"/>
    <portlet:param name="action" value="invokeNotificationService"/>
</portlet:actionURL>

<c:if test="{!usePortalJsLibs}">
    <script src="<rs:resourceURL value="/rs/jquery/1.6.1/jquery-1.6.1.min.js"/>" type="text/javascript"></script>
</c:if>
<script src="<c:url value="/scripts/jquery.notice.js"/>" type="text/javascript"></script>
<link type="text/css" rel="stylesheet" href="<c:url value="/styles/alert.css"/>"/>

<div id="${n}emergencyAlert" class="emergency-alert" style="display: none;">

    <div class="portlet view-alert template hidden" role="section">

        <!-- Portlet Titlebar -->
        <div class="titlebar portlet-titlebar" role="sectionhead">
            <ul class="alerts-pager hidden">
                <li><a title="Previous" href="javascript:void(0);" class="alerts-previous disabled">&#171;</a></li>
                <li><a title="Next" href="javascript:void(0);" class="alerts-next disabled">&#187;</a></li>
            </ul>
            <h2 class="title" role="heading"></h2>
        </div>

        <!-- Portlet Body -->
        <div class="content portlet-content" role="main">
            <p class="body"></p>
            <a class="url" href=""></a>                                     
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

        var autoAdvance = ${autoAdvance};
        var intervalId = -1;

        upnotice.show($, container, { 
            invokeNotificationServiceUrl: '${invokeNotificationServiceUrl}',
            getNotificationsUrl: '<portlet:resourceURL id="GET-NOTIFICATIONS-UNCATEGORIZED"/>',
            readyCallback: function() {

                // Grab the alerts we just created
                var alerts = container.find('.view-alert').not('.template');

                // Hide all but the first one
                alerts.not(':first').toggleClass('hidden');

                var advance = function() {
                    var outgoingAlert = alerts.filter(':visible');
                    var incomingAlert = outgoingAlert.next();
                    if (incomingAlert.size() == 0) {
                      // Cycle to the beginning...
                      incomingAlert = alerts.filter(':first');
                    }
                    outgoingAlert.toggle('slide', { direction: 'left' });
                    incomingAlert.toggle('slide', { direction: 'right' });
                };

                var recede = function() {
                    var outgoingAlert = alerts.filter(':visible');
                    var incomingAlert = outgoingAlert.prev();
                    outgoingAlert.toggle('slide', { direction: 'right' });
                    incomingAlert.toggle('slide', { direction: 'left' });
                };

                // Show paging controls?
                if (alerts.size() > 1) {
                    alerts.find('.alerts-pager').toggleClass('hidden');
                    // All but the first should enable the previous link
                    alerts.filter(':not(:first)').find('.alerts-previous').toggleClass('disabled');
                    // All but the last should enable the next link
                    alerts.filter(':not(:last)').find('.alerts-next').toggleClass('disabled');
                    // Register click handlers
                    alerts.find('.alerts-next:not(.disabled)').click(function() {
                        advance();
                        window.clearInterval(intervalId);
                    });
                    alerts.find('.alerts-previous:not(.disabled)').click(function() {
                        recede();
                        window.clearInterval(intervalId);
                    });
                    // autoAdvance?
                    if (autoAdvance && alerts.size() > 1) {
                        intervalId = window.setInterval(advance, 10000);
                    }
                }

            }
        });

    });

</script>

