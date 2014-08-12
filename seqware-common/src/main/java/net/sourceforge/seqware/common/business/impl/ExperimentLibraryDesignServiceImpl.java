package net.sourceforge.seqware.common.business.impl;

import java.util.List;
import net.sourceforge.seqware.common.business.ExperimentLibraryDesignService;
import net.sourceforge.seqware.common.dao.ExperimentLibraryDesignDAO;
import net.sourceforge.seqware.common.model.ExperimentLibraryDesign;
import net.sourceforge.seqware.common.model.Registration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * ExperimentLibraryDesignServiceImpl class.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public class ExperimentLibraryDesignServiceImpl implements ExperimentLibraryDesignService {
    private ExperimentLibraryDesignDAO dao = null;
    private static final Log LOG = LogFactory.getLog(ExperimentLibraryDesignServiceImpl.class);

    /**
     * <p>
     * Constructor for ExperimentLibraryDesignServiceImpl.
     * </p>
     */
    public ExperimentLibraryDesignServiceImpl() {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public void setExperimentLibraryDesignDAO(ExperimentLibraryDesignDAO dao) {
        this.dao = dao;
    }

    /**
     * {@inheritDoc}
     * 
     * Inserts an instance of ExperimentLibraryDesign into the database.
     */
    @Override
    public void insert(ExperimentLibraryDesign obj) {
        dao.insert(obj);
    }

    /**
     * {@inheritDoc}
     * 
     * Updates an instance of ExperimentLibraryDesign in the database.
     */
    @Override
    public void update(ExperimentLibraryDesign obj) {
        dao.update(obj);
    }

    /** {@inheritDoc} */
    @Override
    public List<ExperimentLibraryDesign> list(Registration registration) {
        return dao.list(registration);
    }

    /**
     * {@inheritDoc}
     * 
     * @param id
     */
    @Override
    public ExperimentLibraryDesign findByID(Integer id) {
        ExperimentLibraryDesign obj = null;
        if (id != null) {
            try {
                obj = dao.findByID(id);
            } catch (Exception exception) {
                LOG.error("Cannot find ExperimentLibraryDesign by id " + id);
                LOG.error(exception.getMessage());
            }
        }
        return obj;
    }

    /** {@inheritDoc} */
    @Override
    public ExperimentLibraryDesign updateDetached(ExperimentLibraryDesign eld) {
        return dao.updateDetached(eld);
    }

    /** {@inheritDoc} */
    @Override
    public List<ExperimentLibraryDesign> list() {
        return dao.list();
    }
}
