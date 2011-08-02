<%@ page contentType="text/html" isELIgnored="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<p class="portlet-section-note" role="note">
	You have ${fn:length(serviceData.dataRows)} items requiring your attention. 
	<c:if test="${fn:length(serviceData.dataRows) gt 0 }">
		<a href="#" id="${namespace}${serviceData.serviceKey}Button">(show)</a>
	</c:if>
</p>
						
<fmt:message key="service.${serviceData.serviceKey}.message"/>
						
<div id="${namespace}${serviceData.serviceKey}Detail" style="display: none;">
	<fmt:message key="service.${serviceData.serviceKey}.messageTop"/>
	<c:if test="${fn:length(serviceData.dataRows) gt 0 }">
		<table id="${namespace}${serviceData.serviceKey}Table">
			<tr>
				<c:forEach items="${serviceData.headerRow}" var="column">
					<th>${column}</th>
				</c:forEach>
			</tr>
			<c:forEach items="${serviceData.dataRows}" var="row">
				<tr>
					<c:forEach items="${row}" var="td">
						<td>${td}</td>
					</c:forEach>
				</tr>
			</c:forEach>
		</table>
	</c:if>
	<fmt:message key="service.${serviceData.serviceKey}.messageBottom"/>
</div>
