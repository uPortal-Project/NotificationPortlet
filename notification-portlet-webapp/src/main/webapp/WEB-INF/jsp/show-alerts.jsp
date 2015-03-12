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
    <rs:aggregatedResources path="/jQueryUIResources.xml"/>
</c:if>
<script src="<c:url value="/scripts/jquery.notice.min.js"/>" type="text/javascript"></script>

<style>
#${n}emergencyAlert {
    position: relative;
    height: 190px;
}
#${n}emergencyAlert .hidden {
    display: none;
}
#${n}emergencyAlert .view-alert {
    position: absolute;
    top: 0;
    left: 0;
    padding: 36px 36px 36px 190px;
    background: url(<c:url value="/images/icon-alert.png"/>) 36px center no-repeat;
    -webkit-border-radius: 0px;
    -moz-border-radius: 0px;
    border-radius: 0px;
    color: #fff;
}
#${n}emergencyAlert .view-alert .titlebar {
    display: block;
}
#${n}emergencyAlert .view-alert .title {
    border: none;
    font-size: 200%;
    font-weight: bold;
    color: #fff;
}
#${n}emergencyAlert .view-alert a {
    color: #B2CCE1;
}
#${n}emergencyAlert .view-alert a:hover {
    color: #fff;
}
#${n}emergencyAlert .view-alert .alerts-pager {
    float: right;
}
#${n}emergencyAlert .view-alert .alerts-pager li {
    display: inline;
    list-style-type: none;
}
#${n}emergencyAlert .view-alert .alerts-pager li a {
    color: #fff;
    text-decoration: none;
    font-size: larger;
    cursor: pointer;
}
#${n}emergencyAlert .view-alert .alerts-pager li a.disabled {
    color: #ccc;
}
</style>

<div id="${n}emergencyAlert" style="display: none;">

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
        <div role="main">
            <p class="body"></p>
            <a class="link" href=""></a>
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

                // Hide all but the first one -- (NOTE:  Need both to toggle class and to manage 'display' property')
                alerts.not(':first').toggleClass('hidden').css('display', 'none');

                var advance = function() {
                    var outgoingAlert = alerts.filter(':visible');
                    var incomingAlert = outgoingAlert.next();
                    if (incomingAlert.size() == 0) {
                      // Cycle to the beginning...
                      incomingAlert = alerts.filter(':first');
                    }
                    outgoingAlert.toggle('slide', { direction: 'left' }).toggleClass('hidden');
                    incomingAlert.toggleClass('hidden').toggle('slide', { direction: 'right' });
                };

                var recede = function() {
                    var outgoingAlert = alerts.filter(':visible');
                    var incomingAlert = outgoingAlert.prev();
                    outgoingAlert.toggle('slide', { direction: 'right' }).toggleClass('hidden');
                    incomingAlert.toggleClass('hidden').toggle('slide', { direction: 'left' });
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

