package net.sourceforge.seqware.common.business;

import java.util.List;

import net.sourceforge.seqware.common.dao.InvoiceDAO;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.Invoice;

/**
 * <p>InvoiceService interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface InvoiceService {

  /** Constant <code>NAME="invoiceService"</code> */
  public static final String NAME = "invoiceService";

  /**
   * <p>setInvoiceDAO.</p>
   *
   * @param invoiceDAO a {@link net.sourceforge.seqware.common.dao.InvoiceDAO} object.
   */
  public void setInvoiceDAO(InvoiceDAO invoiceDAO);

  /**
   * Inserts a new Invoice and returns its sw_accession number.
   *
   * @param invoice
   *          Invoice to be inserted.
   * @return The SeqWare Accession number for the newly inserted invoice.
   */
  public Integer insert(Invoice invoice);

  /**
   * <p>insert.</p>
   *
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @param invoice a {@link net.sourceforge.seqware.common.model.Invoice} object.
   * @return a {@link java.lang.Integer} object.
   */
  public Integer insert(Registration registration, Invoice invoice);

  /**
   * <p>update.</p>
   *
   * @param invoice a {@link net.sourceforge.seqware.common.model.Invoice} object.
   */
  public void update(Invoice invoice);

  /**
   * <p>update.</p>
   *
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @param invoice a {@link net.sourceforge.seqware.common.model.Invoice} object.
   */
  public void update(Registration registration, Invoice invoice);

  /**
   * <p>delete.</p>
   *
   * @param invoice a {@link net.sourceforge.seqware.common.model.Invoice} object.
   */
  public void delete(Invoice invoice);

  /**
   * <p>list.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<Invoice> list();

  /**
   * <p>list.</p>
   *
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @return a {@link java.util.List} object.
   */
  public List<Invoice> list(Registration registration);
  
  /**
   * <p>list.</p>
   *
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @param state a {@link java.lang.String} object.
   * @return a {@link java.util.List} object.
   */
  public List<Invoice> list(Registration registration, String state);

  /**
   * <p>findByID.</p>
   *
   * @param iID a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.Invoice} object.
   */
  public Invoice findByID(Integer iID);

  /**
   * <p>updateDetached.</p>
   *
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @param invoice a {@link net.sourceforge.seqware.common.model.Invoice} object.
   * @return a {@link net.sourceforge.seqware.common.model.Invoice} object.
   */
  public Invoice updateDetached(Registration registration, Invoice invoice);

  /**
   * <p>findBySWAccession.</p>
   *
   * @param swAccession a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.Invoice} object.
   */
  public Invoice findBySWAccession(Integer swAccession);

  /**
   * <p>updateDetached.</p>
   *
   * @param invoice a {@link net.sourceforge.seqware.common.model.Invoice} object.
   * @return a {@link net.sourceforge.seqware.common.model.Invoice} object.
   */
  public Invoice updateDetached(Invoice invoice);

}
