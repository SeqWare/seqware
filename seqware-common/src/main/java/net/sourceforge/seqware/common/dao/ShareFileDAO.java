package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.ShareFile;

public interface ShareFileDAO {

  public abstract void insert(ShareFile shareFile);

  public abstract void update(ShareFile shareFile);

  public abstract void delete(ShareFile shareFile);

  @SuppressWarnings("rawtypes")
  public abstract ShareFile findByID(Integer shareFileID);

  @SuppressWarnings("unchecked")
  public abstract List<ShareFile> findByOwnerID(Integer registrationID);

  @SuppressWarnings({ "unchecked" })
  public abstract ShareFile findBySWAccession(Integer swAccession);

  public abstract ShareFile updateDetached(ShareFile shareFile);
  
  public List<ShareFile> list();

}