package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.LibrarySource;
import net.sourceforge.seqware.common.model.Registration;

public interface LibrarySourceDAO {

    public List<LibrarySource> list();

    public List<LibrarySource> list(Registration registration);

    public LibrarySource findByID(Integer id);

    public LibrarySource updateDetached(LibrarySource librarySource);
}
