package net.sourceforge.seqware.common.dao.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.seqware.common.dao.ProcessingDAO;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.util.NullBeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

/**
 * <p>
 * ProcessingDAOHibernate class.
 * </p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ProcessingDAOHibernate extends HibernateDaoSupport implements ProcessingDAO {

    final Logger localLogger = LoggerFactory.getLogger(ProcessingDAOHibernate.class);

    /**
     * <p>
     * Constructor for ProcessingDAOHibernate.
     * </p>
     */
    public ProcessingDAOHibernate() {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public Integer insert(Processing processing) {
        if (processing.getCreateTimestamp() == null) {
            processing.setCreateTimestamp(new Timestamp(System.currentTimeMillis()));
        }
        this.getHibernateTemplate().save(processing);
        this.currentSession().flush();
        return processing.getSwAccession();
    }

    /**
     * {@inheritDoc}
     *
     * Updates an instance of Processing in the database.
     */
    @Override
    public void update(Processing processing) {

        this.getHibernateTemplate().update(processing);
        currentSession().flush();
    }

    /**
     * {@inheritDoc}
     *
     * Updates an instance of Processing in the database. This is likely to not work given the complex tree structures created with
     * processing entries.
     */
    @Override
    public void delete(Processing processing) {

        this.getHibernateTemplate().delete(processing);
    }

    /**
     * {@inheritDoc}
     *
     * WITH RECURSIVE "processing_root_to_leaf" ("child_id", "parent_id") AS ( SELECT p."child_id" as "child_id", p."parent_id" FROM
     * "processing_relationship" p where p."parent_id" = 53851 UNION ALL SELECT p."child_id", rl."parent_id" FROM "processing_root_to_leaf"
     * rl, "processing_relationship" p WHERE p."parent_id" = rl."child_id" ) --select * from "processing_root_to_leaf" p; select distinct
     * file_id from "processing_root_to_leaf"p, processing_files pf where p.parent_id = processing_id or p.child_id = processing_id;
     */
    @Override
    public List<File> getFiles(Integer processingId) {
        List<File> files = new ArrayList<>();

        /*
         * String query = "WITH RECURSIVE processing_root_to_leaf (child_id, parent_id) AS ( " + "SELECT p.child_id as child_id, p.parent_id
         * FROM processing_relationship p where p.parent_id = ? " + "UNION ALL " + "SELECT p.child_id, rl.parent_id " + "FROM
         * processing_root_to_leaf rl, processing_relationship p " + "WHERE p.parent_id = rl.child_id ) " + "select * from File myfile where
         * myfile.file_id in( " + "select distinct file_id from processing_root_to_leaf p, processing_files pf where p.parent_id =
         * processing_id " + "UNION ALL " + "select distinct file_id from processing_root_to_leaf p, processing_files pf where p.child_id =
         * processing_id)" ;
         */
        String query = "WITH RECURSIVE processing_root_to_leaf (child_id, parent_id) AS ( "
                + "SELECT p.child_id as child_id, p.parent_id FROM processing_relationship p where p.parent_id = ? " + "UNION ALL "
                + "SELECT p.child_id, rl.parent_id " + "FROM processing_root_to_leaf rl, processing_relationship p "
                + "WHERE p.parent_id = rl.child_id ) " + "select * from File myfile where myfile.file_id in( "
                + "select distinct file_id from processing_root_to_leaf p, processing_files pf " + "where p.parent_id = processing_id "
                + "UNION ALL " + "select distinct file_id from processing_root_to_leaf p, processing_files pf "
                + "where p.child_id = processing_id " + "UNION ALL "
                + "select distinct file_id from processing_files pf where pf.processing_id = ?)";

        List list = this.currentSession().createSQLQuery(query).addEntity(File.class).setInteger(0, processingId/*
                                                                                                             * 53851
                                                                                                             */)
                .setInteger(1, processingId).list();

        /*
         * Processing proc = findByID(processingId);
         * 
         * logger.debug("CHILDREN == ? 0"); if(proc.getChildren() == null || proc.getChildren().size() == 0){
         * logger.debug("CHILDREN == NULL"); list.addAll(proc.getFiles()); }
         */
        localLogger.debug("FILES:");
        for (Object file : list) {
            File fl = (File) file;
            localLogger.debug(fl.getFileName());
            files.add(fl);

        }
        // logger.debug("THE END");
        /*
         * Object[] parameters = { processingId }; List list = this.getHibernateTemplate().find(query, parameters);
         * 
         * for(Object file : list) { files.add((File)file); }
         */

        return files;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isHasFile(Integer processingId) {
        boolean isHasFile;
        String query = "WITH RECURSIVE processing_root_to_leaf (child_id, parent_id) AS ( "
                + "SELECT p.child_id as child_id, p.parent_id FROM processing_relationship p where p.parent_id = ? " + "UNION ALL "
                + "SELECT p.child_id, rl.parent_id " + "FROM processing_root_to_leaf rl, processing_relationship p "
                + "WHERE p.parent_id = rl.child_id ) " + "select * from File myfile where myfile.file_id in( "
                + "select distinct file_id from processing_root_to_leaf p, processing_files pf " + "where p.parent_id = processing_id "
                + "UNION ALL " + "select distinct file_id from processing_root_to_leaf p, processing_files pf "
                + "where p.child_id = processing_id " + "UNION ALL "
                + "select distinct file_id from processing_files pf where pf.processing_id = ?) LIMIT 1";

        List list = this.currentSession().createSQLQuery(query).addEntity(File.class).setInteger(0, processingId/*
                                                                                                             * 53851
                                                                                                             */)
                .setInteger(1, processingId).list();

        isHasFile = (list.size() > 0);

        return isHasFile;
    }

    /** {@inheritDoc} */
    @Override
    public List<File> getFiles(Integer processingId, String metaType) {
        List<File> files = new ArrayList<>();
        String query = "WITH RECURSIVE processing_root_to_leaf (child_id, parent_id) AS ( "
                + "SELECT p.child_id as child_id, p.parent_id FROM processing_relationship p where p.parent_id = ? " + "UNION ALL "
                + "SELECT p.child_id, rl.parent_id " + "FROM processing_root_to_leaf rl, processing_relationship p "
                + "WHERE p.parent_id = rl.child_id ) " + "select * from File myfile where myfile.meta_type=? and myfile.file_id in( "
                + "select distinct file_id from processing_root_to_leaf p, processing_files pf " + "where p.parent_id = processing_id "
                + "UNION ALL " + "select distinct file_id from processing_root_to_leaf p, processing_files pf "
                + "where p.child_id = processing_id " + "UNION ALL "
                + "select distinct file_id from processing_files pf where pf.processing_id = ?)";

        List list = this.currentSession().createSQLQuery(query).addEntity(File.class).setInteger(0, processingId/*
                                                                                                             * 53851
                                                                                                             */).setString(1, metaType)
                .setInteger(2, processingId).list();

        // logger.debug("FILES:");
        for (Object file : list) {
            File fl = (File) file;
            // logger.debug(fl.getFileName());
            files.add(fl);

        }
        return files;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isHasFile(Integer processingId, String metaType) {
        boolean isHasFile;
        String query = "WITH RECURSIVE processing_root_to_leaf (child_id, parent_id) AS ( "
                + "SELECT p.child_id as child_id, p.parent_id FROM processing_relationship p where p.parent_id = ? " + "UNION ALL "
                + "SELECT p.child_id, rl.parent_id "
                + "FROM processing_root_to_leaf rl, processing_relationship p "
                + "WHERE p.parent_id = rl.child_id ) "
                + "select * from File myfile where myfile.meta_type=? and myfile.file_id in( "
                + // "select * from File myfile where myfile.file_id in( " +
                "select distinct file_id from processing_root_to_leaf p, processing_files pf " + "where p.parent_id = processing_id "
                + "UNION ALL " + "select distinct file_id from processing_root_to_leaf p, processing_files pf "
                + "where p.child_id = processing_id " + "UNION ALL "
                + "select distinct file_id from processing_files pf where pf.processing_id = ?) LIMIT 1";

        List list = this.currentSession().createSQLQuery(query).addEntity(File.class).setInteger(0, processingId/*
                                                                                                             * 53851
                                                                                                             */).setString(1, metaType)
                .setInteger(2, processingId).list();

        isHasFile = (list.size() > 0);

        return isHasFile;
    }

    /**
     * {@inheritDoc}
     *
     * Finds an instance of Processing in the database by the Processing emailAddress.
     */
    @Override
    public Processing findByFilePath(String filePath) {
        String query = "from processing as processing where processing.file_path = ?";
        Processing processing = null;
        Object[] parameters = { filePath };
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
     *
     * @param id
     */
    @Override
    public Processing findByID(Integer id) {
        String query = "from Processing as processing where processing.processingId = ?";
        Processing processing = null;
        Object[] parameters = { id };
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
        Object[] parameters = { swAccession };
        List<Processing> list = (List<Processing>) this.getHibernateTemplate().find(query, parameters);
        if (list.size() > 0) {
            processing = list.get(0);
        }
        return processing;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public List<Processing> findByOwnerID(Integer registrationId) {
        String query = "from Processing as processing where processing.owner.registrationId = ?";
        Object[] parameters = { registrationId };
        return (List<Processing>) this.getHibernateTemplate().find(query, parameters);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public List<Processing> findByCriteria(String criteria, boolean isCaseSens) {
        String queryStringCase = "from Processing as p where p.description like :description "
                + " or cast(p.swAccession as string) like :sw or p.algorithm like :alg order by p.algorithm, p.description";
        String queryStringICase = "from Processing as p where lower(p.description) like :description "
                + " or cast(p.swAccession as string) like :sw or lower(p.algorithm) like :alg order by p.algorithm, p.description";
        Query query = isCaseSens ? this.currentSession().createQuery(queryStringCase) : this.currentSession().createQuery(queryStringICase);
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
            return this.getHibernateTemplate().merge(dbObject);
        } catch (IllegalAccessException | InvocationTargetException e) {
            localLogger.error("Error updating detached processing", e);
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public List<Processing> list() {
        ArrayList<Processing> l = new ArrayList<>();

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
        if (registration == null) {
            localLogger.error("ProcessingDAOHibernate update registration is null");
        } else if (registration.isLIMSAdmin() || dbObject.givesPermission(registration)) {
            localLogger.info("Updating processing object");
            update(processing);
        } else {
            localLogger.error("ProcessingDAOHibernate update not authorized");
        }
    }

    /** {@inheritDoc} */
    @Override
    public Integer insert(Registration registration, Processing processing) {
        if (registration == null) {
            localLogger.error("ProcessingDAOHibernate insert registration is null");
        } else if (registration.isLIMSAdmin() || processing.givesPermission(registration)) {
            localLogger.info("insert processing object. person is " + registration.getEmailAddress());
            return insert(processing);
        } else {
            localLogger.error("ProcessingDAOHibernate insert not authorized");
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Processing updateDetached(Registration registration, Processing processing) {
        Processing dbObject = reattachProcessing(processing);
        if (registration == null) {
            localLogger.error("ProcessingDAOHibernate updateDetached registration is null");
        } else if (registration.isLIMSAdmin() || dbObject.givesPermission(registration)) {
            localLogger.info("updateDetached processing object");
            return updateDetached(processing);
        } else {
            localLogger.error("ProcessingDAOHibernate updateDetached not authorized");
        }
        return null;
    }

    private Processing reattachProcessing(Processing processing) throws IllegalStateException, DataAccessResourceFailureException {
        Processing dbObject = processing;
        if (!currentSession().contains(processing)) {
            dbObject = findByID(processing.getProcessingId());
        }
        return dbObject;
    }
}
