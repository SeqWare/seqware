<%@include file="/WEB-INF/common/Taglibs.jsp" %>
<%@page session="true" %>

<!-- Main Content -->
<div id="opaco" class="hidden"></div>
<div id="popup" class="hidden"></div>
<jsp:include page="../common/DeleteWindow.jsp"/> 

<h1>Invoices</h1>

<!-- Begin Error -->
<div class="userError">
</div>
<!-- End Error -->
				
<p>
This page allows you view your open invoices.
</p>
                
<div class="b-signup-form">
    
        <h3>Pending Invoices</h3>
        
        <p>These invoices have not yet been finalized by Nimbus. There may be additional adjustments before the invoice is ready for payment.
            Your pending invoice will let you approximately track your currently monthly expenses and is typically updated on a nightly basis.</p>
		
        <table class="m-table-base">
            <c:if test ="${fn:length(pendingInvoiceList) == 0}"><tr><td>No pending invoices found...</td></tr></c:if>
	<c:forEach items="${pendingInvoiceList}" var="invoice">
            <tr><td><b>Invoice: SWID:${invoice.swAccession}</b></td><td>Owner: ${invoice.owner.emailAddress}</td><td>Paid Amount: $${invoice.paidAmount}</td><td>Start Date: ${invoice.startDate}</td>
                <td>[<a href="<c:url value="/invoiceDetails.htm"/>?invoiceSwAccession=${invoice.swAccession}">View</a>]</td></tr>
	</c:forEach>  
        </table>
    
       <p/>
    
	<h3>Open Invoices</h3>
        
         <p>These invoices have been finalized by Nimbus and are ready to be paid. This typically happens within a week of the end of the month.</p>
                
        <table class="m-table-base">
            <c:if test ="${fn:length(openInvoiceList) == 0}"><tr><td>No open invoices found...</td></tr></c:if>
	<c:forEach items="${openInvoiceList}" var="invoice">
            <tr><td><b>Invoice: SWID:${invoice.swAccession}</b></td><td>Owner: ${invoice.owner.emailAddress}</td><td>Paid Amount: $${invoice.paidAmount}</td><td>Start Date: ${invoice.startDate}</td>
                <td>[<a href="<c:url value="/invoiceDetails.htm"/>?invoiceSwAccession=${invoice.swAccession}">View</a>]</td></tr>
	</c:forEach>  
        </table>
                
                <p/>
                
        <h3>Closed Invoices</h3>
        
        <p>These invoices have been marked as paid and closed by Nimbus.</p>
		
        <table class="m-table-base">
            <c:if test ="${fn:length(closedInvoiceList) == 0}"><tr><td>No closed invoices found...</td></tr></c:if>
	<c:forEach items="${closedInvoiceList}" var="invoice">
            <tr><td><b>Invoice: SWID:${invoice.swAccession}</b></td><td>Owner: ${invoice.owner.emailAddress}</td><td>Paid Amount: $${invoice.paidAmount}</td><td>Start Date: ${invoice.startDate}</td>
                <td>[<a href="<c:url value="/invoiceDetails.htm"/>?invoiceSwAccession=${invoice.swAccession}">View</a>]</td></tr>
	</c:forEach>  
        </table>


</div>
<!-- End Main Content -->
