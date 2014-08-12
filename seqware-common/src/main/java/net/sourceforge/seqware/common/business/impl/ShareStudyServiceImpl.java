package net.sourceforge.seqware.common.business.impl;

import java.util.Date;
import java.util.List;
import net.sourceforge.seqware.common.business.ShareStudyService;
import net.sourceforge.seqware.common.dao.ShareStudyDAO;
import net.sourceforge.seqware.common.model.ShareStudy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * ShareStudyServiceImpl class.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public class ShareStudyServiceImpl implements ShareStudyService {

    private ShareStudyDAO dao = null;
    private static final Log LOG = LogFactory.getLog(ShareStudyServiceImpl.class);

    /**
     * <p>
     * Constructor for ShareStudyServiceImpl.
     * </p>
     */
    public ShareStudyServiceImpl() {
        super();
    }

    /**
     * {@inheritDoc}
     * 
     * Sets a private member variable with an instance of an implementation of ShareStudyDAO. This method is called by the Spring framework
     * at run time.
     * 
     * @param dao
     * @see ShareStudyDAO
     */
    @Override
    public void setShareStudyDAO(ShareStudyDAO dao) {
        this.dao = dao;
    }

    /**
     * {@inheritDoc}
     * 
     * Inserts an instance of ShareStudy into the database.
     */
    @Override
    public void insert(ShareStudy shareStudy) {
        // shareStudy.setEmail(shareStudy.getEmail().trim().toLowerCase());
        shareStudy.setCreateTimestamp(new Date());

        dao.insert(shareStudy);
    }

    /**
     * {@inheritDoc}
     * 
     * Updates an instance of ShareStudy in the database.
     */
    @Override
    public void update(ShareStudy shareStudy) {
        dao.update(shareStudy);
    }

    /** {@inheritDoc} */
    @Override
    public void delete(ShareStudy shareStudy) {
        dao.delete(shareStudy);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isExistsShare(Integer studyId, Integer registrationId) {
        boolean isExists = false;
        if (findByStudyIdAndRegistrationId(studyId, registrationId) != null) {
            isExists = true;
        }
        return isExists;
    }

    /** {@inheritDoc} */
    @Override
    public ShareStudy findByStudyIdAndRegistrationId(Integer studyId, Integer registrationId) {
        ShareStudy shareStudy = null;
        if (studyId != null && registrationId != null) {
            try {
                shareStudy = dao.findByStudyIdAndRegistrationId(studyId, registrationId);
            } catch (Exception exception) {
                LOG.error("Cannot find ShareStudy by studyID " + studyId + " registrationId " + registrationId);
                LOG.error(exception.getMessage());
            }
        }
        return shareStudy;
    }

    /**
     * {@inheritDoc}
     * 
     * @param shareStudyId
     */
    @Override
    public ShareStudy findByID(Integer shareStudyId) {
        ShareStudy shareStudy = null;
        if (shareStudyId != null) {
            try {
                shareStudy = dao.findByID(shareStudyId);
            } catch (Exception exception) {
                LOG.error("Cannot find ShareStudy by shareStudyID " + shareStudyId);
                LOG.error(exception.getMessage());
            }
        }
        return shareStudy;
    }

    /** {@inheritDoc} */
    @Override
    public ShareStudy findBySWAccession(Integer swAccession) {
        ShareStudy shareStudy = null;
        if (swAccession != null) {
            try {
                shareStudy = dao.findByID(swAccession);
            } catch (Exception exception) {
                LOG.error("Cannot find ShareStudy by swAccession " + swAccession);
                LOG.error(exception.getMessage());
            }
        }
        return shareStudy;
    }

    /** {@inheritDoc} */
    @Override
    public ShareStudy updateDetached(ShareStudy shareStudy) {
        return dao.updateDetached(shareStudy);
    }

    /** {@inheritDoc} */
    @Override
    public List<ShareStudy> list() {
        return dao.list();
    }
}
