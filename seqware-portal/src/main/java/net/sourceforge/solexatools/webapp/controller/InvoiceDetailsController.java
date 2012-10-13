package net.sourceforge.solexatools.webapp.controller;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sourceforge.seqware.common.business.InvoiceService;

import net.sourceforge.seqware.common.business.WorkflowService;
import net.sourceforge.seqware.common.model.Expense;
import net.sourceforge.seqware.common.model.Invoice;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.solexatools.Security;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

/**
 * <p>InvoiceDetailsController class.</p>
 *
 * TODO: need to switch totally to BigDecimal for rounding, see http://www.javapractices.com/topic/TopicAction.do?Id=13
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public class InvoiceDetailsController  extends BaseCommandController {

	public InvoiceService invoiceService;
        private double totalPrice = 0.0;
	
	/**
	 * <p>Constructor for InvoiceDetailsController.</p>
	 */
	public InvoiceDetailsController() {
		super();
		setSupportedMethods(new String[] {METHOD_GET});
	}
        
	/** {@inheritDoc} */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest	 request, HttpServletResponse response)
		throws Exception {

                this.totalPrice = 0.0;
                
		Registration registration = Security.getRegistration(request);
		if(registration == null)
			return new ModelAndView("redirect:/login.htm");

		ModelAndView			modelAndView	= null;
		HashMap<String,Object>	model			= new HashMap<String,Object>();

                Invoice invoice = getInvoiceService().findBySWAccession(Integer.parseInt(request.getParameter("invoiceSwAccession")));
                model.put("invoice", invoice);
                
                // now figure out the three types of expenses
                Set<Expense> expenses = invoice.getExpenses();
                // fixed
                Set<Expense> fixed = new TreeSet<Expense>();
                filterExpenses(expenses, fixed, "fixed");
                model.put("fixed", fixed);
                model.put("fixed_size", fixed.size());
                model.put("fixed_total_price", round(totalExpenses(fixed)));
                
                // consulting
                Set<Expense> consulting = new TreeSet<Expense>();
                filterExpenses(expenses, consulting, "consulting");
                model.put("consulting_size", consulting.size());
                model.put("consulting", consulting);
                model.put("consulting_total_price", round(totalExpenses(consulting)));

                // analysis
                Set<Expense> analysis = new TreeSet<Expense>();
                filterExpenses(expenses, analysis, "analysis");
                model.put("analysis", analysis);
                model.put("analysis_size", analysis.size());
                model.put("analysis_total_price", round(totalExpenses(analysis)));
                
                // total price
                model.put("total_price", round(this.totalPrice));
                model.put("paid_amount", round(invoice.getPaidAmount()));
                Double totalDue = this.totalPrice - invoice.getPaidAmount();
                model.put("total_due", totalDue);

                
                NumberFormat currencyFormatter = 
                    NumberFormat.getCurrencyInstance(Locale.US);
                
                model.put("total_due_currency", round(totalDue));

		modelAndView = new ModelAndView("invoiceDetails", model);
		
		return modelAndView;
	}
        
        private Double totalExpenses(Set<Expense> expenses) {
            Double total = 0.0;
            for(Expense e : expenses) {
                total += e.getTotalPrice();
            }
            return(total);
        }
        
        private String round(Double input) {
            BigDecimal bd = new BigDecimal(input);
            return (bd.setScale(2, BigDecimal.ROUND_HALF_EVEN).toString());
        }

	/**
	 * <p>Getter for the field <code>invoiceService</code>.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.common.business.InvoiceService} object.
	 */
	public InvoiceService getInvoiceService() {
		return invoiceService;
	}

	/**
	 * <p>Setter for the field <code>invoiceService</code>.</p>
	 *
	 * @param invoiceService a {@link net.sourceforge.seqware.common.business.InvoiceService} object.
	 */
	public void setInvoiceService(InvoiceService invoiceService) {
		this.invoiceService = invoiceService;
	}
        
        private void filterExpenses(Set<Expense> expenses, Set<Expense> dest, String type) {
            for(Expense e : expenses) {
                
                if (e.getExpenseType() != null && e.getExpenseType().equals(type)) {
                    dest.add(e);
                    this.totalPrice += e.getTotalPrice();
                }
            }
        }
}
