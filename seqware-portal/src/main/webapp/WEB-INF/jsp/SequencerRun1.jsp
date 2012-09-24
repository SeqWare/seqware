<%@include file="/WEB-INF/common/Taglibs.jsp" %>
<%@page session="true" %>
<%@page import="net.sourceforge.seqware.common.model.Registration" %>
<%	Registration registration = null;
	if (session != null) {registration = (Registration)session.getAttribute("registration");}
%>

<!-- Main Content -->

<c:if test="${strategy == 'submit'}">
<h1><spring:message code="sequencerRun.header.createNewSequencerRun"/></h1>
</c:if>

<c:if test="${strategy == 'update'}">
<h1><spring:message code="sequencerRun.header.updateSequencerRun"/></h1>
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
	<c:url value="/sequencerRunSave.htm" var="sequencerRunURL"/>
	<form:form method="post" id="f" action="${sequencerRunURL}" commandName="command">

		<h2><spring:message code="sequencerRun.sequencerRunName"/></h2>
		<label><spring:message code="sequencerRun.sequencerRunName.text"/></label>

		<% if (registration.isTechnician() || registration.isLIMSAdmin()) { %>
			<form:input path="name" cssClass="registration_text"
					onchange="copyNameToLanesNames(this,document)"
					onkeyup="copyNameToLanesNames(this,document)"
			/><br />
		<% } %>
		<!-- ELSE -->
		<% if (!registration.isTechnician() && !registration.isLIMSAdmin()) { %>
			<form:input path="name" cssClass="registration_text"/><br />
		<% } %>

		<h2><spring:message code="sequencerRun.description"/></h2>
		<label><spring:message code="sequencerRun.description.text"/></label>
		<form:textarea path="description" rows="5" cols="60"/><br />

		<% if (registration.isTechnician() || registration.isLIMSAdmin()) { %>
			<h2>Cycles to Use</h2>
			<label>e.g <i>2-26</i></label>
			<form:input path="cycles" cssClass="registration_text" maxlength="5"/>

			<h2>Reference Lane</h2>
			<label>The lane where a standard was used.</label>
			<form:select path="refLane">
				<form:option value="1"/>
				<form:option value="2"/>
				<form:option value="3"/>
				<form:option value="4"/>
				<form:option value="5"/>
				<form:option value="6"/>
				<form:option value="7"/>
				<form:option value="8"/>
			</form:select>
		<% } %>

		<% if (registration.isTechnician() || registration.isLIMSAdmin()) { %>
			<h2>Run Lanes Information</h2>
			<label>Provide information about the samples used on each of the 8 flowcell
			lanes.</label>
		<% } %>
		<!-- ELSE -->
		<% if (!registration.isTechnician() && !registration.isLIMSAdmin()) { %>
			<h2>SequencerRun Sample Information</h2>
			<label>Provide information at most 8 samples.</label>
		<% } %>



		<h2>Ready to Process?</h2>
		<label>Check this box <b>only</b> when the sample has been fully run and the
			resulting files have been <b>fully</b> transferred to the staging area
			(currently solexa_assistant).
		</label>

		<h4>Process Now <form:checkbox path="readyToProcess" value="Y"/></h4>

		<div class="b-sbmt-field">
			<c:if test="${strategy == 'submit'}">
				<a href="#" class="m-create-account m-short" typesubmit="submitlink"><spring:message code="sequencerRun.link.submit"/></a>
			</c:if>
			<c:if test="${strategy == 'update'}">
				<a href="#" class="m-create-account m-short" typesubmit="update"><spring:message code="sequencerRun.link.update"/></a>
			</c:if>
			<a href="#" class="m-create-account m-short" typesubmit="reset"><spring:message code="sequencerRun.link.reset"/></a>
			<span class="m-italic"><spring:message code="general.link.or"/></span>&nbsp;
			<a href="#" typesubmit="cancel"><spring:message code="sequencerRun.link.cancel"/></a>
		</div>
	</form:form>
</div>
<!-- End Main Content -->
