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

<%@ page contentType="text/html" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="rs" uri="http://www.jasig.org/resource-server" %>
<portlet:defineObjects/>
<c:set var="n"><portlet:namespace/></c:set>
<portlet:actionURL var="hideErrorUrl"><portlet:param name="action" value="hideError"/><portlet:param name="errorKey" value="ERRORKEY"/></portlet:actionURL>
<link rel="stylesheet" href="<c:url value="/styles/styles.css"/>" type="text/css" media="screen" />
<script src="<rs:resourceURL value="/rs/jquery/1.6.1/jquery-1.6.1.min.js"/>" type="text/javascript"></script>
<script src="<rs:resourceURL value="/rs/jqueryui/1.8.13/jquery-ui-1.8.13.min.js"/>" type="text/javascript"></script>
<script src="<c:url value="/scripts/underscore.min.js"/>" type="text/javascript"></script>
<script src="<c:url value="/scripts/jquery.accordion.js"/>" type="text/javascript"></script>
<script src="<c:url value="/scripts/jquery.notifications.js"/>" type="text/javascript"></script>

<!-- call ajax on dynamic portlet id -->
<script type="text/javascript">
    var ${n} = ${n} || {};
    ${n}.jQuery = jQuery.noConflict(true);
    ${n}.jQuery(document).ready(function () { 
        ${n}.jQuery("#${n}container").notifications({ 
            url: '<portlet:actionURL><portlet:param name="action" value="getNotifications"/></portlet:actionURL>',
            hideErrorUrl: '${hideErrorUrl}'
        });
    });
</script>

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
                <div class="notification-back-button">
                  <span>Back</span>
                </div>
                <div class="notification-detail-container"></div>
              </div>

              <!-- errors -->
              <div class="notification-error-container" style="display: none;"></div>
  
     </div>

</div>

