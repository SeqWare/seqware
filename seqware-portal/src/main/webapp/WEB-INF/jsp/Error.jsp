<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" %>
<%@ include file="/WEB-INF/common/Taglibs.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>SpringWebApp - Error</title>
		<link rel="stylesheet" type="text/css" href="styles/reset.css" media="screen,projection" />
		<link rel="stylesheet" type="text/css" href="styles/main.css" media="screen,projection" />

		<!--[if IE 7]><link rel="stylesheet" type="text/css" media="screen,projection" href="styles/ie7.css" /><![endif]-->
		<!--[if IE 8]><link rel="stylesheet" type="text/css" media="screen,projection" href="styles/ie8.css" /><![endif]-->
	</head>
	
	<body class="m-inner m-private">
		<div class="h-base">
		    <jsp:include page="/WEB-INF/common/NimbusHeader.jsp" />
		    <div class="l-body">
		        <div class="h-content m-inner">
		     	    <div class="b-col-1">
						<jsp:include page="/WEB-INF/common/NimbusNavigation.jsp" />
		            </div>
		            <div class="b-col-2">
		            	<br/><br/><br/><br/><br/>
	            		<%@ include file="/WEB-INF/jsp/ErrorPage.jsp" %>
		            </div>
		        </div>
		    </div>
		</div>
		<jsp:include page="/WEB-INF/common/NimbusFooter.jsp" />
	</body>
</html>
