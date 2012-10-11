<%@ include file="/WEB-INF/common/Taglibs.jsp" %>

<c:forEach items="${iuses}" var="ius">
	<fmt:formatDate value='${ius.createTimestamp}' type='both' dateStyle='short' timeStyle='short' var='createdDate'/>
	<fmt:formatDate value='${ius.updateTimestamp}' type='both' dateStyle='short' timeStyle='short' var='updatedDate'/>
		
	<c:set var="res" value="${res}<span>SWID: ${ius.swAccession}; Created: ${createdDate}; Updated: ${updatedDate}</span><br/>"/>
</c:forEach>

({html: [{ "text": "${res}" }] })