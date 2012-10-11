<%@include file="/WEB-INF/common/Taglibs.jsp" %>
<%@page session="true" %>

<!-- Main Content -->
<h1>Invite New User</h1>

<!-- Begin Error -->
<div id="error-message" class="userError">
	<c:if test="${isHasError}">
		<c:forEach var="message" items="${errorMessages}">
			<c:out value="${message}"/>
		</c:forEach>
	</c:if>
</div>
<!-- End Error -->

<div class="b-signup-form">

<c:url value="inviteNewUser.htm" var="URL"/>
<form method="post" id="f" action="${URL}" class="m-txt">

	<h2>Enter invited user's email addresses*</h2>
	<select id="emails" name="emails"></select>
	
	<input type="hidden" name="" value="submit" id="hidden_submit"/>
	
	<div class="b-sbmt-field">
		<a href="#" onclick="doInvite(this)" class="m-create-account m-short">Invite</a>
		<span class="m-italic">or</span>&nbsp; 
		<a href="#" typesubmit="cancel">Cancel</a>
	</div>

</form>
</div>
<!-- End Main Content -->
<script type="text/javascript">
$(document).ready(function(){
	$("#emails").fcbkcomplete({
	    addontab: true,                   
	    height: 2,
	    cache: true,
		filter_case: true,
		filter_hide: true,
		newel: true                    
	});
});

function doInvite(link){
	var emails = $("li.bit-box");

	var emailsToString = "";
	for( var i=0; i < emails.length; i++){
		emailsToString = emailsToString + "emailsToString=" +$(emails[i]).attr("rel") + "&";
	}
	emailsToString = emailsToString.slice(0, -1);

    $.ajax({
	    url: "<c:url value="inviteNewUserValidator.htm"/>?key=" + getRandomInt() + "&" +emailsToString,     
	    dataType: "json",                    
	    success: function (response) {
	    	if(!response[0].isHasError){
	    		$("#hidden_submit").attr('name', 'submitlink');
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
	return false;
}
</script>