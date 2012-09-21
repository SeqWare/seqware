
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>

  <li>Analysis: <c:out value="${node.algorithm} ${node.updateTimestamp} SWID:${node.swAccession}"/> (<c:out value="${node.status}"/>)
    <ul>
      <li><c:out value="${node.description}"/></li>
      <c:forEach items="${node.files}" var="file">
        <li>File: <a href="<c:url value="downloader.htm"/>?fileId=${file.fileId}"><c:out value="${file.fileName}"/></a> SWID: <c:out value="${file.swAccession}"/></li>
      </c:forEach>

      <c:forEach var="node" items="${node.children}">

        <c:set var="node" value="${node}" scope="request"/>
        
        <jsp:include page="hello.jsp"/>
      
      </c:forEach>
    </ul>
  </li>
