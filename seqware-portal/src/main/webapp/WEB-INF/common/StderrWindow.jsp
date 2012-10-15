<%@include file="/WEB-INF/common/Taglibs.jsp" %>
<div id="stderr-popup" class="hidden">
	<div class="m-report-bundle">
		<div class="bug">
			<div class="title-popup">Errors</div>
			<div class="close" onclick="$('#stderr-popup').togglePopup(); return false;">&nbsp;</div>
			<div class="bug-inner">
                         <form>
                          <textarea rows="100" cols="100" id="stdoutTA"></textarea>
                          <textarea rows="100" cols="100" id="stderrTA"></textarea>
                         </form>
	    	</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	function onDelete(link){
		return false;
	}
</script>
