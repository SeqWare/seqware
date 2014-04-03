package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.InvoiceState;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.Invoice;

/**
 * <p>InvoiceDAO interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface InvoiceDAO {

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
    public List<Invoice> list(Registration registration, InvoiceState state);

  /**
   * <p>findByID.</p>
   *
   * @param wfID a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.Invoice} object.
   */
  public Invoice findByID(Integer wfID);

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
    
  /**
   * <p>updateDetached.</p>
   *
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @param invoice a {@link net.sourceforge.seqware.common.model.Invoice} object.
   * @return a {@link net.sourceforge.seqware.common.model.Invoice} object.
   */
  public Invoice updateDetached(Registration registration, Invoice invoice);


}
