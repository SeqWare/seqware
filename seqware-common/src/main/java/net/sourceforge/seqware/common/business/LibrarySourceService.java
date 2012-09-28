package net.sourceforge.seqware.common.business;

import java.util.List;

import net.sourceforge.seqware.common.dao.LibrarySourceDAO;
import net.sourceforge.seqware.common.model.LibrarySource;
import net.sourceforge.seqware.common.model.Registration;

public interface LibrarySourceService {
  public static final String NAME = "LibrarySourceService";

  public void setLibrarySourceDAO(LibrarySourceDAO dao);

  public List<LibrarySource> list();
  
  public List<LibrarySource> list(Registration registration);

  public LibrarySource findByID(Integer id);

  LibrarySource updateDetached(LibrarySource librarySource);
}
