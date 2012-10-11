<%@include file="/WEB-INF/common/Taglibs.jsp" %>
<%@page session="true" %>
<%@page import="net.sourceforge.seqware.common.model.Registration" %>
<%	Registration registration = null;
	if (session != null) {registration = (Registration)session.getAttribute("registration");}
%>

<!-- Main Content -->
<div class="mainContent">

<h2>Account Center</h2>

<h3>Register A New Account</h3>

<!-- Begin Error -->
<div class="userError">
<spring:bind path="command.*">
	<c:forEach items="${status.errorMessages}" var="errorMessage">
		<c:out value="${errorMessage}" /> <br/>
	</c:forEach>
</spring:bind>
</div>
<!-- End Error -->

<c:url value="/registrationSave.htm" var="registrationURL"/>
<form:form method="post" action="${registrationURL}" commandName="command">
<table border="0" cellspacing="0" cellpadding="0">
	<tr><td><label>* Email Address<br />
				<small><spring:message code="registration.emailDirections"/></small></label>
		</td>
		<td><form:input path="emailAddress" maxlength="60"
				cssClass="registration_text" />
		</td>
	</tr>
	<tr><td><label>* Confirm Email Address</label></td>
		<td><form:input path="confirmEmailAddress" maxlength="60"
				cssClass="registration_text" />
		</td>
	</tr>
	<tr><td><label>* Account Password<br/>
          <small><spring:message code="registration.passwordDirections"/></small>
          </label></td>
		<td><form:password path="password" maxlength="16"
				cssClass="registration_text" />
		</td>
	</tr>
	<tr><td><label>* Confirm Password</label></td>
		<td><form:password path="confirmPassword" maxlength="16"
				cssClass="registration_text" />
		</td>
	</tr>
	<tr><td><label>* Password Hint</label></td>
		<td><form:input path="passwordHint" maxlength="50"
				cssClass="registration_text" />
		</td>
	</tr>
	<tr><td><label>* First Name</label></td>
		<td><form:input path="firstName" maxlength="20"
				cssClass="registration_text" />
		</td>
	</tr>
	<tr><td><label>* Last Name</label></td>
		<td><form:input path="lastName" maxlength="20"
				cssClass="registration_text" />
		</td>
	</tr>

<% if (registration != null
	   && (registration.isTechnician() || registration.isLIMSAdmin())) { %>
	<tr><td><label>* Is LIMS Administrator?</label></td>
		<td><form:checkbox path="LIMSAdmin"
				cssClass="registration_text" />
		</td>
	</tr>
<% } %>

	<tr><td />
		<td><input type="submit"	name="submit"
				value="<spring:message code="registration.submit"/>"
			/>
			<input type="reset"		name="reset"
				value="<spring:message code="registration.reset"/>"
			/>
			<input type="submit"	name="cancel"
				value="<spring:message code="registration.cancel"/>"
			/>
		</td>
	</tr>
</table>

</form:form>

</div>
<!-- End Main Content -->
