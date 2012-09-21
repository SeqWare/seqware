package net.sourceforge.seqware.common.business.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sourceforge.seqware.common.business.IUSService;
import net.sourceforge.seqware.common.dao.FileDAO;
import net.sourceforge.seqware.common.dao.IUSDAO;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Study;

import org.apache.log4j.Logger;

public class IUSServiceImpl implements IUSService {

  private IUSDAO dao = null;
  private FileDAO fileDAO = null;
  private Logger log;

  public IUSServiceImpl() {
    super();
    log = Logger.getLogger(IUSServiceImpl.class);
  }

  public void setIUSDAO(IUSDAO dao) {
    this.dao = dao;
  }

  public void setFileDAO(FileDAO fileDAO) {
    this.fileDAO = fileDAO;
  }

  public void insert(IUS obj) {
    obj.setCreateTimestamp(new Date());
    dao.insert(obj);
  }

  public void delete(IUS ius, String deleteRealFiles) {
    List<File> deleteFiles = null;
    if ("yes".equals(deleteRealFiles)) {
      deleteFiles = dao.getFiles(ius.getIusId());
    }

    ius.getSample().getIUS().remove(ius);
    ius.getLane().getIUS().remove(ius);

    Set<Processing> processings = ius.getProcessings();
    for (Processing processing : processings) {
      processing.getIUS().remove(ius);
    }

    dao.delete(ius);

    if ("yes".equals(deleteRealFiles)) {
      fileDAO.deleteAllWithFolderStore(deleteFiles);
    }
  }

  public void update(IUS obj) {
    dao.update(obj);
  }

  public List<File> getFiles(Integer iusId) {
    return dao.getFiles(iusId);
  }

  public List<File> getFiles(Integer iusId, String metaType) {
    return dao.getFiles(iusId, metaType);
  }

  public boolean isHasFile(Integer iusId, String metaType) {
    return dao.isHasFile(iusId, metaType);
  }

  public boolean isHasFile(Integer iusId) {
    return dao.isHasFile(iusId);
  }

  public SortedSet<IUS> setWithHasFile(SortedSet<IUS> list) {
    for (IUS ius : list) {
      ius.setIsHasFile(isHasFile(ius.getIusId()));
    }
    return list;
  }

  public SortedSet<IUS> listWithHasFile(SortedSet<IUS> list, String metaType) {
    SortedSet<IUS> result = new TreeSet<IUS>();
    for (IUS ius : list) {
      boolean isHasFile = isHasFile(ius.getIusId(), metaType);
      if (isHasFile) {
        // logger.debug("ADD IUS");
        ius.setIsHasFile(true);
        result.add(ius);
      }
    }
    return result;
  }

  public IUS findByID(Integer id) {
    IUS ius = null;
    if (id != null) {
      try {
        ius = dao.findByID(id);
      } catch (Exception exception) {
        log.error("Cannot find IUS by id " + id);
        log.error(exception.getMessage());
      }
    }
    return ius;
  }

  @Override
  public IUS findBySWAccession(Integer swAccession) {
    IUS ius = null;
    if (swAccession != null) {
      try {
        ius = dao.findBySWAccession(swAccession);
      } catch (Exception exception) {
        log.error("Cannot find IUS by swAccession " + swAccession);
        log.error(exception.getMessage());
      }
    }
    return ius;
  }

  @Override
  public List<IUS> findByOwnerID(Integer registrationId) {
    List<IUS> ius = null;
    if (registrationId != null) {
      try {
        ius = dao.findByOwnerID(registrationId);
      } catch (Exception exception) {
        log.error("Cannot find IUS by registrationId " + registrationId);
        log.error(exception.getMessage());
      }
    }
    return ius;
  }

  @Override
  public List<IUS> findByCriteria(String criteria, boolean isCaseSens) {
    return dao.findByCriteria(criteria, isCaseSens);
  }

  @Override
  public IUS updateDetached(IUS ius) {
    return dao.updateDetached(ius);
  }

  @Override
  public List<IUS> findBelongsToStudy(Study study) {
    return dao.findBelongsToStudy(study);
  }

  @Override
  public List<IUS> list() {
    return dao.list();
  }

  @Override
  public void update(Registration registration, IUS ius) {
    dao.update(registration, ius);
  }

  @Override
  public void insert(Registration registration, IUS ius) {
    ius.setCreateTimestamp(new Date());
    dao.insert(registration, ius);
  }

  @Override
  public IUS updateDetached(Registration registration, IUS ius) {
    return dao.updateDetached(registration, ius);
  }

  @Override
  public List<IUS> find(String sequencerRunName, Integer lane, String sampleName) {
    checkNotNull(sequencerRunName);
    checkNotNull(lane);
    checkState(lane > 0, "lane must greater than 0");
    return dao.find(sequencerRunName, lane, sampleName);
  }
}
