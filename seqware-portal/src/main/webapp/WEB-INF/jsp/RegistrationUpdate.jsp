<%@include file="/WEB-INF/common/Taglibs.jsp" %>
<%@page session="true" %>

<!-- Main Content -->
<h1><spring:message code="registration.header.updateRegistration"/></h1>

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
          
	<c:url value="/registrationEdit.htm" var="registrationEditURL"/>
	<form:form method="post" id="f" action="${registrationEditURL}" commandName="command" cssClass="m-txt">
	      	
		<h2><spring:message code="registration.emailAddress"/>*</h2>
		<form:input path="emailAddress" maxlength="60" cssClass="m-txt" />
	
		<h2><spring:message code="registration.confirmEmailAddress"/>*</h2>
		<form:input path="confirmEmailAddress" maxlength="60" cssClass="m-txt" />
	
		<h2><spring:message code="registration.accountPassword"/>*</h2>
		<form:password path="password" maxlength="16" cssClass="m-txt" />
	
		<h2><spring:message code="registration.confirmPassword"/>*</h2>
		<form:password path="confirmPassword" maxlength="16" cssClass="m-txt" />
	
		<h2><spring:message code="registration.passwordHint"/>*</h2>
		<form:input path="passwordHint" maxlength="50" cssClass="m-txt" />
	
		<h2><spring:message code="registration.firstName"/>*</h2>
		<form:input path="firstName" maxlength="20"	cssClass="m-txt" />
	
		<h2><spring:message code="registration.lastName"/>*</h2>
		<form:input path="lastName" maxlength="20" cssClass="m-txt" />
	
	               	
		<input type="hidden" name="" value="submit" id="hidden_submit"/>
	                   
		<div class="b-sbmt-field">
			<a href="#" class="m-create-account m-short" typesubmit="update"><spring:message code="registration.update"/></a>
			<a href="#" class="m-create-account m-short" typesubmit="reset"><spring:message code="registration.reset"/></a>
			<span class="m-italic">or</span>&nbsp;
			<a href="#" typesubmit="cancel"><spring:message code="registration.cancel"/></a>
		</div>
				
	</form:form>
</div>
<!-- End Main Content -->