package net.sourceforge.seqware.common.business;

import java.util.List;
import java.util.SortedSet;

import net.sourceforge.seqware.common.dao.IUSDAO;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Study;

public interface IUSService {

  public static final String NAME = "IUSService";

  public void setIUSDAO(IUSDAO dao);

  public void insert(IUS ius);

  public void insert(Registration registration, IUS ius);

  public void update(IUS ius);

  public void update(Registration registration, IUS ius);

  public void delete(IUS ius, String deleteRealFiles);

  public IUS findByID(Integer id);

  public IUS findBySWAccession(Integer swAccession);

  public List<File> getFiles(Integer iusId);

  public boolean isHasFile(Integer iusId);

  public List<File> getFiles(Integer iusId, String metaType);

  public boolean isHasFile(Integer iusId, String metaType);

  public SortedSet<IUS> setWithHasFile(SortedSet<IUS> list);

  public SortedSet<IUS> listWithHasFile(SortedSet<IUS> list, String metaType);

  public IUS updateDetached(IUS ius);

  public IUS updateDetached(Registration registration, IUS ius);

  public List<IUS> findByOwnerID(Integer registrationId);

  public List<IUS> findByCriteria(String criteria, boolean isCaseSens);

  public List<IUS> findBelongsToStudy(Study study);

  public List<IUS> find(String sequencerRunName, Integer lane, String sampleName);

  public List<IUS> list();
}
