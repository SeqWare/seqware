package net.sourceforge.seqware.common.business.impl;

import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import net.sourceforge.seqware.common.business.ExperimentService;
import net.sourceforge.seqware.common.dao.ExperimentDAO;
import net.sourceforge.seqware.common.dao.FileDAO;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Study;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * ExperimentServiceImpl class.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public class ExperimentServiceImpl implements ExperimentService {
    private ExperimentDAO experimentDAO = null;
    private FileDAO fileDAO = null;
    private static final Log LOG = LogFactory.getLog(ExperimentServiceImpl.class);

    /**
     * <p>
     * Constructor for ExperimentServiceImpl.
     * </p>
     */
    public ExperimentServiceImpl() {
        super();
    }

    /**
     * {@inheritDoc}
     * 
     * Sets a private member variable with an instance of an implementation of ExperimentDAO. This method is called by the Spring framework
     * at run time.
     * 
     * @see ExperimentDAO
     */
    @Override
    public void setExperimentDAO(ExperimentDAO experimentDAO) {
        this.experimentDAO = experimentDAO;
    }

    /**
     * Sets a private member variable with an instance of an implementation of FileDAO. This method is called by the Spring framework at run
     * time.
     * 
     * @param fileDAO
     *            implementation of FileDAO
     * @see FileDAO
     */
    public void setFileDAO(FileDAO fileDAO) {
        this.fileDAO = fileDAO;
    }

    /**
     * {@inheritDoc}
     * 
     * Inserts an instance of Experiment into the database.
     */
    @Override
    public Integer insert(Experiment experiment) {
        experiment.setCreateTimestamp(new Date());
        return (experimentDAO.insert(experiment));
    }

    /**
     * {@inheritDoc}
     * 
     * Updates an instance of Experiment in the database.
     */
    @Override
    public void update(Experiment experiment) {
        experimentDAO.update(experiment);
    }

    /** {@inheritDoc} */
    @Override
    public void merge(Experiment experiment) {
        experimentDAO.merge(experiment);
    }

    /**
     * {@inheritDoc}
     * 
     * @param experiment
     * @param deleteRealFiles
     */
    @Override
    public void delete(Experiment experiment, boolean deleteRealFiles) {
        List<File> deleteFiles = null;
        if (deleteRealFiles) {
            deleteFiles = experimentDAO.getFiles(experiment.getExperimentId());
        }

        experimentDAO.delete(experiment);

        if (deleteRealFiles) {
            fileDAO.deleteAllWithFolderStore(deleteFiles);
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<Experiment> list(Registration registration) {
        return experimentDAO.list(registration);
    }

    /**
     * <p>
     * list.
     * </p>
     * 
     * @param study
     *            a {@link net.sourceforge.seqware.common.model.Study} object.
     * @return a {@link java.util.List} object.
     */
    @Override
    public List<Experiment> list(Study study) {
        return experimentDAO.list(study);
    }

    /**
     * {@inheritDoc}
     * 
     * @param experimentId
     */
    @Override
    public List<File> getFiles(Integer experimentId) {
        return experimentDAO.getFiles(experimentId);
    }

    /**
     * {@inheritDoc}
     * 
     * @param experimentId
     */
    @Override
    public boolean isHasFile(Integer experimentId) {
        return experimentDAO.isHasFile(experimentId);
    }

    /** {@inheritDoc} */
    @Override
    public List<File> getFiles(Integer studyId, String metaType) {
        return experimentDAO.getFiles(studyId, metaType);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isHasFile(Integer studyId, String metaType) {
        return experimentDAO.isHasFile(studyId, metaType);
    }

    /** {@inheritDoc} */
    @Override
    public SortedSet<Experiment> setWithHasFile(SortedSet<Experiment> list) {
        for (Experiment experiment : list) {
            experiment.setIsHasFile(isHasFile(experiment.getExperimentId()));
        }
        return list;
    }

    /** {@inheritDoc} */
    @Override
    public SortedSet<Experiment> listWithHasFile(SortedSet<Experiment> list, String metaType) {
        SortedSet<Experiment> result = new TreeSet<>();
        for (Experiment experiment : list) {
            if (isHasFile(experiment.getExperimentId(), metaType)) {
                experiment.setIsHasFile(true);
                result.add(experiment);
            }
            // experiment.setIsHasFile(isHasFile(experiment.getExperimentId(),
            // metaType));
        }
        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * Finds an instance of Experiment in the database by the Experiment emailAddress, and copies the Experiment properties to an instance
     * of Experiment.
     * 
     * @param title
     */
    @Override
    public Experiment findByTitle(String title) {
        Experiment experiment = null;
        if (title != null) {
            try {
                experiment = experimentDAO.findByTitle(title.trim());
            } catch (Exception exception) {
                LOG.debug("Cannot find Experiment by title " + title);
            }
        }
        return experiment;
    }

    /** {@inheritDoc} */
    @Override
    public Experiment findByID(Integer expID) {
        Experiment experiment = null;
        if (expID != null) {
            try {
                experiment = experimentDAO.findByID(expID);
            } catch (Exception exception) {
                LOG.error("Cannot find Experiment by expID " + expID);
                LOG.error(exception.getMessage());
            }
        }
        return experiment;
    }

    /** {@inheritDoc} */
    @Override
    public Experiment findBySWAccession(Integer swAccession) {
        Experiment experiment = null;
        if (swAccession != null) {
            try {
                experiment = experimentDAO.findBySWAccession(swAccession);
            } catch (Exception exception) {
                LOG.error("Cannot find Experiment by swAccession " + swAccession);
                LOG.error(exception.getMessage());
            }
        }
        return experiment;
    }

    /**
     * {@inheritDoc}
     * 
     * Determines if an email address has already been used.
     * 
     * @param oldTitle
     * @param newTitle
     */
    @Override
    public boolean hasTitleBeenUsed(String oldTitle, String newTitle) {
        boolean titleUsed = false;
        boolean checkTitle = true;

        if (newTitle != null) {
            if (oldTitle != null) {
                /*
                 * We do not want to check if an title address has been used if the user is updating an existing sequencer run and has not
                 * changed the titleAddress.
                 */
                checkTitle = !newTitle.trim().equalsIgnoreCase(oldTitle.trim());
            }

            if (checkTitle) {
                Experiment experiment = this.findByTitle(newTitle.trim().toLowerCase());
                if (experiment != null) {
                    titleUsed = true;
                }
            }
        }
        return titleUsed;
    }

    /** {@inheritDoc} */
    @Override
    public Experiment updateDetached(Experiment experiment) {
        return experimentDAO.updateDetached(experiment);
    }

    /** {@inheritDoc} */
    @Override
    public List<Experiment> findByOwnerID(Integer registrationID) {
        return experimentDAO.findByOwnerID(registrationID);
    }

    /** {@inheritDoc} */
    @Override
    public List<Experiment> findByCriteria(String criteria, boolean isCaseSens) {
        return experimentDAO.findByCriteria(criteria, isCaseSens);
    }

    /** {@inheritDoc} */
    @Override
    public List<Experiment> list() {
        return experimentDAO.list();
    }

    /** {@inheritDoc} */
    @Override
    public void update(Registration registration, Experiment experiment) {
        experimentDAO.update(registration, experiment);
    }

    /** {@inheritDoc} */
    @Override
    public Integer insert(Registration registration, Experiment experiment) {
        experiment.setCreateTimestamp(new Date());
        return (experimentDAO.insert(registration, experiment));
    }

    /** {@inheritDoc} */
    @Override
    public Experiment updateDetached(Registration registration, Experiment experiment) {
        return experimentDAO.updateDetached(registration, experiment);
    }

}

// ex:sw=4:ts=4:
