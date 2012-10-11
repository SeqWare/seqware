package net.sourceforge.seqware.common.business;

import java.util.List;

import net.sourceforge.seqware.common.dao.LibraryStrategyDAO;
import net.sourceforge.seqware.common.model.LibraryStrategy;
import net.sourceforge.seqware.common.model.Registration;

public interface LibraryStrategyService {
  public static final String NAME = "LibraryStrategyService";

  public void setLibraryStrategyDAO(LibraryStrategyDAO dao);

  public List<LibraryStrategy> list();
  
  public List<LibraryStrategy> list(Registration registration);

  public LibraryStrategy findByID(Integer id);

  LibraryStrategy updateDetached(LibraryStrategy strategy);

}
