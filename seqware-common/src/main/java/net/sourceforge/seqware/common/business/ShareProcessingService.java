package net.sourceforge.seqware.common.business;

import java.util.List;

import net.sourceforge.seqware.common.dao.ShareProcessingDAO;
import net.sourceforge.seqware.common.model.ShareProcessing;

public interface ShareProcessingService {

  public abstract void setShareProcessingDAO(ShareProcessingDAO dao);

  public abstract void insert(ShareProcessing shareProcessing);

  public abstract void update(ShareProcessing shareProcessing);

  public abstract void delete(ShareProcessing shareProcessing);

  public abstract ShareProcessing findByID(Integer shareProcessingID);

  public abstract List<ShareProcessing> findByOwnerID(Integer registrationID);

  public abstract ShareProcessing findBySWAccession(Integer swAccession);

  public abstract ShareProcessing updateDetached(ShareProcessing shareProcessing);

  public List<ShareProcessing> list();
  
}