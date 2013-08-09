package net.sourceforge.seqware.common.dao.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.seqware.common.dao.InvoiceDAO;
import net.sourceforge.seqware.common.model.InvoiceState;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.Invoice;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.NullBeanUtils;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * <p>InvoiceDAOHibernate class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class InvoiceDAOHibernate extends HibernateDaoSupport implements InvoiceDAO {

  /**
   * <p>Constructor for InvoiceDAOHibernate.</p>
   */
  public InvoiceDAOHibernate() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  public Integer insert(Invoice invoice) {
    this.getHibernateTemplate().save(invoice);
    this.getSession().flush();
    return invoice.getSwAccession();
  }

  /** {@inheritDoc} */
  public void update(Invoice invoice) {
    getHibernateTemplate().update(invoice);
    getSession().flush();
  }

  /** {@inheritDoc} */
  public void delete(Invoice invoice) {
    getHibernateTemplate().delete(invoice);
  }

  /**
   * <p>list.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<Invoice> list() {
    ArrayList<Invoice> invoices = new ArrayList<Invoice>();

    List expmts = null;

    // Limit the invoices to those owned by the user
    expmts = this.getHibernateTemplate()
        .find("from Invoice as invoice order by invoice.createTimestamp");

    // expmts =
    // this.getHibernateTemplate().find("from Invoice as invoice order by invoice.name desc");
    for (Object invoice : expmts) {
      invoices.add((Invoice) invoice);
    }
    return invoices;
  }

  /** {@inheritDoc} */
  public List<Invoice> list(Registration registration) {
    ArrayList<Invoice> invoices = new ArrayList<Invoice>();

    // Limit the invoices to those owned by the user
    String query = "";
    Object[] parameters = { registration.getRegistrationId() };

    if (registration.isLIMSAdmin()) {
      query = "from Invoice as invoice order by createTimestamp";
      parameters = null;
    } else {
      query = "from Invoice as invoice where invoice.owner.registrationId=? order by createTimestamp";
    }

    List list = this.getHibernateTemplate().find(query, parameters);

    for (Object invoice : list) {
      invoices.add((Invoice) invoice);
    }
    return invoices;
  }
  
  /** {@inheritDoc} */
  public List<Invoice> list(Registration registration, InvoiceState state) {
    ArrayList<Invoice> invoices = new ArrayList<Invoice>();

    // Limit the invoices to those owned by the user
    String query = "";
    Object[] parameters = null;

    if (registration.isLIMSAdmin()) {
      query = "from Invoice as invoice where invoice.state = ? order by createTimestamp";
      parameters = new Object[]{ state.name() };
    } else {
      query = "from Invoice as invoice where invoice.owner.registrationId=? and invoice.state = ? order by createTimestamp";
      parameters = new Object[]{ registration.getRegistrationId(), state.name() };
    }

    List list = this.getHibernateTemplate().find(query, parameters);

    for (Object invoice : list) {
      invoices.add((Invoice) invoice);
    }
    return invoices;
  }

 

  /** {@inheritDoc} */
  public Invoice findByID(Integer wfID) {
    String query = "from Invoice as invoice where invoice.invoiceId = ?";
    Invoice invoice = null;
    Object[] parameters = { wfID };
    List list = this.getHibernateTemplate().find(query, parameters);
    if (list.size() > 0) {
      invoice = (Invoice) list.get(0);
    }
    return invoice;
  }

  /** {@inheritDoc} */
  @Override
  public Invoice findBySWAccession(Integer swAccession) {
    String query = "from Invoice as invoice where invoice.swAccession = ?";
    Invoice invoice = null;
    Object[] parameters = { swAccession };
    List<Invoice> list = this.getHibernateTemplate().find(query, parameters);
    if (list.size() > 0) {
      invoice = (Invoice) list.get(0);
    } else
    {
        Log.error("Could not find invoice of swaccession = "+swAccession);
    }
    return invoice;
  }


  /** {@inheritDoc} */
  @Override
  public Invoice updateDetached(Invoice invoice) {
    Invoice dbObject = findByID(invoice.getInvoiceId());
    try {
      BeanUtilsBean beanUtils = new NullBeanUtils();
      beanUtils.copyProperties(dbObject, invoice);
      return (Invoice) this.getHibernateTemplate().merge(dbObject);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public void update(Registration registration, Invoice invoice) {
    Invoice dbObject = reattachInvoice(invoice);
    Logger logger = Logger.getLogger(InvoiceDAOHibernate.class);
    if (registration == null) {
      logger.error("InvoiceDAOHibernate update: Registration is null - exiting");
    } else if (registration.isLIMSAdmin()
        || (invoice.givesPermission(registration) && dbObject.givesPermission(registration))) {
      logger.info("Updating invoice object");
      update(invoice);
    } else {
      logger.error("InvoiceDAOHibernate update: Registration is incorrect - exiting");
    }
  }

  /** {@inheritDoc} */
  @Override
  public Integer insert(Registration registration, Invoice invoice) {
    Logger logger = Logger.getLogger(InvoiceDAOHibernate.class);
    if (registration == null) {
      logger.error("InvoiceDAOHibernate insert: Registration is null - exiting");
    } else {
      logger.info("insert invoice object");
      return insert(invoice);
    }
    return null;

  }

  /** {@inheritDoc} */
  @Override
  public Invoice updateDetached(Registration registration, Invoice invoice) {
    Invoice dbObject = reattachInvoice(invoice);
    Logger logger = Logger.getLogger(InvoiceDAOHibernate.class);
    if (registration == null) {
      logger.error("InvoiceDAOHibernate updateDetached: Registration is null - exiting");
    } else if (registration.isLIMSAdmin() || dbObject.givesPermission(registration)) {
      logger.info("updateDetached invoice object");
      return updateDetached(invoice);
    } else {
      logger.error("InvoiceDAOHibernate updateDetached: Registration is incorrect - exiting");
    }
    return null;
  }

  private Invoice reattachInvoice(Invoice invoice) throws IllegalStateException {
    Invoice dbObject = invoice;
    if (!getSession().contains(invoice)) {
      dbObject = findByID(invoice.getInvoiceId());
    }
    return dbObject;
  }
}
