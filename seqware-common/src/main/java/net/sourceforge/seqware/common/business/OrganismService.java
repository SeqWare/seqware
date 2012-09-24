package net.sourceforge.seqware.common.business;

import java.util.List;

import net.sourceforge.seqware.common.dao.OrganismDAO;
import net.sourceforge.seqware.common.model.Organism;
import net.sourceforge.seqware.common.model.Registration;

public interface OrganismService {
  public static final String NAME = "OrganismService";

  public void setOrganismDAO(OrganismDAO platformDAO);

  public List<Organism> list();
  
  public List<Organism> list(Registration registration);

  public Organism findByID(Integer id);

  Organism updateDetached(Organism organism);

}
