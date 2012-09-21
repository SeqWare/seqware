<%@ include file="/WEB-INF/common/Taglibs.jsp" %>

<script type="text/javascript">
function hideshow(anchor,which) {
	if (!document.getElementById)
		return false;

	if (which.style.display == "none") {
		which.style.display = "";
		anchor.innerHTML = "hide details";
	} else {
		which.style.display = "none";
		anchor.innerHTML = "show details";
	}
	return false;
}
</script>

<!-- Main Content -->
<div class="mainContent">

<h2>List Studies</h2>

<!-- Begin Error -->
<span class="userError"></span>
<!-- End Error -->

<table>
	<tr><th>Name</th><th></th><th></th></tr>
	<c:forEach items="${workflowList}" var="workflow">
	
		<tr><td><b>Study: <c:out value="${study.title}" /> SWID: <c:out value="${study.swAccession}" /> </b></td>
			<td>[ <a href="<c:url value="studyUpdateSetup.htm"/>?studyID=${study.studyId}">edit</a>
				| <a href="<c:url value="experimentSetup.htm?studyId=${study.studyId}"/>"> add experiment</a>
				| <a href="" onClick="return hideshow(this, document.getElementById('study_<c:out value="${study.studyId}"/>'));">show details</a>
				]
			</td>
			<td></td>
		</tr>

	</c:forEach>
</table>

</div>
<!-- End Main Content -->
