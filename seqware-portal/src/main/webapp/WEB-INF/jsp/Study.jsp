<%@include file="/WEB-INF/common/Taglibs.jsp" %>
<%@page session="true" %>

<!-- Main Content -->
     		
<c:if test="${strategy == 'submit'}">
	<h1><spring:message code="study.header.createNewStudy"/></h1>
</c:if>

<c:if test="${strategy == 'update'}">
	<h1><spring:message code="study.header.updateStudy"/></h1>
	<h3><spring:message code="general.header.swid"/>:<fmt:formatNumber value="${swid}" minIntegerDigits="10" pattern="##########"/></h3>
</c:if>

<!-- Begin Error -->
<div class="userError">
	<spring:bind path="command.*">
	<c:forEach items="${status.errorMessages}" var="errorMessage">
		<c:out value="${errorMessage}" /> <br />
	</c:forEach>
	</spring:bind>
</div>
<!-- End Error -->

<div class="b-signup-form">

	<c:url value="studyNew.htm" var="studyURL"/>
	<form:form method="post" id="f" action="${studyURL}" commandName="command" cssClass="m-txt">
	    
		<h2><spring:message code="study.title"/>*</h2>
		<label><spring:message code="study.title.text"/></label>
		<form:input path="title" cssClass="m-txt"/>
		
		<h2><spring:message code="study.accession"/></h2>
		<label><spring:message code="study.accession.text"/></label>
		<form:input path="accession" cssClass="m-txt"/>
		
		<h2><spring:message code="study.description"/></h2>
		<label><spring:message code="study.description.text"/></label>
		<form:textarea path="description" rows="5" cols="40"/>
		             
		<h2><spring:message code="study.abstract"/></h2>
		<label><spring:message code="study.abstract.text"/></label>
		<form:textarea path="abstractStr" rows="5" cols="40"/>
		
		<h2><spring:message code="study.studyType"/>*</h2>
		<label><spring:message code="study.studyType.text"/></label>
		<form:select path="existingTypeInt" id="existingTypeInt">
			<form:options items="${studyTypeList}" itemValue="studyTypeId" itemLabel="name"/>
		</form:select>
		
		<h2>Center Name*</h2>
		<form:input path="centerName" cssClass="m-txt"/>
		
		<h2>Center Project Name*</h2>
		<form:input path="centerProjectName" cssClass="m-txt"/>
		
		<input type="hidden" name="" value="submit" id="hidden_submit"/>
		
		<div class="b-sbmt-field">
			<c:if test="${strategy == 'submit'}">
			<a href="#" class="m-create-account m-short" typesubmit="submitlink">
				<spring:message code="study.link.submit"/>
			</a>
			</c:if>
			<c:if test="${strategy == 'update'}">
			<a href="#" class="m-create-account m-short" typesubmit="update">
				<spring:message code="study.link.update"/>
			</a>
			</c:if>
			<a href="#" class="m-create-account m-short" typesubmit="reset">
				<spring:message code="study.link.reset"/>
			</a> 
			<span class="m-italic">or</span>&nbsp; 
			<a href="#" typesubmit="cancel">
				<spring:message code="study.link.cancel"/>
			</a>
		</div>
		
	</form:form>
</div>

