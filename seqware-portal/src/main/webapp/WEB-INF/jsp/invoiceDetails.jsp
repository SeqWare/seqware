<%@include file="/WEB-INF/common/Taglibs.jsp" %>
<%@page session="true" %>

<!-- Main Content -->
<div id="opaco" class="hidden"></div>
<div id="popup" class="hidden"></div>
<jsp:include page="../common/DeleteWindow.jsp"/> 

<h1>Invoice SWID:${invoice.swAccession}</h1>

<!-- Begin Error -->
<div class="userError">
</div>
<!-- End Error -->
				
<p>

</p>
                
<div class="b-signup-form">
    
    <p><b>Invoice Start</b>: ${invoice.startDate}</p>
    
    <p><b>Invoice Status</b>: ${invoice.state}</p>
    
    <p><b>Invoice is Paid</b>: ${invoice.fullyPaid}</p>
    
    <p><b>Notes to Client</b>:</p><p>${invoice.notes}</p>
    
    <link rel="stylesheet" type="text/css" href="styles/jquery-ui.css"/>
    <script type="text/javascript" src="scripts/jquery-1.8.2.js"></script>
    <script type="text/javascript" src="scripts/jquery-ui.js"></script>
    
    <script>
    $(function() {
        $( "#accordion" ).accordion({
            heightStyle: "content",
            active: false,
            collapsible: true
        });
    });
    </script>

    <div id="accordion">
    <h3>
      Fixed Monthly Expenses
      <table class="m-table-base">
        <tr><th style="width: 80%">Fixed Expense Total Price</th><th>$${fixed_total_price}</th></tr>
      </table>
    </h3>
    <div>
    <c:if test ="${fixed_size == 0}">
        No fixed expenses currently reported.<p/>
    </c:if>
    <c:forEach items="${fixed}" var="expense">
        <table class="m-table-base">
            <tr><th style="width: 80%">Item (SWID:${expense.swAccession})</th><th>Price</th></tr>
            <tr><td>${expense.description} [${expense.agent}]</td><td></td></tr>
            <tr><td>${expense.totalUnits} units @ $${expense.pricePerUnitString} per unit = </td><td>$${expense.totalPriceString}</td></tr>
        </table>
        <p/>
    </c:forEach>   
    </div>
    <h3>
        Consulting Expenses
        <table class="m-table-base">
        <tr><th style="width: 80%">Consulting Expense Total Price</th><th>$${consulting_total_price}</th></tr>
        </table>
    </h3>
    <div>
    <c:if test ="${consulting_size == 0}">
        No consulting expenses currently reported. <p/>
    </c:if>
    <c:forEach items="${consulting}" var="expense">
        <table class="m-table-base">
            <tr><th style="width: 80%">Item (SWID:${expense.swAccession})</th><th>Price</th></tr>
            <tr><td>${expense.description} [${expense.agent}]</td><td></td></tr>
            <tr><td>${expense.totalUnits} units @ $${expense.pricePerUnitString} per unit = </td><td>$${expense.totalPriceString}</td></tr>
        </table>
        <p/>
    </c:forEach> 
    </div>
    <h3>
        Analysis and Storage Expenses
        <table class="m-table-base">
        <tr><th style="width: 80%">Analysis Expense Total Price</th><th>$${analysis_total_price}</th></tr>
        </table>
    </h3>
    <div>
    <c:if test ="${analysis_size == 0}">
        No analysis expenses currently reported.<p/>
    </c:if>
    <c:forEach items="${analysis}" var="expense">
        <table class="m-table-base">
            <tr><th style="width: 80%">Item (SWID:${expense.swAccession})</th><th>Price</th></tr>
            <tr><td>${expense.description} [${expense.agent}]</td><td></td></tr>
            <tr><td>${expense.totalUnits} units @ $${expense.pricePerUnitString} per unit</td><td>$${expense.totalPriceString}</td></tr>
        </table>
        <p/> 
    </c:forEach> 
    </div>
        
    <!-- end accordion -->
    </div>
    
        <p>
    <table>
        <tr><td><b>Total:</b></td><td>$${total_price}</td></tr>
        <tr><td><b>Previously Paid:</b></td><td>$${paid_amount}</td></tr>
        <tr><td><b>Total Due:</b></td><td>$${total_due_currency}</td></tr>
        <tr><td><b>Please Pay Within:</b></td><td>${invoice.daysUntilDue} days</td></tr>
    </table>
</p>
    

<c:if test ="${total_due > 0 && invoice.state eq 'open'}">
    
    <p>
        <b>Payments By Check</b><br/>
        Please mail a payment by check to:<br/>
        Nimbus Informatics LLC<br/>
        104R NC Hwy 54 West, Suite 252<br/>
        Carborro, NC 27510<br/>
        USA<br/>
    </p>
    
    <p>
        <b>Payments By Credit/Debit Card</b><br/>
        Clicking on this "Pay Now" button will take you to a secure payment form hosted by PayPal where you 
        can use a variety of payment methods. You will
        receive an email confirmation of your payment and, once validated on the
        Nimbus side, you will see the payment amount credited in the "Previously Paid"
        field above.  Fully paid invoices will be marked as "closed" typically within a week.
    </p>
    
<form action="https://www.paypal.com/cgi-bin/webscr" method="post">
<input type="hidden" name="cmd" value="_xclick">
<input type="hidden" name="business" value="Q7V6MWPCNQR7N">
<input type="hidden" name="lc" value="US">
<input type="hidden" name="item_name" value="Nimbus Informatics Invoice SWID:${invoice.swAccession}">
<input type="hidden" name="item_number" value="1">
<input type="hidden" name="button_subtype" value="services">
<input type="hidden" name="no_note" value="0">
<input type="hidden" name="cn" value="Add special instructions to the seller:">
<input type="hidden" name="no_shipping" value="2">
<input type="hidden" name="currency_code" value="USD">
<input type="hidden" name="amount" value="$${total_due_currency}">
<input type="hidden" name="bn" value="PP-BuyNowBF:btn_paynowCC_LG.gif:NonHosted">
<input type="image" src="https://www.paypalobjects.com/en_US/i/btn/btn_paynowCC_LG.gif" border="0" name="submit" alt="PayPal - The safer, easier way to pay online!">
<img alt="" border="0" src="https://www.paypalobjects.com/en_US/i/scr/pixel.gif" width="1" height="1">
</form>

</c:if>

</div>
<!-- End Main Content -->
