<%@include file="/WEB-INF/common/Taglibs.jsp" %>
<div id="stderr-popup" class="hidden">
	<div class="m-report-bundle">
		<div class="bug">
			<div class="title-popup">Details</div>
			<div class="close" onclick="$('#stderr-popup').togglePopup(); return false;">&nbsp;</div>
			<!-- div class="bug-inner" -->
                         <form>
                          <textarea rows="400" cols="100" id="stdTA"></textarea>
                         </form>
		</div>
	</div>
</div>

<script type="text/javascript">
	function onDelete(link){
		return false;
	}
</script>
