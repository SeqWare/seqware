package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.Invoice;

public interface InvoiceDAO {

  /**
   * Inserts a new Invoice and returns its sw_accession number.
   * 
   * @param invoice
   *          Invoice to be inserted.
   * @return The SeqWare Accession number for the newly inserted invoice.
   */
  public Integer insert(Invoice invoice);

  public Integer insert(Registration registration, Invoice invoice);

  public void update(Invoice invoice);

  public void update(Registration registration, Invoice invoice);

  public void delete(Invoice invoice);

  public List<Invoice> list();

  public List<Invoice> list(Registration registration);
  
    public List<Invoice> list(Registration registration, String state);

  public Invoice findByID(Integer wfID);

  public Invoice findBySWAccession(Integer swAccession);

  public Invoice updateDetached(Invoice invoice);
    
  public Invoice updateDetached(Registration registration, Invoice invoice);


}
