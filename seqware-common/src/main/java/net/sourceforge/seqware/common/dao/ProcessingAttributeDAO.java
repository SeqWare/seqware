package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingAttribute;

public interface ProcessingAttributeDAO {

  public abstract void insert(ProcessingAttribute processingAttribute);

  public abstract void update(ProcessingAttribute processingAttribute);

  public abstract void delete(ProcessingAttribute processingAttribute);

  @SuppressWarnings("unchecked")
  public abstract List<ProcessingAttribute> findAll(Processing processing);
  
  public List<ProcessingAttribute> list();

}