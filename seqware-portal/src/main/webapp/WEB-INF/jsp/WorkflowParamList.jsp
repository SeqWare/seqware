<%@ include file="/WEB-INF/common/Taglibs.jsp" %>
({workflowParams: 
	[
	  {
	   "workflowFullName":	"<c:out value="${workflow.jsonEscapeFullName}"/>",
	   "workflowDesc": 		"<c:out value="${workflow.jsonEscapeDescription}"/>",
	   "isHasError":		<c:out value="${isHasError}"/>,
	   "errorMessage":		"<c:out value="${errorMessage}"/>",
	   "defaultValueIfValueEmpty":	"<c:out value="${defaultValueIfValueEmpty}"/>"
	  }
	  <c:set var="paramCnt" value="${fn:length(workflow.visibleWorkflowParams)}"/>
	  <c:forEach items="${workflow.visibleWorkflowParams}" var="pr">
	 ,{
	   "id":	"<c:out value="${pr.workflowParamId}"/>",
	   "type":	"<c:out value="${pr.jsonEscapeType}"/>",
	   "key":	"<c:out value="${pr.jsonEscapeKey}"/>",
	   "display":		"<c:out value="${pr.display}"/>",
	   "displayName":	"<c:out value="${pr.jsonEscapeDisplayName}"/>",
	   "defaultValue":	"<c:out value="${pr.jsonEscapeDefaultValue}"/>",
	   "values" :
	   [
	   	 <c:set var="valueCnt" value="${fn:length(pr.values)}"/>
	   	 <c:forEach items="${pr.values}" var="value">
	   	 	<c:set var="valueCnt" value="${valueCnt - 1}"/>
	   		{
	   			"id":			"<c:out value="${value.workflowParamValueId}"/>",
	   			"displayName":	"<c:out value="${value.jsonEscapeDisplayName}"/>",
	   			"value":		"<c:out value="${value.jsonEscapeValue}"/>"
	   		}<c:if test="${valueCnt > 0}">,</c:if>
	     </c:forEach>
	   ]
	  }
	  </c:forEach>
	]
})
