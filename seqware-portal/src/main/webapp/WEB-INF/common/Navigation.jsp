<%@include file="/WEB-INF/common/Taglibs.jsp" %>
<%@page session="false" %>
<%@page import="net.sourceforge.seqware.common.model.Registration" %>

<% javax.servlet.http.HttpSession session = request.getSession(false); %>
<% Registration registration = null; %>
<% if (session != null) {registration = (Registration)session.getAttribute("registration");} %>

<div class="sideBar" id="sidebar-1">

  <% if (registration != null) { %>
	
  <div class="sbModule">
    <h3>Studies</h3>
    <ul>
    <% if (!registration.isLIMSAdmin()) { %>
    <li><a href="<c:url value="/myStudyList.htm"/>" title="Show My Studies">Show My Studies</a></li>
    <li><a href="<c:url value="/studySetup.htm"/>" title="Create a New Study">Create New Study</a></li>
    <% } %>
    <% if (registration.isLIMSAdmin()) { %>
    <li><a href="<c:url value="/allStudyList.htm"/>" title="Show Studies">Show Studies</a></li>
    <% } %>
    <li><a href="<c:url value="/studySetup.htm"/>" title="Create a New Study">Create New Study</a></li>
    </ul>
  </div>
  
  <div class="sbModule">
    <h3>Sequencer Runs</h3>
    <ul>
    <% if (!registration.isLIMSAdmin()) { %>
    <li><a href="<c:url value="/sequencerRunList.htm"/>" title="Show My Sequencer Runs">Show My Sequencer Runs</a></li>
    <li><a href="<c:url value="/sequencerRunWizardSetup.htm"/>" title="Create a New Sequencer Run">Create a New Sequencer Run</a></li>

    <% } %>
    <% if (registration.isLIMSAdmin()) { %>
    <li><a href="<c:url value="/sequencerRunList.htm"/>" title="Show Sequencer Runs">Show Sequencer Runs</a></li>
    <li><a href="<c:url value="/sequencerRunWizardSetup.htm"/>" title="Create a New Sequencer Run">Create a New Sequencer Run</a></li>
    <% } %>
    </ul>
  </div>
  
  <!-- div class="sbModule">
    <h3>Analysis</h3>
    <ul>
    <li><a href="<c:url value="/workflowSetup.htm"/>" title="Start New Analysis">Start New Analysis</a></li>
    <li><a href="<c:url value="/workflowRunListActive.htm"/>" title="Show Active Analyses">Show Active Analyses</a></li>
    <li><a href="<c:url value="/workflowRunListAllFinished.htm"/>" title="Show Finished Analyses">Show Finished Analyses</a></li>
    <% if (registration.isLIMSAdmin()) { %>
    <li><a href="<c:url value="/workflowRunListAllActive.htm"/>" title="Show All Active Analyses">Show All Active Analyses</a></li>
    <li><a href="<c:url value="/workflowRunListAllFinished.htm"/>" title="Show All Finished Analyses">Show All Finished Analyses</a></li>
    <% } %>
    </ul>
  </div -->
	
	<% } %>

	<div class="sbModule" <% if(registration != null) { %>style="margin-bottom: 0;"<% } %> >
		<h3>Your Account</h3>
		<ul>
	<% if (registration == null) { %>
			<li><a href="<c:url value="/login.htm"/>" title="Login to your account">Login to Account</a></li>
	<% } else { %>
			<li><a href="<c:url value="/logout.htm"/>" title="Logout of your account">Logout of Account</a></li>
			<li><a href="<c:url value="/registrationEditSetup.htm"/>" title="Update your account">Update Account</a></li>
	<% } %>
			<li><a href="<c:url value="/registrationSetup.htm"/>" title="Register a new account">Register New Account</a></li>
			<li><a href="<c:url value="/signUp.htm"/>" title="Sign Up">Sign Up</a></li>
		</ul>
	</div>

	<% if (registration != null) { %>
	<div style="text-align: right; margin-bottom: 1em">
		<small><small>
			<%= registration.getFirstName() %>
			<%= registration.getLastName() %>
			<%= registration.getEmailAddress() %>
		</small></small>
	</div>
	<% } %>

</div>
