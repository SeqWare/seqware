package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.StudyLink;

public interface StudyLinkDAO {

  public abstract void insert(StudyLink studyLink);

  public abstract void update(StudyLink studyLink);

  public abstract void delete(StudyLink studyLink);

  @SuppressWarnings("unchecked")
  public abstract List<StudyLink> findAll(Study study);
  
  public List<StudyLink> list();

}