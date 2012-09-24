package net.sourceforge.seqware.common.business;

import java.util.List;

import net.sourceforge.seqware.common.dao.ShareLaneDAO;
import net.sourceforge.seqware.common.model.ShareLane;

public interface ShareLaneService {

  public abstract void setShareLaneDAO(ShareLaneDAO dao);

  public abstract void insert(ShareLane shareLane);

  public abstract void update(ShareLane shareLane);

  public abstract void delete(ShareLane shareLane);

  public abstract ShareLane findByID(Integer shareLaneID);

  public abstract List<ShareLane> findByOwnerID(Integer registrationID);

  public abstract ShareLane findBySWAccession(Integer swAccession);

  public abstract ShareLane updateDetached(ShareLane shareLane);
  
  public List<ShareLane> list();

}