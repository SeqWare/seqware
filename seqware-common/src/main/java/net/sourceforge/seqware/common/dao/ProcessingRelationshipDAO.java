package net.sourceforge.seqware.common.dao;

import java.util.List;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingRelationship;

public interface ProcessingRelationshipDAO {

  public abstract ProcessingRelationship findByProcessings(Processing processingParent, Processing processingChild);

  public abstract void delete(ProcessingRelationship processingRelationship);

  public abstract void update(ProcessingRelationship processingRelationship);

  public abstract void insert(ProcessingRelationship processingRelationship);

  public abstract ProcessingRelationship updateDetached(ProcessingRelationship processingRelationship);
  
  public List<ProcessingRelationship> list();

}
