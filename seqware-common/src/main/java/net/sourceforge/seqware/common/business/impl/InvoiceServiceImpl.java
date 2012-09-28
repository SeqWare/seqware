package net.sourceforge.seqware.common.business.impl;

import java.util.Date;
import java.util.List;
import java.util.SortedSet;

import net.sourceforge.seqware.common.business.InvoiceService;
import net.sourceforge.seqware.common.dao.InvoiceDAO;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.Invoice;
//import net.sourceforge.seqware.common.model.InvoiceParam;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class InvoiceServiceImpl implements InvoiceService {

  private InvoiceDAO invoiceDAO = null;
  private static final Log log = LogFactory.getLog(InvoiceServiceImpl.class);

  public InvoiceServiceImpl() {
    super();
  }

  /**
   * Sets a private member variable with an instance of an implementation of
   * InvoiceDAO. This method is called by the Spring framework at run time.
   * 
   * @param invoiceDAO
   *          implementation of InvoiceDAO
   * @see InvoiceDAO
   */
  public void setInvoiceDAO(InvoiceDAO invoiceDAO) {
    this.invoiceDAO = invoiceDAO;
  }

  @Override
  public Integer insert(Invoice invoice) {
    invoice.setCreateTimestamp(new Date());
    return invoiceDAO.insert(invoice);
  }

  public void update(Invoice invoice) {
    invoiceDAO.update(invoice);
  }

  public void delete(Invoice invoice) {
    invoiceDAO.delete(invoice);
  }

  public List<Invoice> list() {
    return invoiceDAO.list();
  }

  public List<Invoice> list(Registration registration) {
    return invoiceDAO.list(registration);
  }
  
  public List<Invoice> list(Registration registration, String state) {
    return invoiceDAO.list(registration, state);
  }

  public Invoice findByID(Integer iID) {
    Invoice invoice = null;
    if (iID != null) {
      try {
        invoice = invoiceDAO.findByID(iID);
      } catch (Exception exception) {
        log.error("Cannot find Invoice by wfID " + iID);
        log.error(exception.getMessage());
      }
    }
    return invoice;
  }

  @Override
  public Invoice findBySWAccession(Integer swAccession) {
    Invoice invoice = null;
    if (swAccession != null) {
      try {
        invoice = invoiceDAO.findBySWAccession(swAccession);
      } catch (Exception exception) {
        log.error("Cannot find Invoice by swAccession " + swAccession);
        log.error(exception.getMessage());
      }
    }
    return invoice;
  }

  @Override
  public Invoice updateDetached(Invoice invoice) {
    return invoiceDAO.updateDetached(invoice);
  }

  @Override
  public Integer insert(Registration registration, Invoice invoice) {
    invoice.setCreateTimestamp(new Date());
    return invoiceDAO.insert(registration, invoice);
  }

  @Override
  public Invoice updateDetached(Registration registration, Invoice invoice) {
    return invoiceDAO.updateDetached(registration, invoice);
  }

  @Override
  public void update(Registration registration, Invoice invoice) {
    invoiceDAO.update(registration, invoice);
  }
}
