<%@include file="/WEB-INF/common/Taglibs.jsp" %>
<%@page session="false" %>
<%@page import="net.sourceforge.seqware.common.model.Registration" %>

<% javax.servlet.http.HttpSession session = request.getSession(false); %>
<% Registration registration = null; %>
<% if (session != null) {registration = (Registration)session.getAttribute("registration");} %>

<% if (registration != null) { %>

<c:if test="${isHasSelectedInputMenu}">
<div id="selected-input-bar" class="b-news m-downloads m-collapsed">

    <div class="b-news-hdr m-black m-size15 m-bold"><spring:message code="navigation.selectedInput.selectedInput"/><div class="b-expander">&nbsp;</div></div>
	<div class="b-news-content">
		<div id="workflow-param-display-name"></div>
		<ul id="study-input"></ul>
    </div>
    <div class="b-news-footer m-huge m-pad">
    	<div id="page-info-input" class="b-selected-files"></div>
	    <div id="input-pagination-links">
	        <a operation-type="first" operation="sequence-list-action" href="javascript:void(0)"><spring:message code="pagination.first"/></a>
	        <a operation-type="previous" operation="sequence-list-action" href="javascript:void(0)"><b><spring:message code="pagination.previous"/></b></a> &nbsp;&nbsp;&nbsp;
	        <a operation-type="next" operation="sequence-list-action" href="javascript:void(0)"><b><spring:message code="pagination.next"/></b></a>
	        <a operation-type="last" operation="sequence-list-action" href="javascript:void(0)"><spring:message code="pagination.last"/></a>
	    </div>
	</div>
</div>
</c:if>

<% if (!registration.isPayee()) { %>
<div class="b-news">
    <div class="b-news-hdr m-black m-size15 m-bold"><spring:message code="navigation.myStudies.myStudies"/>
        <div class="b-expander">&nbsp;</div>
    </div>
    <div class="b-news-content">
        <ul>
        	<!--li><a href="<c:url value="/myStudyList.htm"/>" class="m-current">Show Studies</a></li-->
        	<li><a href="<c:url value="/myStudyList.htm"/>"><spring:message code="navigation.myStudies.showStudies"/></a></li>
        	<li><a href="<c:url value="/studySetup.htm"/>"><spring:message code="navigation.myStudies.createStudy"/></a></li>
        	<li><a href="<c:url value="/uploadSequenceSetup.htm"/>"><spring:message code="navigation.myStudies.uploadSequence"/></a></li>
        	<li><a href="<c:url value="/myBulkDownloadList.htm"/>"><spring:message code="navigation.myStudies.bulkDownload"/></a></li>
        </ul>
    </div>
    <div class="b-news-footer"> &nbsp; </div>
</div>

<div class="b-news">
    <div class="b-news-hdr m-black m-size15 m-bold"><spring:message code="navigation.myAnalysis.myAnalysis"/>
        <div class="b-expander">&nbsp;</div>
    </div>
    <div class="b-news-content m-collapsed">
        <ul>
            <li><a href="<c:url value="/myAnalisysList.htm"/>"><spring:message code="navigation.myAnalysis.showAnalysis"/></a></li>
            <li><a href="<c:url value="/myAnalisysBulkDownloadList.htm"/>"><spring:message code="navigation.myAnalysis.bulkDownload"/></a></li>
            <% if (registration != null && registration.isLIMSAdmin()) { %>
            <li><a href="<c:url value="/manageWorkflows.htm"/>"><spring:message code="navigation.myAnalysis.manageWorkflows"/></a></li>
        	<% } %> 
            <li><a href="<c:url value="/launchWorkflowSetup.htm"/>"><spring:message code="navigation.myAnalysis.launchWorkflows"/></a></li>
            <!--li><a href="<c:url value="/workflowSetup.htm"/>">Create New Workflows</a></li-->
            <!--li><a href="javascript:void(0)">Upload Modules</a></li-->
            <!--li><a href="javascript:void(0)">Module Store</a></li-->
        </ul>
    </div>
    <div class="b-news-footer"> &nbsp; </div>
</div>

<div class="b-news">
    <div class="b-news-hdr m-black m-size15 m-bold"><spring:message code="navigation.sequencerRuns.sequencerRuns"/>
        <div class="b-expander">&nbsp;</div>
    </div>
    <div class="b-news-content m-collapsed">
        <ul>
            <li><a href="<c:url value="/sequencerRunList.htm"/>"><spring:message code="navigation.sequencerRuns.showSequencerRuns"/></a></li>
            <li><a href="<c:url value="/sequencerRunWizardSetup.htm"/>"><spring:message code="navigation.sequencerRuns.createSequencerRun"/></a></li>
        </ul>
    </div>
    <div class="b-news-footer"> &nbsp; </div>
</div>

<!--div class="b-news">
    <div class="b-news-hdr m-black m-size15 m-bold"><spring:message code="navigation.myReports.myReports"/>
        <div class="b-expander">&nbsp;</div>
    </div>
    <div class="b-news-content m-collapsed">
        <ul>
            <li><a href="<c:url value="/studyReport.htm"/>"><spring:message code="navigation.myReports.studyReport"/></a></li>
        </ul>
    </div>
    <div class="b-news-footer"> &nbsp; </div>
</div-->

<div class="b-news">
    <div class="b-news-hdr m-black m-size15 m-bold"><spring:message code="navigation.search.searchEntity"/>
        <div class="b-expander">&nbsp;</div>
    </div>
    <div class="b-news-content">
        <ul>
       		<li><a href="<c:url value="/search.htm"/>"><spring:message code="navigation.search.search"/></a></li>
        </ul>
    </div>
    <div class="b-news-footer"> &nbsp; </div>
</div>
                
<% } %>

<div class="b-news">
    <div class="b-news-hdr m-black m-size15 m-bold"><spring:message code="navigation.invoice.invoiceBox"/>
        <div class="b-expander">&nbsp;</div>
    </div>
    <div class="b-news-content">
        <ul>
       		<li><a href="<c:url value="/invoicesOpen.htm"/>"><spring:message code="navigation.invoice.open"/></a></li>
        </ul>
    </div>
    <div class="b-news-footer"> &nbsp; </div>
</div>

<% } %>

