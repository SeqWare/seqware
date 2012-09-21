<%@include file="/WEB-INF/common/Taglibs.jsp" %>
<%@page session="true" %>

<!-- Main Content -->

            <div class="b-col-2">
                <h1><spring:message code="login.header"/></h1>
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
					<c:url value="login.htm" var="loginURL"/>
					<form:form method="post" action="${loginURL}" commandName="command" cssClass="m-txt">
					
						<label><spring:message code="registration.email"/>:</label>		
						<form:input path="emailAddress" maxlength="60" cssClass="m-txt" />
					
						<label><spring:message code="registration.password"/>:</label>
						<form:password path="password" maxlength="16" cssClass="m-txt" />
	
						<!--input type="hidden" name="submitlink" value="submit"/-->
						<input type="hidden" name="" value="submit" id="hidden_submit"/>
						<div class="b-sbmt-field">
							<a href="#" class="m-create-account m-short" typesubmit="submitlink"><spring:message code="registration.submit"/></a>
						</div>
					</form:form>
                </div>
            </div>

<!-- End Main Content -->  