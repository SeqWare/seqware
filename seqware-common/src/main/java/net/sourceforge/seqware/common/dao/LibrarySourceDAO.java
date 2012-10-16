package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.LibrarySource;
import net.sourceforge.seqware.common.model.Registration;

/**
 * <p>LibrarySourceDAO interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface LibrarySourceDAO {

    /**
     * <p>list.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<LibrarySource> list();

    /**
     * <p>list.</p>
     *
     * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @return a {@link java.util.List} object.
     */
    public List<LibrarySource> list(Registration registration);

    /**
     * <p>findByID.</p>
     *
     * @param id a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.common.model.LibrarySource} object.
     */
    public LibrarySource findByID(Integer id);

    /**
     * <p>updateDetached.</p>
     *
     * @param librarySource a {@link net.sourceforge.seqware.common.model.LibrarySource} object.
     * @return a {@link net.sourceforge.seqware.common.model.LibrarySource} object.
     */
    public LibrarySource updateDetached(LibrarySource librarySource);
}
