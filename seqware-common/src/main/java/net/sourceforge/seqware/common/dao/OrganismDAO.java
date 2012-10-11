package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.Organism;
import net.sourceforge.seqware.common.model.Registration;

public interface OrganismDAO {
  public List<Organism> list(Registration registration);

  public Organism findByID(Integer id);

  public Organism updateDetached(Organism organism);
  
  public List<Organism> list();
}
