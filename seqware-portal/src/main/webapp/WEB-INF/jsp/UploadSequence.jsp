<%@include file="/WEB-INF/common/Taglibs.jsp" %>
<%@page session="true" %>

<script>
function setSingleOrPairedVal(el){
	var val = $(el).val();
	var pairedFile = $("#upload-file-two > input:first");
	var pairedCheckbox = $("#upload-file-two > input:nth-child(3)");
	var pairedURL = $("#upload-file-two > input:last");
	if(val == "single"){
		$("#upload-file-two").addClass('hidden');
		pairedFile.attr('disabled','disabled');
		pairedCheckbox.attr('checked','');
		pairedURL.attr('disabled','disabled');
	}else{
		$("#upload-file-two").removeClass('hidden');
		if(pairedCheckbox.attr('checked')){
			pairedFile.attr('disabled','disabled');
			pairedURL.attr('disabled','');		
		}else{
			pairedFile.attr('disabled','');
			pairedURL.attr('disabled','disabled');
		}
	}
}
$(document).ready(function(){
	setSingleOrPairedVal($("#single-or-paired-end"));

	$("#single-or-paired-end").bind('change', function(){ 
		setSingleOrPairedVal(this);
	});
});
</script>

<!-- Main Content -->
<h1><spring:message code="uploadSequence.header"/></h1>

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

<c:url value="uploadSequenceNew.htm" var="uploadSequenceURL"/>
<form:form method="post" id="f" action="${uploadSequenceURL}" enctype="multipart/form-data" commandName="command" cssClass="m-txt">

	<h2><spring:message code="uploadSequence.sampleAssociated"/>*</h2>
	
	<c:if test="${strategy == 'defined_node'}">
		<input type="text" readonly="readonly" value="<c:out value="${sample.title}"/>" class="m-txt"/>
		<input type="hidden" name="currentSampleId" value="<c:out value="${sample.sampleId}"/>"/>
	</c:if>
	
	<c:if test="${strategy == 'any_node'}">
		<form:select path="sampleId" id="sampleId">
			<form:options items="${sampleList}" itemValue="sampleId" itemLabel="title"/>
		</form:select>
	</c:if>
	
	<label><spring:message code="uploadSequence.sampleAssociated.text"/><br/></label>

	<label>If you plan on uploading medium to large size files (greater than a few MB) we strongly recommend you use the Nimbus Transfer Tool.  Click <a href="http://s3.amazonaws.com/nimbusinformatics.bundles/releases/NimbusTransferTool/NimbusTransferTool-0.9/NimbusTransferTool.jnlp">here</a> to launch the tool. You must have Java Webstart installed and have the correct settings from Nimbus for this to work.  Email help@nimbusinformatics.com for more information. Once your files are uploaded you can include their URLs in the form below.<br/></label>
	
	<h2>Type*</h2>
	<form:select path="fileTypeId" id="fileTypeId">
		<form:options items="${listFileType}" itemValue="fileTypeId" itemLabel="displayName"/>
	</form:select>
	
	<h2>Paired end or single end*</h2>
	<form:select path="end" id="single-or-paired-end">
    		<form:option value="single" label="single end"/>
    		<form:option value="paired" label="paired end"/>
    	</form:select>

	<h2><spring:message code="uploadSequence.upload"/>*</h2>

	<div id="upload-files">
		<div id="upload-file">
			<input type="file" name="fileOne" size="50" class="m-txt"/> 			
			<input type="checkbox" name="useOneURL"/> Or check this box and enter a URL (http:// or s3://) directly into the database
			<input name="fileURL" size="50" disabled="disabled" class="m-txt m-middle"/>			
		</div> 
		
		<div id="upload-file-two" class="hidden">
			<h2>Paired file</h2>
			<input type="file" name="fileTwo" size="50" disabled="disabled" paired-file="true" class="m-txt"/>
			<input type="checkbox" name="useTwoURL"/> Or check this box and enter a URL (http:// or s3://) directly into the database
			<input name="fileTwoURL" size="50" disabled="disabled" class="m-txt m-middle"/>
		</div>
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
