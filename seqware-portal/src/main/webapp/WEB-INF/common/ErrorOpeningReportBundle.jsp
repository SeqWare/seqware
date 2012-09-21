<%@include file="/WEB-INF/common/Taglibs.jsp" %>
<div id="error-opening-report-bundle-popup" class="hidden">
	<div class="m-report-bundle">
		<div class="bug">
			<div class="title-popup">Error Opening Report Bundle</div>
			<div class="close" close-popup>&nbsp;</div>
			<div class="bug-inner">
				<br/>
				<div class="b-object-name"><p>
					The report bundle '<span id="name-error-report-bundle"></span>' does not contain<br/>the required 'index.html' file and cannot be opened.
				</p></div>
				<br/>
		   		<div class="b-sbmt-field">
		    		<a href="javascript:void(0)" id="error-opening-report-bundle-ok" class="m-create-account m-to-right m-short" onclick="$('#error-opening-report-bundle-popup').togglePopup();">Ok</a>
		   		</div>
		   	</div>
		</div>
	</div>
</div>