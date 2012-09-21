<%@ include file="/WEB-INF/common/Taglibs.jsp" %>
({result: 
	[{ 
		"isHasError"	:	${isHasError}, 
		"errorMessage"	:	"${errorMessage}", 
		"path"			:	"${pathToIndexPage}",
		"isStartPage"	:	${isStartPage},
		"isEndPage"		:	${isEndPage},
		"html"			:	"${html}",
		"isAborted" 	:	${isAborted},
		"zipFileName"	:	"${zipFileName}"
	}]
})