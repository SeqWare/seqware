package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.module.ReturnValue;

public interface StudyDAO {

  public Integer insert(Study sequencerRun);

  public Integer insert(Registration registration, Study sequencerRun);

  public void update(Study sequencerRun);

  public void update(Registration registration, Study study);

  public void delete(Study study);

  void merge(Study study);

  public List<Study> list(Registration registration, Boolean isAcs);

  public List<Study> listMyShared(Registration registration, Boolean isAcs);

  public List<Study> listSharedWithMe(Registration registration, Boolean isAcs);

  public Study findByTitle(String title);

  public Study findByID(Integer studyID);

  public List<File> getFiles(Integer studyId);

  public boolean isHasFile(Integer studyId);

  public List<File> getFiles(Integer studyId, String metaType);

  public List<ReturnValue> findFiles(Integer swAccession);

  public boolean isHasFile(Integer studyId, String metaType);

  public List<Study> listStudyHasFile(Registration registration, String metaType, Boolean iaAsc);

  public Study findBySWAccession(Integer swAccession);

  public List<Study> findByCriteria(String criteria, boolean isCaseSens);

  public Study updateDetached(Study study);

  public Study updateDetached(Registration registration, Study study);

  public List<Study> findByOwnerID(Integer registrationId);

  public void updateOwners(Integer swAccession);

  //
  // List search();
  public List<Study> list();

  public int getStatusCount(Study study, String running);

}
