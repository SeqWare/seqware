<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" errorPage="/WEB-INF/jsp/ErrorPage.jsp" %>
<%@ include file="/WEB-INF/common/Taglibs.jsp" %>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title><tiles:getAsString name="title"/></title>
	
	<link rel="stylesheet" href="styles/master.css" type="text/css">
	<!--link rel="stylesheet" href="styles/jquery.treeview.css" type="text/css"/-->
	<link rel="stylesheet" href="styles/jquery.tooltip.css" />
	<link rel="stylesheet" type="text/css" href="styles/treeview.css" media="screen,projection" />
	<link rel="stylesheet" type="text/css" href="styles/report.css" media="screen,projection" />
	
  <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4/jquery.min.js"></script>
  <!--script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.2.6/jquery.min.js"></script-->
  <script type="text/javascript" src="scripts/jquery.cookie.js"></script>
  <script type="text/javascript" src="scripts/jquery.treeview.js"></script>
  <script type="text/javascript" src="scripts/jquery.treeview.async.js"></script>

  <script type="text/javascript" src="scripts/jquery.tooltip.js"></script>

  <script type="text/javascript">
  function getCurrentNodeId(){
	return "<c:out value="${sessionScope.nodeId}"/>";
  }
  
  $(document).ready(function(){
   $("#asynctree").treeview({
    collapsed: true,
    url: "<c:url value="sequencerRunListDetails.htm"/>"
   
   });
   $("#asynctree2").treeview({
    collapsed: true,
    url: "<c:url value="studyListDetails.htm"/>"
   });
  });
 
  </script>

<!-- c:url value="SequencerRunListDetails.htm"/
 url: "http://localhost:8080/SeqWareLIMS/sequencerRunListDetails.htm"
     url: "http://localhost:8080/SeqWareLIMS/studyListDetails.htm"
 -->
</head>
<body>
<div class="container">
	<tiles:insert attribute="header"/>
	<tiles:insert attribute="navigation"/>
	<tiles:insert attribute="content"/>
	<tiles:insert attribute="footer"/>
</div>
</body>
</html>
