package net.sourceforge.seqware.common.business;

import java.util.List;

import net.sourceforge.seqware.common.dao.ShareFileDAO;
import net.sourceforge.seqware.common.model.ShareFile;

public interface ShareFileService {

  public abstract void setShareFileDAO(ShareFileDAO dao);

  public abstract void insert(ShareFile shareFile);

  public abstract void update(ShareFile shareFile);

  public abstract void delete(ShareFile shareFile);

  public abstract ShareFile findByID(Integer shareFileID);

  public abstract List<ShareFile> findByOwnerID(Integer registrationID);

  public abstract ShareFile findBySWAccession(Integer swAccession);

  public abstract ShareFile updateDetached(ShareFile shareFile);
  
  public List<ShareFile> list();

}