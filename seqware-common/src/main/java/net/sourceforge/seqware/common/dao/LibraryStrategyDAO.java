package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.LibraryStrategy;
import net.sourceforge.seqware.common.model.Registration;

public interface LibraryStrategyDAO {
  public List<LibraryStrategy> list(Registration registration);

  public LibraryStrategy findByID(Integer id);

  public LibraryStrategy updateDetached(LibraryStrategy strategy);
  
  public List<LibraryStrategy> list();

}
