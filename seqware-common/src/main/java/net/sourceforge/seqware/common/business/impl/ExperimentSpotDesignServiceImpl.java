package net.sourceforge.seqware.common.business.impl;

import java.util.List;
import net.sourceforge.seqware.common.business.ExperimentSpotDesignService;
import net.sourceforge.seqware.common.dao.ExperimentSpotDesignDAO;
import net.sourceforge.seqware.common.model.ExperimentSpotDesign;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * ExperimentSpotDesignServiceImpl class.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public class ExperimentSpotDesignServiceImpl implements ExperimentSpotDesignService {
    private ExperimentSpotDesignDAO dao = null;
    private static final Log LOG = LogFactory.getLog(ExperimentSpotDesignServiceImpl.class);

    /**
     * <p>
     * Constructor for ExperimentSpotDesignServiceImpl.
     * </p>
     */
    public ExperimentSpotDesignServiceImpl() {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public void setExperimentSpotDesignDAO(ExperimentSpotDesignDAO dao) {
        this.dao = dao;
    }

    /**
     * {@inheritDoc}
     * 
     * Inserts an instance of ExperimentSpotDesign into the database.
     */
    @Override
    public void insert(ExperimentSpotDesign obj) {
        dao.insert(obj);
    }

    /**
     * {@inheritDoc}
     * 
     * Updates an instance of ExperimentSpotDesign in the database.
     */
    @Override
    public void update(ExperimentSpotDesign obj) {
        dao.update(obj);
    }

    /**
     * {@inheritDoc}
     * 
     * @param id
     */
    @Override
    public ExperimentSpotDesign findByID(Integer id) {
        ExperimentSpotDesign obj = null;
        if (id != null) {
            try {
                obj = dao.findByID(id);
            } catch (Exception exception) {
                LOG.error("Cannot find ExperimentSpotDesign by id " + id);
                LOG.error(exception.getMessage());
            }
        }
        return obj;
    }

    /** {@inheritDoc} */
    @Override
    public ExperimentSpotDesign updateDetached(ExperimentSpotDesign experiment) {
        return dao.updateDetached(experiment);
    }

    /** {@inheritDoc} */
    @Override
    public List<ExperimentSpotDesign> list() {
        return dao.list();
    }
}
