package net.sourceforge.seqware.common.dao.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import net.sourceforge.seqware.common.dao.LaneDAO;
import net.sourceforge.seqware.common.hibernate.FindAllTheFiles;
import net.sourceforge.seqware.common.model.*;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.NullBeanUtils;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * <p>LaneDAOHibernate class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class LaneDAOHibernate extends HibernateDaoSupport implements LaneDAO {
    
    private Logger logger;
    
    /**
     * <p>Constructor for LaneDAOHibernate.</p>
     */
    public LaneDAOHibernate() {
        super();
        logger = Logger.getLogger(LaneDAOHibernate.class);
    }

    /**
     * {@inheritDoc}
     *
     * Inserts an instance of Lane into the database.
     */
    public Integer insert(Lane lane) {
        this.getHibernateTemplate().save(lane);
        getSession().flush();
        return(lane.getSwAccession());
    }

    /**
     * {@inheritDoc}
     *
     * Updates an instance of Lane in the database.
     */
    public void update(Lane lane) {
        this.getHibernateTemplate().update(lane);
        getSession().flush();
    }

    /**
     * {@inheritDoc}
     *
     * Deletes an instance of Lane in the database.
     */
    public void delete(Lane lane) {
        // first delete records from processing_lanes
        String query = "DELETE FROM processing_lanes as pl where pl.lane_id = ?";
        SQLQuery sql = this.getSession().createSQLQuery(query);
        sql.setInteger(0, lane.getLaneId());
        sql.executeUpdate();

        // and then
        this.getHibernateTemplate().delete(lane);
    }
    
    /** {@inheritDoc} */
    public List<File> getFiles(Integer laneId) {
        List<File> files = new ArrayList<File>();
        
        String query = "WITH RECURSIVE processing_root_to_leaf (child_id, parent_id) AS ( "
                + "SELECT p.child_id as child_id, p.parent_id "
                + "FROM processing_relationship p inner join processing_ius pr_i on (pr_i.processing_id = p.parent_id) "
                + "inner join ius i on (i.ius_id = pr_i.ius_id) " + "inner join lane ln on (ln.lane_id = i.lane_id)  "
                + "where ln.lane_id = ? " + "UNION ALL " + "SELECT p.child_id, rl.parent_id "
                + "FROM processing_root_to_leaf rl, processing_relationship p " + "WHERE p.parent_id = rl.child_id ) "
                + "select * from File myfile where myfile.file_id in( "
                + "select distinct file_id from processing_root_to_leaf p, processing_files pf "
                + "where p.parent_id = processing_id " + "UNION ALL "
                + "select distinct file_id from processing_root_to_leaf p, processing_files pf "
                + "where p.child_id = processing_id " + "UNION ALL " + "select distinct file_id from processing_files pf "
                + "inner join processing_ius pr_i on (pr_i.processing_id = pf.processing_id) "
                + "inner join ius i on (i.ius_id = pr_i.ius_id) " + "inner join lane ln on (ln.lane_id = i.lane_id)  "
                + "where ln.lane_id = ? )";
        
        List list = this.getSession().createSQLQuery(query).addEntity(File.class).setInteger(0, laneId).setInteger(1, laneId).list();
        
        for (Object file : list) {
            File fl = (File) file;
            files.add(fl);
        }
        
        return files;
    }
    
    /** {@inheritDoc} */
    public boolean isHasFile(Integer laneId) {
        boolean isHasFile = false;
        String query = "WITH RECURSIVE processing_root_to_leaf (child_id, parent_id) AS ( "
                + "SELECT p.child_id as child_id, p.parent_id "
                + "FROM processing_relationship p inner join processing_ius pr_i on (pr_i.processing_id = p.parent_id) "
                + "inner join ius i on (i.ius_id = pr_i.ius_id) " + "inner join lane ln on (ln.lane_id = i.lane_id) "
                + "where ln.lane_id = ? " + "UNION ALL " + "SELECT p.child_id, rl.parent_id "
                + "FROM processing_root_to_leaf rl, processing_relationship p " + "WHERE p.parent_id = rl.child_id ) "
                + "select * from File myfile where myfile.file_id in( "
                + "select distinct file_id from processing_root_to_leaf p, processing_files pf "
                + "where p.parent_id = processing_id " + "UNION ALL "
                + "select distinct file_id from processing_root_to_leaf p, processing_files pf "
                + "where p.child_id = processing_id " + "UNION ALL " + "select distinct file_id from processing_files pf "
                + "inner join processing_ius pr_i on (pr_i.processing_id = pf.processing_id) "
                + "inner join ius i on (i.ius_id = pr_i.ius_id) " + "inner join lane ln on (ln.lane_id = i.lane_id)  "
                + "where ln.lane_id = ? ) LIMIT 1";
        
        List list = this.getSession().createSQLQuery(query).addEntity(File.class).setInteger(0, laneId).setInteger(1, laneId).list();
        
        isHasFile = (list.size() > 0) ? true : false;
        
        return isHasFile;
    }
    
    /** {@inheritDoc} */
    public List<File> getFiles(Integer laneId, String metaType) {
        List<File> files = new ArrayList<File>();
        
        String query = "WITH RECURSIVE processing_root_to_leaf (child_id, parent_id) AS ( "
                + "SELECT p.child_id as child_id, p.parent_id "
                + "FROM processing_relationship p inner join processing_ius pr_i on (pr_i.processing_id = p.parent_id) "
                + "inner join ius i on (i.ius_id = pr_i.ius_id) " + "inner join lane ln on (ln.lane_id = i.lane_id) "
                + "where ln.lane_id = ? " + "UNION ALL " + "SELECT p.child_id, rl.parent_id "
                + "FROM processing_root_to_leaf rl, processing_relationship p " + "WHERE p.parent_id = rl.child_id ) "
                + "select * from File myfile where myfile.meta_type=? and myfile.file_id in( "
                + "select distinct file_id from processing_root_to_leaf p, processing_files pf "
                + "where p.parent_id = processing_id " + "UNION ALL "
                + "select distinct file_id from processing_root_to_leaf p, processing_files pf "
                + "where p.child_id = processing_id " + "UNION ALL " + "select distinct file_id from processing_files pf "
                + "inner join processing_ius pr_i on (pr_i.processing_id = pf.processing_id) "
                + "inner join ius i on (i.ius_id = pr_i.ius_id) " + "inner join lane ln on (ln.lane_id = i.lane_id)  "
                + "where ln.lane_id = ? )";
        
        List list = this.getSession().createSQLQuery(query).addEntity(File.class).setInteger(0, laneId).setString(1, metaType).setInteger(2, laneId).list();
        
        for (Object file : list) {
            File fl = (File) file;
            files.add(fl);
        }
        
        return files;
    }
    
    /** {@inheritDoc} */
    public boolean isHasFile(Integer laneId, String metaType) {
        boolean isHasFile = false;
        String query = "WITH RECURSIVE processing_root_to_leaf (child_id, parent_id) AS ( "
                + "SELECT p.child_id as child_id, p.parent_id "
                + "FROM processing_relationship p inner join processing_ius pr_i on (pr_i.processing_id = p.parent_id) "
                + "inner join ius i on (i.ius_id = pr_i.ius_id) " + "inner join lane ln on (ln.lane_id = i.lane_id) "
                + "where ln.lane_id = ? " + "UNION ALL " + "SELECT p.child_id, rl.parent_id "
                + "FROM processing_root_to_leaf rl, processing_relationship p " + "WHERE p.parent_id = rl.child_id ) "
                + "select * from File myfile where myfile.meta_type=? and myfile.file_id in( "
                + "select distinct file_id from processing_root_to_leaf p, processing_files pf "
                + "where p.parent_id = processing_id " + "UNION ALL "
                + "select distinct file_id from processing_root_to_leaf p, processing_files pf "
                + "where p.child_id = processing_id " + "UNION ALL " + "select distinct file_id from processing_files pf "
                + "inner join processing_ius pr_i on (pr_i.processing_id = pf.processing_id) "
                + "inner join ius i on (i.ius_id = pr_i.ius_id) " + "inner join lane ln on (ln.lane_id = i.lane_id)  "
                + "where ln.lane_id = ? ) LIMIT 1";
        
        List list = this.getSession().createSQLQuery(query).addEntity(File.class).setInteger(0, laneId).setString(1, metaType).setInteger(2, laneId).list();
        
        isHasFile = (list.size() > 0) ? true : false;
        
        return isHasFile;
    }
    
    /** {@inheritDoc} */
    public List<Lane> list(List<Integer> laneIds) {
        List<Lane> lanes = new ArrayList<Lane>();
        
        String paramQuery = "";
        for (int i = 0; i < laneIds.size() - 1; i++) {
            paramQuery = paramQuery + "?,";
        }
        paramQuery = paramQuery + "?";
        
        String query = "SELECT * FROM Lane as l where l.lane_id in (" + paramQuery + ")";
        
        SQLQuery sql = this.getSession().createSQLQuery(query).addEntity(Lane.class);
        
        for (int i = 0; i < laneIds.size(); i++) {
            sql.setInteger(i, laneIds.get(i));
        }
        
        List list = sql.list();
        
        for (Object lane : list) {
            Lane ln = (Lane) lane;
            lanes.add(ln);
        }
        
        return lanes;
    }

    // delete Only lane, without CASCADE delete processig
    /**
     * <p>delete.</p>
     *
     * @param lanes a {@link java.util.SortedSet} object.
     */
    public void delete(SortedSet<Lane> lanes) {
        if (lanes == null || lanes.size() == 0) {
            return;
        }
        
        for (Lane lane : lanes) {
            this.getSession().evict(lane.getSequencerRun());
            this.getSession().evict(lane.getSample());
            
            Set<Processing> processings = lane.getProcessings();
            for (Processing processing : processings) {
                this.getSession().evict(processing);
            }
            this.getSession().evict(lane);
        }
        
        String paramQuery = "";
        for (int i = 0; i < lanes.size() - 1; i++) {
            paramQuery = paramQuery + "?,";
        }
        
        paramQuery = paramQuery + "?";
        logger.debug("Delete lanes:");
        logger.debug(paramQuery);

        // delete processing_lanes
        String query = "DELETE FROM processing_lanes as pl where pl.lane_id in (" + paramQuery + ")";
        
        SQLQuery sql = this.getSession().createSQLQuery(query);
        
        int iter = 0;
        for (Lane lane : lanes) {
            logger.debug("iter: " + iter + "; laneId = " + lane.getLaneId());
            sql.setInteger(iter, lane.getLaneId());
            iter++;
        }
        sql.executeUpdate();
        // logger.debug("END DELETE 1");

        // get ius by lane_id
        query = "SELECT ius_id FROM ius as i where i.lane_id in (" + paramQuery + ")";
        sql = this.getSession().createSQLQuery(query);
        iter = 0;
        for (Lane lane : lanes) {
            logger.debug("iter: " + iter + "; ius laneId = " + lane.getLaneId());
            sql.setInteger(iter, lane.getLaneId());
            iter++;
        }
        List list = sql.list();
        List<Integer> listIUSId = new ArrayList<Integer>();
        for (Object id : list) {
            if (id != null) {
                Integer iusId = (Integer) id;
                listIUSId.add(iusId);
            }
        }

        // delete ius_attribute and ius_link if want
        if (listIUSId.size() > 0) {
            // create iusParamQuery
            String iusParamQuery = "";
            for (int i = 0; i < lanes.size() - 1; i++) {
                iusParamQuery = iusParamQuery + "?,";
            }
            iusParamQuery = iusParamQuery + "?";

            // delete ius_link
            query = "DELETE FROM ius_link as link where link.ius_id in (" + iusParamQuery + ")";
            sql = this.getSession().createSQLQuery(query);
            iter = 0;
            for (Integer iusId : listIUSId) {
                sql.setInteger(iter, iusId);
                iter++;
            }
            sql.executeUpdate();

            // delete ius_attribute
            query = "DELETE FROM ius_attribute as attribute where attribute.ius_id in (" + iusParamQuery + ")";
            sql = this.getSession().createSQLQuery(query);
            iter = 0;
            for (Integer iusId : listIUSId) {
                sql.setInteger(iter, iusId);
                iter++;
            }
            sql.executeUpdate();
        }

        // delete ius for lanes
        query = "DELETE FROM ius as i where i.lane_id in (" + paramQuery + ")";
        sql = this.getSession().createSQLQuery(query);
        iter = 0;
        for (Lane lane : lanes) {
            sql.setInteger(iter, lane.getLaneId());
            iter++;
        }
        sql.executeUpdate();
        logger.debug("END DELETE 2");

        // delete lanes
        query = "DELETE FROM lane as l where l.lane_id in (" + paramQuery + ")";
        sql = this.getSession().createSQLQuery(query);
        iter = 0;
        for (Lane lane : lanes) {
            sql.setInteger(iter, lane.getLaneId());
            iter++;
        }
        sql.executeUpdate();
        logger.debug("END DELETE3");
    }

    /**
     * {@inheritDoc}
     *
     * Finds an instance of Lane in the database by the Lane emailAddress.
     */
    public Lane findByName(String name) {
        String query = "from lane as lane where lane.name = ?";
        Lane lane = null;
        Object[] parameters = {name};
        List list = this.getHibernateTemplate().find(query, parameters);
        if (list.size() > 0) {
            lane = (Lane) list.get(0);
        }
        return lane;
    }
    
    /** {@inheritDoc} */
    public Lane findByID(Integer expID) {
        String query = "from Lane as lane where lane.laneId = ?";
        Lane lane = null;
        Object[] parameters = {expID};
        List list = this.getHibernateTemplate().find(query, parameters);
        if (list.size() > 0) {
            lane = (Lane) list.get(0);
        }
        return lane;
    }
    
    /** {@inheritDoc} */
    @Override
    public Lane findBySWAccession(Integer swAccession) {
        String query = "from Lane as lane where lane.swAccession = ?";
        Lane lane = null;
        Object[] parameters = {swAccession};
        List<Lane> list = this.getHibernateTemplate().find(query, parameters);
        if (list.size() > 0) {
            lane = (Lane) list.get(0);
        }
        return lane;
    }
    
    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public List<Lane> findByOwnerID(Integer registrationId) {
        String query = "from Lane as lane where lane.owner.registrationId = ?";
        Object[] parameters = {registrationId};
        return this.getHibernateTemplate().find(query, parameters);
    }
    
    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public List<Lane> findByCriteria(String criteria, boolean isCaseSens) {
        String queryStringCase = "from Lane as l where  l.description like :description "
                + " or cast(l.swAccession as string) like :sw " + " or l.name like :name order by l.name, l.description";
        String queryStringICase = "from Lane as l where  lower(l.description) like :description "
                + " or cast(l.swAccession as string) like :sw " + " or lower(l.name) like :name order by l.name, l.description";
        Query query = isCaseSens ? this.getSession().createQuery(queryStringCase) : this.getSession().createQuery(
                queryStringICase);
        if (!isCaseSens) {
            criteria = criteria.toLowerCase();
        }
        criteria = "%" + criteria + "%";
        query.setString("description", criteria);
        query.setString("sw", criteria);
        query.setString("name", criteria);
        
        return query.list();
    }
    
    /** {@inheritDoc} */
    @Override
    public Lane updateDetached(Lane lane) {
        Lane dbObject = findByID(lane.getLaneId());
        try {
            BeanUtilsBean beanUtils = new NullBeanUtils();
            beanUtils.copyProperties(dbObject, lane);
            return (Lane) this.getHibernateTemplate().merge(dbObject);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /** {@inheritDoc} */
    @Override
    public List<ReturnValue> findFiles(Integer swAccession) {
        getSessionFactory().getCurrentSession().flush();
        getSessionFactory().getCurrentSession().clear();
        FindAllTheFiles fatf = new FindAllTheFiles();
        Lane lane = findBySWAccession(swAccession);
        List<ReturnValue> list = fatf.filesFromLane(lane, null, null);
        getSessionFactory().getCurrentSession().flush();
        getSessionFactory().getCurrentSession().clear();
        return list;
    }
    
    /** {@inheritDoc} */
    @Override
    public List<Lane> list() {
        ArrayList<Lane> l = new ArrayList<Lane>();
        
        String query = "from Lane";
        
        List list = this.getHibernateTemplate().find(query);
        
        for (Object e : list) {
            l.add((Lane) e);
        }
        
        return l;
    }
    
    /** {@inheritDoc} */
    @Override
    public void update(Registration registration, Lane lane) {
        Lane dbObject = reattachLane(lane);
        Logger logger = Logger.getLogger(LaneDAOHibernate.class);
        if (registration == null) {
            logger.error("LaneDAOHibernate update registration is null");
        } else if (registration.isLIMSAdmin() || 
                (lane.givesPermission(registration) && dbObject.givesPermission(registration))) {
            logger.info("updating Lane object");
            update(lane);
        } else {
            logger.error("LaneDAOHibernate update not authorized");
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public Integer insert(Registration registration, Lane lane) {
        Logger logger = Logger.getLogger(LaneDAOHibernate.class);
        if (registration == null) {
            logger.error("LaneDAOHibernate insert registration is null");
        } else if (registration.isLIMSAdmin() || lane.givesPermission(registration)) {
            logger.info("insert Lane object");
            insert(lane);
            return(lane.getSwAccession());
        } else {
            logger.error("LaneDAOHibernate insert not authorized");
        }
        return(null);
    }
    
    /** {@inheritDoc} */
    @Override
    public Lane updateDetached(Registration registration, Lane lane) {
        Lane dbObject = reattachLane(lane);
        Logger logger = Logger.getLogger(LaneDAOHibernate.class);
        if (registration == null) {
            logger.error("LaneDAOHibernate updateDetached registration is null");
        } else if (registration.isLIMSAdmin() || dbObject.givesPermission(registration)) {
            logger.info("updateDetached Lane object");
            return updateDetached(lane);
        } else {
            logger.error("LaneDAOHibernate updateDetached not authorized");
        }
        return null;
    }
    
        private Lane reattachLane(Lane lane) throws IllegalStateException, DataAccessResourceFailureException {
        Lane dbObject = lane;
        if (!getSession().contains(lane)) {
            dbObject = findByID(lane.getLaneId());
        }
        return dbObject;
    }
}
