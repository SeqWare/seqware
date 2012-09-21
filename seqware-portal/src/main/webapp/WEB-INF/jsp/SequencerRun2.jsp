<%@include file="/WEB-INF/common/Taglibs.jsp" %>
<%@page session="true" %>
<%@page import="net.sourceforge.seqware.common.model.Registration" %>
<%	Registration registration = null;
	if (session != null) {registration = (Registration)session.getAttribute("registration");}
%>

<!-- Main Content -->
<div class="mainContent">

<c:if test="${strategy == 'submit'}">
<h2>Create New SequencerRun</h2>
</c:if>

<c:if test="${strategy == 'update'}">
<h2>Update SequencerRun</h2>
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

<c:url value="/sequencerRunNew.htm" var="sequencerRunURL"/>
<form:form method="post" id="f" action="${sequencerRunURL}" commandName="command">
	<h3>SequencerRun Information</h3>

	<h4>SequencerRun Name </h4>
	Use the official identifier provided by the instrument.<br />

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

	<h4>Description</h4>
	Provide a description of the overall sequencerRun.  This is the place to keep notes about the run.<br />
	<form:textarea path="description" rows="5" cols="60"/><br />

	<% if (registration.isTechnician() || registration.isLIMSAdmin()) { %>
		<h4>Cycles to Use</h4>
		e.g <i>2-26</i><br />
		<form:input path="cycles" cssClass="registration_text" maxlength="5"/>

		<h4>Reference Lane</h4>
		The lane where a standard was used.<br />
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
		<h3>Run Lanes Information</h3>
		Provide information about the samples used on each of the 8 flowcell
		lanes.<br />
	<% } %>
	<!-- ELSE -->
	<% if (!registration.isTechnician() && !registration.isLIMSAdmin()) { %>
		<h3>SequencerRun Sample Information</h3>
		Provide information at most 8 samples.<br />
	<% } %>

	<c:forTokens items="1,2 3,4 5,6 7,8" delims=" " var="table_num">
	<table>
		<tr><td />
			<c:forTokens items="${table_num}" delims="," var="lane_num">
				<td><h4><c:out value="${LaneOrSample} ${lane_num}"/></h4></td>
			</c:forTokens>
		</tr>

		<!-- //TODO// Tags, -->
		<c:forTokens items="Lane Name,Sample Name,Description,Organism,Sample Type,Regions,Skip Processing?" delims="," var="field">
		<tr><!-- Row Header -->
			<c:choose>
				<c:when test="${field == 'Skip Processing?' || field == 'Lane Name'}">
					<% if (registration.isTechnician() || registration.isLIMSAdmin()) { %>
						<td><h4><c:out value="${field}"/></h4></td>
					<% } %>
				</c:when>
				<c:otherwise>
					<td valign="top"><h4><c:out value="${field}"/></h4></td>
				</c:otherwise>
			</c:choose>
			<!-- Form fields -->
			<c:forTokens items="${table_num}" delims="," var="lane_num">
				<td><c:choose>
						<c:when test="${field == 'Lane Name'}">
							<% if (registration.isTechnician() || registration.isLIMSAdmin()) { %>
								<form:input path="lane${lane_num}.name" size="20" readonly="true"></form:input>
							<% } %>
						</c:when>
						<c:when test="${field == 'Sample Name'}">
							<form:input path="lane${lane_num}.sampleName" size="20"></form:input>
						</c:when>
						<c:when test="${field == 'Description'}">
							<form:textarea path="lane${lane_num}.description" rows="6" cols="20"/>
						</c:when>
						<c:when test="${field == 'Organism'}">
							<form:select path="lane${lane_num}.organism">
								<form:option value="H. Sapien"/>
								<form:option value="C. Pygerythrus"/>
								<form:option value="M. musculus"/>
								<form:option value="R. Norvegicus"/>
								<form:option value="D. Melanogaster"/>
								<form:option value="C. Elegans"/>
								<form:option value="S. Cerevisiae"/>
								<form:option value="other (add to description)"/>
							</form:select>
						</c:when>
						<c:when test="${field == 'Sample Type'}">
							<form:select path="lane${lane_num}.sampleType">
								<form:option value="genomic sequencing"/>
								<form:option value="cDNA sequencing"/>
								<form:option value="digital gene expression - DpnII"/>
								<form:option value="digital gene expression - NlaIII"/>
								<form:option value="other (add to description)"/>
							</form:select>
						</c:when>
						<c:when test="${field == 'Regions'}">
						<!-- - c:when test="${field == 'Tags'}" -->
						<!-- -	one per line, e.g. GATG -->
						<!-- -	form:textarea path="lane${lane_num}.tags" rows="6" cols="20"/ -->
						<!-- - / c:when -->
							one per line, e.g. chr17:27842741-28229015
							<form:textarea path="lane${lane_num}.regions" rows="6" cols="20"/>
						</c:when>
						<c:when test="${field == 'Skip Processing?'}">
							<% if (registration.isTechnician() || registration.isLIMSAdmin()) { %>
								<form:checkbox path="lane${lane_num}.skip" value="Y"/>
							<% } %>
						</c:when>
						<c:otherwise>
							<c:out value="lane${lane_num}.${field}"/>
						</c:otherwise>
					</c:choose>
				</td>
			</c:forTokens>
		</tr>
		</c:forTokens>

	</table>
	</c:forTokens>

	<h4>Ready to Process?</h4>
	<p> Check this box <b>only</b> when the sample has been fully run and the
		resulting files have been <b>fully</b> transferred to the staging area
		(currently solexa_assistant).
	</p>

	<h5>Process Now <form:checkbox path="readyToProcess" value="Y"/></h5>
	<input type="submit" name="<c:out value="${strategy}"/>" value="<spring:message code="registration.${strategy}"/>" />
	<input type="submit" name="reset" value="<spring:message code="registration.reset"/>" />
	<input type="submit" name="cancel" value="<spring:message code="registration.cancel"/>" />
</form:form>

</div>
<!-- End Main Content -->

