package net.sourceforge.seqware.common.business.impl;

import java.util.List;
import net.sourceforge.seqware.common.business.FileTypeService;
import net.sourceforge.seqware.common.dao.FileTypeDAO;
import net.sourceforge.seqware.common.model.FileType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * FileTypeServiceImpl class.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public class FileTypeServiceImpl implements FileTypeService {
    private FileTypeDAO fileTypeDAO = null;
    private static final Log LOG = LogFactory.getLog(FileTypeServiceImpl.class);

    /**
     * <p>
     * Constructor for FileTypeServiceImpl.
     * </p>
     */
    public FileTypeServiceImpl() {
        super();
    }

    /**
     * {@inheritDoc}
     * 
     * Sets a private member variable with an instance of an implementation of FileTypeDAO. This method is called by the Spring framework at
     * run time.
     * 
     * @see FileTypeDAO
     */
    @Override
    public void setFileTypeDAO(FileTypeDAO fileTypeDAO) {
        this.fileTypeDAO = fileTypeDAO;
    }

    /**
     * <p>
     * list.
     * </p>
     * 
     * @return a {@link java.util.List} object.
     */
    @Override
    public List<FileType> list() {
        return fileTypeDAO.list();
    }

    /** {@inheritDoc} */
    @Override
    public FileType findByID(Integer id) {
        FileType obj = null;
        if (id != null) {
            try {
                obj = fileTypeDAO.findByID(id);
            } catch (Exception exception) {
                LOG.error("Cannot find FileType by id " + id);
                LOG.error(exception.getMessage());
            }
        }
        return obj;
    }

    /** {@inheritDoc} */
    @Override
    public FileType updateDetached(FileType fileType) {
        return fileTypeDAO.updateDetached(fileType);
    }
}
