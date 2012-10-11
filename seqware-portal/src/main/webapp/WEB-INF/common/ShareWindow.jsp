 <%@include file="/WEB-INF/common/Taglibs.jsp" %>
<div id="opaco" class="hidden"></div>
<div id="popup" class="hidden"></div>

<div id="share-popup" class="hidden">
	<div class="m-report-bundle">
		<div class="bug">
			<div class="title-popup">Share object</div>
			<div class="close" onclick="$('#share-popup').togglePopup(); return false;">&nbsp;</div>
			<div class="bug-inner">
		    	<form action="#" class="bug">
		    		<input type="hidden" name="setOpenNodeId" value="" id="hidden-share-open-node-id"/>
		    		<input type="hidden" name="objectId" value="" id="hidden-share-object-id"/>
		    		<input type="hidden" name="share" value="submit"/>
		    		
		    	    <h1><spring:message code="share.popup.header"/></h1>
		    	    <div id="error-message" class="userError"></div>
		    	    
		    	    <div class="b-object-name"><span id="share-object-name"></span></div>
		    	    
		    	    <p><spring:message code="share.popup.emailsInput"/></p>
		            <select id="emails" name="emails">
		            </select>
		        
		     		<p><spring:message code="share.popup.includeMessage"/>:</p>
				    <textarea rows="5"  cols="30"></textarea>
				    
				    <div class="b-sbmt-field">
		     			<a href="#" id="share-obj" onclick="onShare(this);" class="m-create-account m-short"><spring:message code="share.popup.link.share"/></a>
		     			<span class="m-italic">or</span>&nbsp;
		     			<a href="#" onclick="$('#share-popup').togglePopup(); return false;"><spring:message code="share.popup.link.cancel"/></a>
		     		</div>
		     		
				    <!-- input type="button" value="Share" onclick="$('#popup_bug').togglePopup(); return false;" /-->
				    <!-- input type="button" value="Cancel" onclick="$('#popup_bug').togglePopup(); return false;" /-->
		     
		    	</form>	
		
		    	<script type="text/javascript">
		    	function onShare(link){
		    		var emails = $("li.bit-box");
		
		    	//	if(isValidEmails(emails)){
			    		var emailsToString = "";
			    		for( var i=0; i < emails.length; i++){
			    			emailsToString = emailsToString + "emailsToString=" +$(emails[i]).attr("rel") + "&";
						}
			    		emailsToString = emailsToString.slice(0, -1);
			
			    		var openNodeId = $("#hidden-share-open-node-id").attr('value');
			    	    $.ajax({
			    		    url: "<c:url value="shareValidator.htm"/>?key=" + getRandomInt() + "&" +emailsToString,     
			    		    data: { openNodeId: openNodeId },  
			    		    dataType: "json",                    
			    		    success: function (response) {
			    		    	if(!response[0].isHasError){
			    		    	//	$('#share_popup').togglePopup(); 
			    		        	$(link).closest('form').submit(); 
			    		    	}else{
			    		    		$('#error-message').empty();
		    		    			for(i=0; i<response[0].messages.length; i++){
		    		    				$('#error-message').append(response[0].messages[i].text + "<br>");
		    		    		 	}
			    		    	}
			    		    },
			    		    error:function (xhr, error/*ajaxOptions, thrownError*/){
			    		    //	 $('#error-message').empty().append("Error.");
			    		    	 showError(xhr, error, $("#error-message"));
			                }
			    		});
		    	//	}
		    		return false;
		    	}
		
				function isValidEmails(emails){
					var isValid = true;
		
					if(emails.length==0){
		    			$("#error-message").html("Warning: List Email is empty");
		    			isValid=false;
		    		}else{
		    			for( var i=0; i < emails.length; i++){
		    				if(!isEmailCorrect($(emails[i]).attr("rel"))){
		    					$("#error-message").html("Warning: Some emails is incorrect");
		    					isValid = false	
		    				}
		    			}
		    		}
					return isValid;
				}
			
				function isEmailCorrect(email) { 
					//var re = /^\w+([\.-]?\w+)*@(((([a-z0-9]{2,})|([a-z0-9][-][a-z0-9]+))[\.][a-z0-9])|([a-z0-9]+[-]?))+[a-z0-9]+\.([a-z]{2}|(com|net|org|edu|int|mil|gov|arpa|biz|aero|name|coop|info|pro|museum))$/i;
					var re = /(^[A-Za-z0-9_])([A-Za-z0-9_.-]*)@((([A-Za-z0-9_-]{1,})\.){1,})(([A-Za-z0-9]{1,4})$)/;
					if(re.test(email)) return true;
					else {
						return false; 
					} 
				}
		     
		    </script>
			</div>
    	</div>
	</div>
</div>