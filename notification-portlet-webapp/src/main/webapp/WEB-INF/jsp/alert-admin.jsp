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

<div>
    <h2 class="title" role="heading"><spring:message code="alertAdmin.title"/></h2>

    <p class="lead bg-warning"><spring:message code="alertAdmin.text"/> <strong><spring:message code="alertAdmin.${value}"/></strong></p>
    
    <form action="<portlet:actionURL/>" method="POST">
        <input type="submit" class="btn btn-primary btn-lg" value="<spring:message code="alertAdmin.button.${value}"/>" />
    </form>

    <p><spring:message code="alertAdmin.note"/></p>
</div>

