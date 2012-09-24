<%@include file="/WEB-INF/common/Taglibs.jsp" %>

<!-- Main Content -->
<h1><spring:message code="search.title"/></h1>

<!-- Begin Error -->

<!-- End Error -->
<c:url value="/searchResults.htm" var="ajaxURL"/>
<input id="ajaxurl" type="hidden" value="${ajaxURL}" />
<input id="issearched" type="hidden" value="false" />
<div class="b-signup-form b-search-form">
	<c:url value="/searchResults.htm" var="URL"/>
	<form method="GET" action="${URL}" enctype="application/form-url-encoded" cssClass="m-txt">

		<div class="m-block">
			<div class="m-txt"><spring:message code="search.search.title"/></div>
			<input id="criteria" value="${criteria}" class="m-txt" />
			<label for="casesens"><spring:message code="search.case"/></label>
			<input id="casesens" type="checkbox" ${checked} />
		</div>

		<div class="m-block">
			<div class="m-txt"><spring:message code="search.type.title"/></div>
			<select id="type">
				<c:forEach var="item" items="${types}">
					<option>${item}</option>
				</c:forEach>
			<select/>
		</div>
		<div class="b-sbmt-field">
			<a id="searchbutton" href="#" class="m-create-account m-short"><spring:message code="search.go"/></a>
		</div>
	</form>
</div>

<div id="searcharea" > </div>

<script type="text/javascript">
	$(document).ready(function(){
		if ($('#issearched').val() == 'true') {

			var ajaxUrl = $('#ajaxurl').val();
			var type = $('#type').val();
			var criteria = $('#criteria').val();
			 
			IS_LOAD_FILES_FINISHED = true;
			$.ajax({
			  	url: ajaxUrl +"?type=" + type + "&criteria=" + criteria + "&key=" + getRandomInt(),
			   	success: function(data){
					$('#searcharea').html(data);
				}
			});
		}

		// If enter pressed, push the button
		$("body").keypress(function(e) {
		 	if(e.keyCode == 13) {
				$("#searchbutton").click();
			}               
		});

	});
</script>
<!-- End Main Content -->
