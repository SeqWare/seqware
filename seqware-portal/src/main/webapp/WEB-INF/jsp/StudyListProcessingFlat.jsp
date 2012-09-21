<%@ include file="/WEB-INF/common/Taglibs.jsp" %>

<c:forEach items="${processing.files}" var="file">
		<c:set var="res" value="${res}<span>File: SWID: ${file.swAccession} <a href='downloader.htm?fileId=${file.fileId}'>${file.jsonEscapeFileName}</a></span><br/>"/>
</c:forEach>
	
({html: [{ "text": "${res}" }] })