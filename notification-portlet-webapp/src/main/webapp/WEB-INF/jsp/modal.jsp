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
<portlet:actionURL name="invokeActionUrlTemplate" var="invokeActionUrlTemplate" escapeXml="false">
</portlet:actionURL>

<script type="text/javascript">
    var invokeNotificationServiceUrl = "${invokeNotificationServiceUrl}";
    var invokeActionUrl = "${invokeActionUrlTemplate}";
    var getNotifications = "<portlet:resourceURL id="GET-NOTIFICATIONS-UNCATEGORIZED"/>";
</script>


<script src="<c:url value="/scripts/vue.min.js"/>" type="text/javascript"></script>
<script src="<c:url value="/scripts/vue-resource@1.3.4"/>" type="text/javascript"></script>

<link href="<c:url value="/css/modal.css"/>" rel="stylesheet" type="text/css" />
<link href="<c:url value="/css/modal-override.css"/>" rel="stylesheet" type="text/css" />

<script type="text/x-template" id="modal-template">
  <transition name="modal">
    <div class="notification-modal-mask" tabindex="-1">
      <div :id="id" class="notification-modal-wrapper" role="alertdialog" aria-label="dialog-description">
        <div class="notification-modal-content" tabindex="0" role="document">
          <div class="notification-modal-header">
            <slot name="header">
				<button type="button" class="close np-close" data-dismiss="modal" aria-label="Close">
                    <span>&times;</span>
                </button>
            </slot>
          </div>
          <div class="notification-modal-body">
            <slot name="body">This notification has no content</slot>
            <slot name="link"></slot>
          </div>
          <div class="notification-modal-footer">
            <slot name="actions"></slot>
          </div>
        </div>
      </div>
    </div>
  </transition>
</script>

<!-- app -->
<div id="app">
    <modal :id="id" v-for="(item, index) in items" v-if="item.show" @close="gotoNext(index)">
		<h2 v-if="item.title" slot="header">{{ item.title }}</h2>
		<div v-if="item.body" slot="body" id="dialog-description">{{ item.body }}</div>
        <div v-if="item.linkText" slot="link"><a :href="item.url">{{ item.linkText }}</a></div>
		<ul v-if="item.availableActions" slot="actions" class="np-actions list-inline text-center">
			<li v-for="action in item.availableActions">
                <button class="notification-flat" v-on:click="submit(item.id, action.id, index)">{{ action.label }}</button>
			</li>
		</ul>
	</modal>
</div>

<script src="<c:url value="/scripts/modal.js"/>" type="text/javascript"></script>
