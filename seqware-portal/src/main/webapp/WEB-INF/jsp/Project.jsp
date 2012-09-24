<%@include file="/WEB-INF/common/Taglibs.jsp" %>
<%@page session="true" %>
<%@page import="net.sourceforge.seqware.common.model.Registration" %>
<%	Registration registration = null;
	if (session != null) {registration = (Registration)session.getAttribute("registration");}
%>

<!-- Main Content -->
<div class="mainContent">

<c:if test="${strategy == 'submit'}">
<h2>Create New Project</h2>
</c:if>

<c:if test="${strategy == 'update'}">
<h2>Update Project</h2>
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

<c:url value="/projectNew.htm" var="projectURL"/>
<form:form method="post" id="f" action="${projectURL}" commandName="command">
	<h3>Project Information</h3>
	
	A project is a collection of one or more samples that will be sequenced. Each
	sample can be sequenced one or more times depending on the nature of the sample.
	Use this form to create a project to contain related samples.

	<h4>Project Name </h4>
	A name for your project.<br />

    <form:input path="name" cssClass="registration_text" maxlength="1000"/><br />

	<h4>Description</h4>
	Provide a description of the overall project.  This is the place to keep notes about the run.<br />
	<form:textarea path="description" rows="5" cols="60"/><br />


	<input type="submit" name="<c:out value="${strategy}"/>" value="<spring:message code="registration.${strategy}"/>" />
	<input type="submit" name="reset" value="<spring:message code="registration.reset"/>" />
	<input type="submit" name="cancel" value="<spring:message code="registration.cancel"/>" />
</form:form>

</div>
<!-- End Main Content -->
