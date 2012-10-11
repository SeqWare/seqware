package net.sourceforge.seqware.common.business;

import java.util.List;

import net.sourceforge.seqware.common.dao.LibrarySelectionDAO;
import net.sourceforge.seqware.common.model.LibrarySelection;
import net.sourceforge.seqware.common.model.Registration;

public interface LibrarySelectionService {
  public static final String NAME = "LibrarySelectionService";

  public void setLibrarySelectionDAO(LibrarySelectionDAO dao);

  public List<LibrarySelection> list();
  
  public List<LibrarySelection> list(Registration registration);

  public LibrarySelection findByID(Integer id);

  LibrarySelection updateDetached(LibrarySelection librarySelection);

}
