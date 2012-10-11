package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.LibrarySelection;
import net.sourceforge.seqware.common.model.Registration;

public interface LibrarySelectionDAO {

    public List<LibrarySelection> list();

    public List<LibrarySelection> list(Registration registration);

    public LibrarySelection findByID(Integer id);

    public LibrarySelection updateDetached(LibrarySelection librarySelection);
}
