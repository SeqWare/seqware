<%@include file="/WEB-INF/common/Taglibs.jsp" %>
<%@page session="true" %>
Invoice SWID	${invoice.swAccession}
Invoice Start	${invoice.startDate}
Invoice Status	${invoice.state}
Invoice is Paid	${invoice.fullyPaid}
Notes to Client	${invoice.notes}

Fixed Monthly Expenses
Item	Price per Unit	Units	Total Price
<c:forEach items="${fixed}" var="expense">SWID:${expense.swAccession} ${expense.description} [${expense.agent}]	${expense.pricePerUnitString}	${expense.totalUnits}	${expense.totalPriceString}
</c:forEach>Fixed Expense Total Price			${fixed_total_price}

Consulting Expenses
Item	Price per Unit	Units	Total Price
<c:forEach items="${consulting}" var="expense">SWID:${expense.swAccession} ${expense.description} [${expense.agent}]	${expense.pricePerUnitString}	${expense.totalUnits}	${expense.totalPriceString}
</c:forEach>Consulting Expense Total Price			${consulting_total_price}

Analysis and Storage Expenses
Item	Price per Unit	Units	Total Price
<c:forEach items="${analysis}" var="expense">SWID:${expense.swAccession} ${expense.description} [${expense.agent}]	${expense.pricePerUnitString}	${expense.totalUnits}	${expense.totalPriceString}
</c:forEach>Analysis Expense Total Price			${analysis_total_price}

Total	${total_price}
Previously Paid	${paid_amount}
Total Due	${total_due_currency}
Please Pay Within	${invoice.daysUntilDue} days