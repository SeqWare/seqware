<%@include file="/WEB-INF/common/Taglibs.jsp" %>
<%@page session="true" %>

<!-- Main Content -->

<h1><spring:message code="signup.header"/></h1>
<p><spring:message code="signup.header.message"/></p>

<!-- Begin Error -->
<div class="userError">
<spring:bind path="command.*">
	<c:forEach items="${status.errorMessages}" var="errorMessage">
		<c:out value="${errorMessage}" /> <br/>
	</c:forEach>
</spring:bind>
</div>
<!-- End Error -->

<div class="b-signup-form">
	<c:url value="registrationSave.htm" var="registrationURL"/>
	<form:form method="post" action="${registrationURL}" commandName="command" cssClass="m-txt">
		<label><spring:message code="registration.email"/>*:</label>		
		<form:input path="emailAddress" maxlength="60" cssClass="m-txt" />
		
		<label><spring:message code="registration.password"/>*:</label>
		<form:password path="password" maxlength="16" cssClass="m-txt" />
		
		<label><spring:message code="registration.confirmPassword"/>*:</label>
		<form:password path="confirmPassword" maxlength="16" cssClass="m-txt" />
		
		<c:if test="${isInvitationCode == 'true'}">
			<label><spring:message code="registration.invitationCode"/>*:</label>
			<form:input path="invitationCode" maxlength="32" cssClass="m-txt"/>
		</c:if>
		
		<div class="b-chck-field"><form:checkbox path="joinDevelopersMailingList" id="nimbus"/> <label for="nimbus"><spring:message code="registration.joinDevelopersMailingList"/></label></div>
		<div class="b-chck-field"><form:checkbox path="joinUsersMailingList" id="open"/> <label for="open"><spring:message code="registration.joinUsersMailingList"/></label></div>
		
		<!--input type="hidden" name="submitlink" value="submit"/-->
		<input type="hidden" name="" value="submit" id="hidden_submit"/>
		<div class="b-sbmt-field">
			<a href="#" class="m-create-account" typesubmit="submitlink"><spring:message code="registration.createAccount"/></a> 
			&nbsp;<span class="m-italic">or</span>&nbsp; 
			<a href="#" typesubmit="cancel"><spring:message code="registration.cancel"/></a>
		</div>
	</form:form>
</div>

<!-- End Main Content -->  