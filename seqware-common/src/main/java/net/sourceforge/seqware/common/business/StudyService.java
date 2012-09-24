package net.sourceforge.seqware.common.business;

import java.util.List;

import net.sourceforge.seqware.common.dao.StudyDAO;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.module.ReturnValue;

public interface StudyService {

  public static final String NAME = "StudyService";

  public void setStudyDAO(StudyDAO studyDAO);

  public Integer insert(Study study);

  public Integer insert(Registration registration, Study study);

  public void update(Study study);

  public void update(Registration registration, Study study);

  public void updateOwners(Integer swAccession);

  public void delete(Study study, String deleteRealFiles);

  public void merge(Study study);

  public List<Study> list(Registration registration);

  public List<Study> list(Registration registration, Boolean isAcs);

  public List<Study> listMyShared(Registration registration, Boolean isAcs);

  public List<Study> listSharedWithMe(Registration registration);

  public List<Study> listSharedWithMe(Registration registration, Boolean isAcs);

  public Study findByTitle(String title);

  public Study findByID(Integer expID);

  public Study findBySWAccession(Integer swAccession);

  public List<ReturnValue> findFiles(Integer swAccession);

  public List<Study> findByOwnerID(Integer registrationId);

  public List<Study> findByCriteria(String criteria, boolean isCaseSens);

  public boolean hasTitleBeenUsed(String oldTitle, String newTitle);

  public List<File> getFiles(Integer studyId);

  public boolean isHasFile(Integer studyId);

  public List<Study> listWithHasFile(List<Study> list);

  public List<File> getFiles(Integer studyId, String metaType);

  public boolean isHasFile(Integer studyId, String metaType);

  public List<Study> listWithHasFile(List<Study> list, String metaType);

  public List<Study> listStudyHasFile(Registration registration, String metaType, Boolean isAcs);

  public Study updateDetached(Study study);

  public Study updateDetached(Registration registration, Study study);

  public List<Study> list();

  public int getRunningCount(Study study);

  public int getFinishedCount(Study study);

  public int getFailedCount(Study study);
}

// ex:sw=4:ts=4:
