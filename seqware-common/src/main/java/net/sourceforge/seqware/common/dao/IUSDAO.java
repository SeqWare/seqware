package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Study;

public interface IUSDAO {

  public void insert(IUS obj);

  public void insert(Registration registration, IUS obj);

  public void update(IUS obj);

  public void update(Registration registration, IUS ius);

  public void delete(IUS obj);

  public IUS findByID(Integer id);

  public List<File> getFiles(Integer iusId);

  public boolean isHasFile(Integer iusId);

  public List<File> getFiles(Integer iusId, String metaType);

  public boolean isHasFile(Integer iusId, String metaType);

  public IUS findBySWAccession(Integer swAccession);

  public IUS updateDetached(IUS ius);

  public IUS updateDetached(Registration registration, IUS ius);

  public List<IUS> findByOwnerID(Integer registrationId);

  public List<IUS> findByCriteria(String criteria, boolean isCaseSens);

  public List<IUS> findBelongsToStudy(Study study);

  public List<IUS> find(String sequencerRunName, Integer lane, String sampleName);

  public List<IUS> list();
}