<% if (registration != null && registration.isLIMSAdmin()) { %>
<div class="b-news">
    <div class="b-news-hdr m-black m-size15 m-bold"><spring:message code="navigation.report.reportBox"/>
        <div class="b-expander">&nbsp;</div>
    </div>
    <div class="b-news-content">
        <ul>
       		<li><a href="<c:url value="/reportStudySetup.htm"/>"><spring:message code="navigation.report.study"/></a></li>
       		<li><a href="<c:url value="/reportSeqRunSetup.htm"/>"><spring:message code="navigation.report.seq.run"/></a></li>
        </ul>
    </div>
    <div class="b-news-footer"> &nbsp; </div>
</div>

<% } %>

<% if (registration != null && registration.isLIMSAdmin()) { %>
<div class="b-news">
    <div class="b-news-hdr m-black m-size15 m-bold"><spring:message code="navigation.manageUsers.manageUsers"/>
        <div class="b-expander">&nbsp;</div>
    </div>
    <div class="b-news-content">
        <ul>
       		<li><a href="<c:url value="/inviteNewUserSetup.htm"/>"><spring:message code="navigation.manageUsers.inviteNewUser"/></a></li>
       		<!--li><a href="<c:url value="/manageUsers.htm"/>">Manage Users</a></li-->
        </ul>
    </div>
    <div class="b-news-footer"> &nbsp; </div>
</div>
<% } %>  

<div class="b-news">
    <div class="b-news-hdr m-black m-size15 m-bold"><spring:message code="navigation.myAccount.myAccount"/>
        <div class="b-expander">&nbsp;</div>
    </div>
    <div class="b-news-content">
        <ul>
        	<% if (registration == null) { %>
        		<li><a href="<c:url value="/login.htm"/>"><spring:message code="navigation.myAccount.login"/></a></li>
        		<li><a href="<c:url value="/signUp.htm"/>"><spring:message code="navigation.myAccount.signup"/></a></li>
        	<% } %>
        	<% if (registration != null) { %>
            	<li><a href="<c:url value="/registrationEditSetup.htm"/>"><spring:message code="navigation.myAccount.accountInformation"/></a></li>
            	<!--li><a href="javascript:void(0)"><spring:message code="navigation.myAccount.accountActivity"/></a></li-->
            	<!--li><a href="javascript:void(0)"><spring:message code="navigation.myAccount.usageReports"/></a></li-->
            	<li><a href="<c:url value="/logout.htm"/>"><spring:message code="navigation.myAccount.logout"/></a></li>
			<% } %>            
        </ul>
    </div>
    <div class="b-news-footer"> &nbsp; </div>
</div>

<% if (registration != null && !registration.isPayee()) { %>
<div id="selected-download-bar" class="b-news m-downloads m-collapsed">
    <div class="b-news-hdr m-black m-size15 m-bold"><spring:message code="navigation.selectedDownloads.selectedDownloads"/> <div class="b-expander">&nbsp;</div></div>
	<div class="b-news-content">
			<ul id="study-file"></ul>
    </div>
    <div class="b-news-footer m-huge">
    	<div id="page-info-file" class="b-selected-files"></div>
	    <div id="file-pagination-links">
	        <a operation-type="first" operation="file-list-action" href="javascript:void(0)"><spring:message code="pagination.first"/></a>
	        <a operation-type="previous" operation="file-list-action" href="javascript:void(0)"><b><spring:message code="pagination.previous"/></b></a> &nbsp;&nbsp;&nbsp;
	        <a operation-type="next" operation="file-list-action" href="javascript:void(0)"><b><spring:message code="pagination.next"/></b></a>
	        <a operation-type="last" operation="file-list-action" href="javascript:void(0)"><spring:message code="pagination.last"/></a>
	    </div>
	    <div class="btn-holder">
	        <a id="start-download" onclick="return false" class="btn-start-download" href="<c:url value="/bulkDownloader.htm"/>"><spring:message code="navigation.selectedDownloads.startDownload"/></a> 
	        <span class="m-italic">or</span> 
	        <a id="cancel-download" href="javascript:void(0)"><spring:message code="navigation.selectedDownloads.cancel"/></a>
	    </div>
	</div>
</div>
<% } %>
