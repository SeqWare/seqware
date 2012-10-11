<%@include file="/WEB-INF/common/Taglibs.jsp" %>
<%@page session="true" %>

<!-- Main Content -->
<h1>File Upload</h1>

<!-- Begin Error -->
<div class="userError">
	<spring:bind path="command.*">
		<c:forEach items="${status.errorMessages}" var="errorMessage">
			<c:out value="${errorMessage}"/><br/>
		</c:forEach>
	</spring:bind>
</div>
<!-- End Error -->

<div class="b-signup-form">

<c:url value="uploadFile.htm" var="URL"/>
<form:form method="post" id="f" action="${URL}" enctype="multipart/form-data" commandName="command" cssClass="m-txt">


	<c:set var="titleNode" value="Not set name"/>
	<c:if test ="${tn == 'st'}">
		<c:set var="titleNode" value="Study"/>
	</c:if>
	<c:if test ="${tn == 'exp'}">
		<c:set var="titleNode" value="Experiment"/>
	</c:if>
	<c:if test ="${tn == 'sam'}">
		<c:set var="titleNode" value="Sample"/>
	</c:if>
	<c:if test ="${tn == 'seq'}">
		<c:set var="titleNode" value="Sequence"/>
	</c:if>
	<c:if test ="${tn == 'ius'}">
		<c:set var="titleNode" value="IUS"/>
	</c:if>
	<c:if test ="${tn == 'ae'}">
		<c:set var="titleNode" value="Analysis Event"/>
	</c:if>
		<c:if test ="${tn == 'sr'}">
		<c:set var="titleNode" value="Sequencer Run"/>
	</c:if>
	
	<h2>${titleNode} associated with file upload*</h2>

	<input type="text" readonly="readonly" value="${nameNode}" class="m-txt"/>
	<input type="hidden" name="id" value="<c:out value="${id}"/>"/>
	<input type="hidden" name="tn" value="<c:out value="${tn}"/>"/>
	
        <label>If you plan on uploading medium to large size files (greater than a few MB) we strongly recommend you use the Nimbus Transfer Tool.  Click <a href="http://s3.amazonaws.com/nimbusinformatics.bundles/releases/NimbusTransferTool/NimbusTransferTool-0.9/NimbusTransferTool.jnlp">here</a> to launch the tool. You must have Java Webstart installed and have the correct settings from Nimbus for this to work.  Email help@nimbusinformatics.com for more information. Once your files are uploaded you can include their URLs in the form below.<br/></label>
	
	<h2>Type*</h2>
	
	<form:select path="fileTypeId" id="fileTypeId">
		<form:options items="${listFileType}" itemValue="fileTypeId" itemLabel="info"/>
	</form:select>
	
	<h2>File to upload</h2>
	<div id="browser-upload-file">
		<input type="file" name="file" size="50" class="m-txt"/> 
	</div>
	
	<input id="use-url-for-file-upload" name="useURL" type="checkbox"/>
	<h2>or enter the file URL into the database</h2>
	<div id="url-upload-file">
		<input name="fileURL" size="50" disabled="disabled" class="m-txt m-middle"/>
	</div>
	
	<input type="hidden" name="" value="submit" id="hidden_submit"/>
	
	<div class="b-sbmt-field">
		<a href="#" class="m-create-account m-short" typesubmit="submitlink"><spring:message code="uploadSequence.link.upload"/></a>
		<span class="m-italic">or</span>&nbsp; 
		<a href="#" typesubmit="cancel"><spring:message code="uploadSequence.link.cancel"/></a>
	</div>

</form:form>
</div>
<!-- End Main Content -->
