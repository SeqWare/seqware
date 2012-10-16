package net.sourceforge.seqware.common.dao.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.seqware.common.dao.ProcessingDAO;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.util.NullBeanUtils;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * <p>ProcessingDAOHibernate class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ProcessingDAOHibernate extends HibernateDaoSupport implements ProcessingDAO {

    private Logger logger;

    /**
     * <p>Constructor for ProcessingDAOHibernate.</p>
     */
    public ProcessingDAOHibernate() {
        super();
        logger = Logger.getLogger(ProcessingDAOHibernate.class);
    }

    /** {@inheritDoc} */
    @Override
    public Integer insert(Processing processing) {
        if (processing.getCreateTimestamp() == null) {
            processing.setCreateTimestamp(new Timestamp(System.currentTimeMillis()));
        }
        this.getHibernateTemplate().save(processing);
        this.getSession().flush();
        return processing.getSwAccession();
    }

    /**
     * {@inheritDoc}
     *
     * Updates an instance of Processing in the database.
     */
    public void update(Processing processing) {

        this.getHibernateTemplate().update(processing);
        getSession().flush();
    }

    /**
     * {@inheritDoc}
     *
     * Updates an instance of Processing in the database.
     */
    public void delete(Processing processing) {

        this.getHibernateTemplate().delete(processing);
    }

    /**
     * {@inheritDoc}
     *
     * WITH RECURSIVE "processing_root_to_leaf" ("child_id", "parent_id") AS (
     * SELECT p."child_id" as "child_id", p."parent_id" FROM
     * "processing_relationship" p where p."parent_id" = 53851 UNION ALL SELECT
     * p."child_id", rl."parent_id" FROM "processing_root_to_leaf" rl,
     * "processing_relationship" p WHERE p."parent_id" = rl."child_id" )
     * --select * from "processing_root_to_leaf" p; select distinct file_id from
     * "processing_root_to_leaf"p, processing_files pf where p.parent_id =
     * processing_id or p.child_id = processing_id;
     */
    public List<File> getFiles(Integer processingId) {
        List<File> files = new ArrayList<File>();

        /*
         * String query = "WITH RECURSIVE processing_root_to_leaf (child_id,
         * parent_id) AS ( " + "SELECT p.child_id as child_id, p.parent_id FROM
         * processing_relationship p where p.parent_id = ? " + "UNION ALL " +
         * "SELECT p.child_id, rl.parent_id " + "FROM processing_root_to_leaf
         * rl, processing_relationship p " + "WHERE p.parent_id = rl.child_id )
         * " + "select * from File myfile where myfile.file_id in( " + "select
         * distinct file_id from processing_root_to_leaf p, processing_files pf
         * where p.parent_id = processing_id " + "UNION ALL " + "select distinct
         * file_id from processing_root_to_leaf p, processing_files pf where
         * p.child_id = processing_id)" ;
         */
        String query = "WITH RECURSIVE processing_root_to_leaf (child_id, parent_id) AS ( "
                + "SELECT p.child_id as child_id, p.parent_id FROM processing_relationship p where p.parent_id = ? "
                + "UNION ALL " + "SELECT p.child_id, rl.parent_id "
                + "FROM processing_root_to_leaf rl, processing_relationship p " + "WHERE p.parent_id = rl.child_id ) "
                + "select * from File myfile where myfile.file_id in( "
                + "select distinct file_id from processing_root_to_leaf p, processing_files pf "
                + "where p.parent_id = processing_id " + "UNION ALL "
                + "select distinct file_id from processing_root_to_leaf p, processing_files pf "
                + "where p.child_id = processing_id " + "UNION ALL "
                + "select distinct file_id from processing_files pf where pf.processing_id = ?)";

        List list = this.getSession().createSQLQuery(query).addEntity(File.class).setInteger(0, processingId/*
                 * 53851
                 */).setInteger(1, processingId).list();

        /*
         * Processing proc = findByID(processingId);
         *
         * logger.debug("CHILDREN == ? 0"); if(proc.getChildren() == null ||
         * proc.getChildren().size() == 0){ logger.debug("CHILDREN == NULL");
         * list.addAll(proc.getFiles()); }
         */
        logger.debug("FILES:");
        for (Object file : list) {
            File fl = (File) file;
            logger.debug(fl.getFileName());
            files.add(fl);

        }
        // logger.debug("THE END");
    /*
         * Object[] parameters = { processingId }; List list =
         * this.getHibernateTemplate().find(query, parameters);
         *
         * for(Object file : list) { files.add((File)file); }
         */

        return files;
    }

    /** {@inheritDoc} */
    public boolean isHasFile(Integer processingId) {
        boolean isHasFile = false;
        String query = "WITH RECURSIVE processing_root_to_leaf (child_id, parent_id) AS ( "
                + "SELECT p.child_id as child_id, p.parent_id FROM processing_relationship p where p.parent_id = ? "
                + "UNION ALL " + "SELECT p.child_id, rl.parent_id "
                + "FROM processing_root_to_leaf rl, processing_relationship p " + "WHERE p.parent_id = rl.child_id ) "
                + "select * from File myfile where myfile.file_id in( "
                + "select distinct file_id from processing_root_to_leaf p, processing_files pf "
                + "where p.parent_id = processing_id " + "UNION ALL "
                + "select distinct file_id from processing_root_to_leaf p, processing_files pf "
                + "where p.child_id = processing_id " + "UNION ALL "
                + "select distinct file_id from processing_files pf where pf.processing_id = ?) LIMIT 1";

        List list = this.getSession().createSQLQuery(query).addEntity(File.class).setInteger(0, processingId/*
                 * 53851
                 */).setInteger(1, processingId).list();

        isHasFile = (list.size() > 0) ? true : false;

        return isHasFile;
    }

    /** {@inheritDoc} */
    public List<File> getFiles(Integer processingId, String metaType) {
        List<File> files = new ArrayList<File>();
        String query = "WITH RECURSIVE processing_root_to_leaf (child_id, parent_id) AS ( "
                + "SELECT p.child_id as child_id, p.parent_id FROM processing_relationship p where p.parent_id = ? "
                + "UNION ALL " + "SELECT p.child_id, rl.parent_id "
                + "FROM processing_root_to_leaf rl, processing_relationship p " + "WHERE p.parent_id = rl.child_id ) "
                + "select * from File myfile where myfile.meta_type=? and myfile.file_id in( "
                + "select distinct file_id from processing_root_to_leaf p, processing_files pf "
                + "where p.parent_id = processing_id " + "UNION ALL "
                + "select distinct file_id from processing_root_to_leaf p, processing_files pf "
                + "where p.child_id = processing_id " + "UNION ALL "
                + "select distinct file_id from processing_files pf where pf.processing_id = ?)";

        List list = this.getSession().createSQLQuery(query).addEntity(File.class).setInteger(0, processingId/*
                 * 53851
                 */).setString(1, metaType).setInteger(2, processingId).list();

        // logger.debug("FILES:");
        for (Object file : list) {
            File fl = (File) file;
            // logger.debug(fl.getFileName());
            files.add(fl);

        }
        return files;
    }

    /** {@inheritDoc} */
    public boolean isHasFile(Integer processingId, String metaType) {
        boolean isHasFile = false;
        String query = "WITH RECURSIVE processing_root_to_leaf (child_id, parent_id) AS ( "
                + "SELECT p.child_id as child_id, p.parent_id FROM processing_relationship p where p.parent_id = ? "
                + "UNION ALL "
                + "SELECT p.child_id, rl.parent_id "
                + "FROM processing_root_to_leaf rl, processing_relationship p "
                + "WHERE p.parent_id = rl.child_id ) "
                + "select * from File myfile where myfile.meta_type=? and myfile.file_id in( "
                + // "select * from File myfile where myfile.file_id in( " +
                "select distinct file_id from processing_root_to_leaf p, processing_files pf "
                + "where p.parent_id = processing_id " + "UNION ALL "
                + "select distinct file_id from processing_root_to_leaf p, processing_files pf "
                + "where p.child_id = processing_id " + "UNION ALL "
                + "select distinct file_id from processing_files pf where pf.processing_id = ?) LIMIT 1";

        List list = this.getSession().createSQLQuery(query).addEntity(File.class).setInteger(0, processingId/*
                 * 53851
                 */).setString(1, metaType).setInteger(2, processingId).list();

        isHasFile = (list.size() > 0) ? true : false;

        return isHasFile;
    }

    /**
     * {@inheritDoc}
     *
     * Finds an instance of Processing in the database by the Processing
     * emailAddress.
     */
    public Processing findByFilePath(String filePath) {
        String query = "from processing as processing where processing.file_path = ?";
        Processing processing = null;
        Object[] parameters = {filePath};
        List list = this.getHibernateTemplate().find(query, parameters);
        if (list.size() > 0) {
            processing = (Processing) list.get(0);
        }
        return processing;
    }

    /**
     * {@inheritDoc}
     *
     * Finds an instance of SequencerRun in the database by the SequencerRun ID.
     */
    public Processing findByID(Integer id) {
        String query = "from Processing as processing where processing.processingId = ?";
        Processing processing = null;
        Object[] parameters = {id};
        List list = this.getHibernateTemplate().find(query, parameters);
        if (list.size() > 0) {
            processing = (Processing) list.get(0);
        }
        return processing;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public Processing findBySWAccession(Integer swAccession) {
        String query = "from Processing as processing where processing.swAccession = ?";
        Processing processing = null;
        Object[] parameters = {swAccession};
        List<Processing> list = this.getHibernateTemplate().find(query, parameters);
        if (list.size() > 0) {
            processing = (Processing) list.get(0);
        }
        return processing;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public List<Processing> findByOwnerID(Integer registrationId) {
        String query = "from Processing as processing where processing.owner.registrationId = ?";
        Object[] parameters = {registrationId};
        return this.getHibernateTemplate().find(query, parameters);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public List<Processing> findByCriteria(String criteria, boolean isCaseSens) {
        String queryStringCase = "from Processing as p where p.description like :description "
                + " or cast(p.swAccession as string) like :sw or p.algorithm like :alg order by p.algorithm, p.description";
        String queryStringICase = "from Processing as p where lower(p.description) like :description "
                + " or cast(p.swAccession as string) like :sw or lower(p.algorithm) like :alg order by p.algorithm, p.description";
        Query query = isCaseSens ? this.getSession().createQuery(queryStringCase) : this.getSession().createQuery(
                queryStringICase);
        if (!isCaseSens) {
            criteria = criteria.toLowerCase();
        }
        criteria = "%" + criteria + "%";
        query.setString("description", criteria);
        query.setString("sw", criteria);
        query.setString("alg", criteria);

        return query.list();
    }

    /** {@inheritDoc} */
    @Override
    public Processing updateDetached(Processing processing) {
        Processing dbObject = findByID(processing.getProcessingId());
        try {
            BeanUtilsBean beanUtils = new NullBeanUtils();
            beanUtils.copyProperties(dbObject, processing);
            return (Processing) this.getHibernateTemplate().merge(dbObject);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public List<Processing> list() {
        ArrayList<Processing> l = new ArrayList<Processing>();

        String query = "from Processing";

        List list = this.getHibernateTemplate().find(query);

        for (Object e : list) {
            l.add((Processing) e);
        }

        return l;
    }

    /** {@inheritDoc} */
    @Override
    public void update(Registration registration, Processing processing) {
        Processing dbObject = reattachProcessing(processing);
        Logger logger = Logger.getLogger(ProcessingDAOHibernate.class);
        if (registration == null) {
            logger.error("ProcessingDAOHibernate update registration is null");
        } else if (registration.isLIMSAdmin()
                || dbObject.givesPermission(registration)) {
            logger.info("Updating processing object");
            update(processing);
        } else {
            logger.error("ProcessingDAOHibernate update not authorized");
        }
    }

    /** {@inheritDoc} */
    @Override
    public Integer insert(Registration registration, Processing processing) {
        Logger logger = Logger.getLogger(ProcessingDAOHibernate.class);
        if (registration == null) {
            logger.error("ProcessingDAOHibernate insert registration is null");
        } else if (registration.isLIMSAdmin() || processing.givesPermission(registration)) {
            logger.info("insert processing object. person is " + registration.getEmailAddress());
            return insert(processing);
        } else {
            logger.error("ProcessingDAOHibernate insert not authorized");
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Processing updateDetached(Registration registration, Processing processing) {
        Processing dbObject = reattachProcessing(processing);
        Logger logger = Logger.getLogger(ProcessingDAOHibernate.class);
        if (registration == null) {
            logger.error("ProcessingDAOHibernate updateDetached registration is null");
        } else if (registration.isLIMSAdmin() || dbObject.givesPermission(registration)) {
            logger.info("updateDetached processing object");
            return updateDetached(processing);
        } else {
            logger.error("ProcessingDAOHibernate updateDetached not authorized");
        }
        return null;
    }
    
        private Processing reattachProcessing(Processing processing) throws IllegalStateException, DataAccessResourceFailureException {
        Processing dbObject = processing;
        if (!getSession().contains(processing)) {
            dbObject = findByID(processing.getProcessingId());
        }
        return dbObject;
    }
}
