<%@include file="/WEB-INF/common/Taglibs.jsp" %>
<div id="stop-popup" class="hidden">
	<div class="m-report-bundle" style="width: 870px">
		<div class="bug">
			<div class="title-popup">Display Large Report Bundle?</div>
			<div class="close" close-popup>&nbsp;</div>
			<div class="bug-inner">
				<p> The report bundle '<span></span>' exceeds <c:out value="${warningSize}"/>MB in size and will take a considerable amount of time to decompress and display.</p>
				<p><i>Wondering what`s inside? Here`s a listing of all the files in the oversized bundle.</i> </p>
	                               
	            <div class="b-popup-content">
	            	Archive: <span></span>
	            	<div id="list-entity"></div>
	            </div>                       
	   		
				<p> You may choose to continue by selecting 'Display Report' or alternatively you can download the report and view it locally on your own machine. </p>
	                                    
		   		<div class="b-sbmt-field">
		   			<a href="javascript:void(0)" id="cancel-report" class="m-create-account m-short m-to-right m-light" onclick="$('#stop-popup').togglePopup();">Cancel</a>
		   			<a href="javascript:void(0)" id="no-download-report" class="m-create-account m-to-right m-light">Display Report</a>
		   			<a href="javascript:void(0)" id="yes-download-report" class="m-create-account m-to-right">Download Report</a>
		   		</div>
	   		</div>
		</div>
	</div>
</div>