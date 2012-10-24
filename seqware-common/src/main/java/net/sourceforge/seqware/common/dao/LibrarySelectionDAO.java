package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.LibrarySelection;
import net.sourceforge.seqware.common.model.Registration;

/**
 * <p>LibrarySelectionDAO interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface LibrarySelectionDAO {

    /**
     * <p>list.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<LibrarySelection> list();

    /**
     * <p>list.</p>
     *
     * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @return a {@link java.util.List} object.
     */
    public List<LibrarySelection> list(Registration registration);

    /**
     * <p>findByID.</p>
     *
     * @param id a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.common.model.LibrarySelection} object.
     */
    public LibrarySelection findByID(Integer id);

    /**
     * <p>updateDetached.</p>
     *
     * @param librarySelection a {@link net.sourceforge.seqware.common.model.LibrarySelection} object.
     * @return a {@link net.sourceforge.seqware.common.model.LibrarySelection} object.
     */
    public LibrarySelection updateDetached(LibrarySelection librarySelection);
}
