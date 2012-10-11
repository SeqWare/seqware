<%@ include file="/WEB-INF/common/Taglibs.jsp" %>
[
  {
   "isHasError"  :  <c:out value="${isHasError}"/>,
   "messages":
	[
	<c:set var="messageCnt" value="${fn:length(errorMessages)}"/>
	<c:forEach items="${errorMessages}" var="message">
	  <c:set var="messageCnt" value="${messageCnt - 1}"/>
	  {
	   "text": "<c:out value="${message}"/>"
	  }<c:if test="${messageCnt > 0}">,</c:if>
	</c:forEach>
	]
  }
]