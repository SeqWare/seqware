<%@ include file="/WEB-INF/common/Taglibs.jsp" %>
[
  {
	"pageInfo": "${pageInfo.info}",
	"isStart" :	"${pageInfo.isStart}",	
	"isEnd"   :	"${pageInfo.isEnd}",
	"isSelectedInput" : ${isSelectedInput}, 
	"displayName" 	  : "<c:out value="${workflowParam.jsonEscapeDisplayName}"/>",
	"files":
	[
	<c:set var="fileCnt" value="${fn:length(files)}"/>
	<c:forEach items="${files}" var="file">
	  <c:set var="fileCnt" value="${fileCnt - 1}"/>
	  {
	   "text": "<c:out value="${file.jsonEscapeFileName}"/>"
	  }<c:if test="${fileCnt > 0}">,</c:if>
	</c:forEach>
	]
  }
]