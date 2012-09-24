<%@include file="/WEB-INF/common/Taglibs.jsp" %>
<div class="l-footer">
    <div class="b-copy"><spring:message code="footer.rightsReserved"/></div>
    <ul class="b-footer-navigation">
        <li><a href="Welcome.htm"><spring:message code="footer.home"/></a></li>
        <li><a href="http://nimbusinformatics.elasticbeanstalk.com/services.jsp"><spring:message code="footer.services"/></a></li>
        <!--li><a href="about.html"><spring:message code="footer.about"/></a></li-->
        <li><a href="http://nimbusinformatics.elasticbeanstalk.com/contact.jsp"><spring:message code="footer.contact"/></a></li>
        <!--li><a href="/"><spring:message code="footer.terms"/></a></li>
        <li><a href="/"><spring:message code="footer.privacyPolicy"/></a></li-->
    </ul>
<script language="javascript">
if(location.pathname.match(/\/blog\//))
{
	document.write('<div class="b-copy">Powered by WordPress</div>');
}
</script>
</div>
