<%@ include file="/WEB-INF/common/Taglibs.jsp" %>
<%@ taglib prefix="function" uri="http://seqware-portal/taglibs/tagutils"%>

<c:set var="res" value="" scope="request"/>
<c:set var="res" value="${res}<ul>" scope="request"/>
<c:url var="URL" value="entity.htm"/>
<c:forEach items="${results}" var="entity">
	<c:if test="${function:instanceOf(entity, 'net.sourceforge.seqware.common.model.Study')}">
		<c:set var="res" value="${res}<li class='noline'><a href='${URL}?sw=${entity.swAccession}'>Study: ${entity.jsonEscapeTitle}, SWID: ${entity.swAccession}</a><span class='m-description'>${entity.jsonEscapeDescription}</span></li>" />
	</c:if>
	<c:if test="${function:instanceOf(entity, 'net.sourceforge.seqware.common.model.Experiment')}">
		<c:set var="res" value="${res}<li class='noline'><a href='${URL}?sw=${entity.swAccession}'>Experiment: ${entity.jsonEscapeTitle}, Name: ${entity.jsonEscapeName}, SWID: ${entity.swAccession}</a><span class='m-description'>${entity.jsonEscapeDescription}</span></li>" />
	</c:if>
	<c:if test="${function:instanceOf(entity, 'net.sourceforge.seqware.common.model.Sample')}">
		<c:set var="res" value="${res}<li class='noline'><a href='${URL}?sw=${entity.swAccession}'>Sample: ${entity.jsonEscapeTitle}, Name: ${entity.jsonEscapeName}, SWID: ${entity.swAccession} </a><span class='m-description'>${entity.jsonEscapeDescription}</span></li>" />
	</c:if>
	<c:if test="${function:instanceOf(entity, 'net.sourceforge.seqware.common.model.IUS')}">
		<c:set var="res" value="${res}<li class='noline'><a href='${URL}?sw=${entity.swAccession}'>IUS: ${entity.jsonEscapeName}, SWID: ${entity.swAccession}</a><span class='m-description'>${entity.jsonEscapeDescription}</span></li>" />
	</c:if>
	<c:if test="${function:instanceOf(entity, 'net.sourceforge.seqware.common.model.SequencerRun')}">
		<c:set var="res" value="${res}<li class='noline'><a href='${URL}?sw=${entity.swAccession}'>SequencerRun: ${entity.jsonEscapeName}, SWID: ${entity.swAccession}</a><span class='m-description'>${entity.jsonEscapeDescription}</span></li>" />
	</c:if>
	<c:if test="${function:instanceOf(entity, 'net.sourceforge.seqware.common.model.Lane')}">
		<c:set var="res" value="${res}<li class='noline'><a href='${URL}?sw=${entity.swAccession}'>Lane: ${entity.jsonEscapeName}, SWID: ${entity.swAccession}</a><span class='m-description'>${entity.jsonEscapeDescription}</span> </li>" />
	</c:if>
	<c:if test="${function:instanceOf(entity, 'net.sourceforge.seqware.common.model.Processing')}">
		<c:set var="res" value="${res}<li class='noline'><a href='${URL}?sw=${entity.swAccession}'>Processing: ${entity.jsonEscapeAlgorithm}, SWID: ${entity.swAccession}</a><span class='m-description'>${entity.jsonEscapeDescription}</span></li>" />
	</c:if>
	<c:if test="${function:instanceOf(entity, 'net.sourceforge.seqware.common.model.File')}">
		<c:set var="res" value="${res}<li class='noline'><a href='${URL}?sw=${entity.swAccession}'>File: ${entity.jsonEscapeFileName}, SWID: ${entity.swAccession}</a><span class='m-description'>${entity.jsonEscapeDescription}</span> </li>" />
	</c:if>
	<c:if test="${function:instanceOf(entity, 'net.sourceforge.seqware.common.model.Workflow')}">
		<c:set var="res" value="${res}<li class='noline'><a href='${URL}?sw=${entity.swAccession}'>Workflow: ${entity.jsonEscapeName}, SWID: ${entity.swAccession}</a><span class='m-description'>${entity.jsonEscapeDescription}</span> </li>" />
	</c:if>
	<c:if test="${function:instanceOf(entity, 'net.sourceforge.seqware.common.model.WorkflowRun')}">
		<c:set var="res" value="${res}<li class='noline'><a href='${URL}?sw=${entity.swAccession}'>WorkflowRun: ${entity.jsonEscapeName}, SWID: ${entity.swAccession}</a></li>" />
	</c:if>
	
</c:forEach>
<c:set var="res" value="${res}</ul>" scope="request"/>

({html: 
	[
		{
			"pageInfo": "${pageInfo.info}",
			"isStart" : "${pageInfo.isStart}",
			"isEnd" : "${pageInfo.isEnd}",
			"isHasError" : ${isHasError},
			"errorMessage"	:	"${errorMessage}",
			"text": "${res}"
	 	}  
	]
})
