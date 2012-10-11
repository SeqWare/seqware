<%@include file="/WEB-INF/common/Taglibs.jsp" %>
<div id="delete-popup" class="hidden">
	<div class="m-report-bundle">
		<div class="bug">
			<div class="title-popup">Delete object</div>
			<div class="close" onclick="$('#delete-popup').togglePopup(); return false;">&nbsp;</div>
			<div class="bug-inner">
		    	<form action="#" class="bug">
		    	 	<input type="hidden" name="objectId" value="" id="hidden-delete-object-id"/>
		    	 	<input type="hidden" name="tt" value="" id="hidden-type-tree"/>
		    	 	<input type="hidden" name="delete" value="submit"/>
		
		     		<div class="b-object-name"><p>
		     			<spring:message code="delete.popup.text.part1"/><br/> 
		     			<span id="delete-object-name"></span>
		     			<spring:message code="delete.popup.text.part2"/><br/>
		     			<spring:message code="delete.popup.text.part3"/>
		     		</p></div>
		     
		     		<div class="b-sbmt-field">
		     			<a href="#" id="delete-obj" onclick="onDelete(this);" class="m-create-account m-short"><spring:message code="delete.popup.link.submit"/></a>
		     			<span class="m-italic">or</span>&nbsp;
		     			<a href="#" onclick="$('#delete-popup').togglePopup(); return false;"><spring:message code="delete.popup.link.cancel"/></a>
		     		</div>
		    	</form>
	    	</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	function onDelete(link){
		$(link).closest('form').submit();
		return false;
	}
</script>