<%@ include file="/WEB-INF/common/Taglibs.jsp" %>
[
<c:set var="inputCnt" value="${fn:length(inputs)}"/>
<c:forEach items="${inputs}" var="input">
  <c:set var="inputCnt" value="${inputCnt - 1}"/>
  {
   "text": "<c:out value="${input.name}"/>"
  }<c:if test="${inputCnt > 0}">,</c:if>
</c:forEach>
]