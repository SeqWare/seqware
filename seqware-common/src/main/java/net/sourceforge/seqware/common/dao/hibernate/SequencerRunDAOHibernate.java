package net.sourceforge.seqware.common.dao.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.sourceforge.seqware.common.dao.SequencerRunDAO;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.SequencerRunWizardDTO;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.NullBeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

/**
 * <p>
 * SequencerRunDAOHibernate class.
 * </p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class SequencerRunDAOHibernate extends HibernateDaoSupport implements SequencerRunDAO {

    final Logger localLogger = LoggerFactory.getLogger(SequencerRunDAOHibernate.class);

    /**
     * <p>
     * Constructor for SequencerRunDAOHibernate.
     * </p>
     */
    public SequencerRunDAOHibernate() {
        super();
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public Integer insert(SequencerRun sequencerRun) {
        this.getHibernateTemplate().save(sequencerRun);
        currentSession().flush();
        return (sequencerRun.getSwAccession());
    }

    /**
     * <p>
     * insert.
     * </p>
     *
     * @param sequencerRun
     *            a {@link net.sourceforge.seqware.common.model.SequencerRunWizardDTO} object.
     * @return
     */
    @Override
    public Integer insert(SequencerRunWizardDTO sequencerRun) {
        this.getHibernateTemplate().save(sequencerRun);
        currentSession().flush();
        return (sequencerRun.getSwAccession());
    }

    /** {@inheritDoc} */
    @Override
    public void update(SequencerRun sequencerRun) {
        this.getHibernateTemplate().update(sequencerRun);
        currentSession().flush();
    }

    /** {@inheritDoc} */
    @Override
    public void delete(SequencerRun sequencerRun) {
        this.getHibernateTemplate().delete(sequencerRun);
    }

    /** {@inheritDoc} */
    @Override
    public List<SequencerRun> list(Registration registration, Boolean isAsc) {
        ArrayList<SequencerRun> sequencerRuns = new ArrayList<>();
        if (registration == null) {
            return sequencerRuns;
        }

        /*
         * List list = this.getHibernateTemplate().find( "from SequencerRun as sequencerRun order by create_tstmp desc" "from SequencerRun
         * as sequencerRun where sequencerRun.owner.registrationId=? order by create_tstmp desc" );
         */
        String query;
        Object[] parameters = { registration.getRegistrationId() };
        String sortValue = (!isAsc) ? "asc" : "desc";

        List list;
        if (registration.isLIMSAdmin()) {
            // select distinct f from Foo f
            query = "select * from sequencer_run as sr order by sr.create_tstmp " + sortValue + ";";
            // query = "from SequencerRun as sr where sr.sequencerRunId in ( "
            // +
            // " select distinct sequencerRun.sequencerRunId from(select sequencerRun.sequencerRunId, sequencerRun.createTimestamp "
            // +
            // " from SequencerRun as sequencerRun order by sequencerRun.createTimestamp asc ))";
            parameters = null;
            list = this.currentSession().createSQLQuery(query).addEntity(SequencerRun.class).list();
        } else {
            query = "select * from sequencer_run as sr where sr.owner_id = ? order by sr.create_tstmp " + sortValue + ";";
            // query =
            // "from SequencerRun as sequencerRun where sequencerRun.owner.registrationId=? "
            // +
            // "order by sequencerRun.createTimestamp " + sortValue;
            list = this.currentSession().createSQLQuery(query).addEntity(SequencerRun.class).setInteger(0, registration.getRegistrationId())
                    .list();
        }

        for (Object obj : list) {
            SequencerRun sr = (SequencerRun) obj;
            sequencerRuns.add(sr);
        }

        // List list = this.getHibernateTemplate().find(query, parameters);
        // for(Object sequencerRun : list) {
        // sequencerRuns.add((SequencerRun)sequencerRun);
        // }

        // Limit the sequencerRuns to those owned by the user
        /*
         * expmts = this.getHibernateTemplate().find( "from SequencerRun as sequencerRun where owner_id = ? order by sequencerRun.name desc"
         * , registration.getRegistrationId() );
         */

        // expmts =
        // this.getHibernateTemplate().find("from SequencerRun as sequencerRun order by sequencerRun.name desc");
        // FIXME: why am I getting multiple values back?
        /*
         * HashMap map = new HashMap(); for(Object sequencerRun : expmts) { if
         * (!map.containsKey(((SequencerRun)sequencerRun).getSequencerRunId())) { boolean add = false; Registration currOwner =
         * ((SequencerRun)sequencerRun).getOwner(); if (registration.isLIMSAdmin()) add = true; else if (currOwner != null &&
         * currOwner.getRegistrationId().equals(registration.getRegistrationId())) add = true; else { for (Lane lane :
         * ((SequencerRun)sequencerRun).getLanes()) { if (lane.getOwner() != null && lane
         * .getOwner().getRegistrationId().equals(registration.getRegistrationId())) add = true; for (IUS ius : lane.getIUS()) { if
         * (ius.getOwner() != null && ius .getOwner().getRegistrationId().equals(registration.getRegistrationId())) add = true; } } } if
         * (add) { sequencerRuns.add((SequencerRun)sequencerRun); map.put(((SequencerRun)sequencerRun).getSequencerRunId(), "");
         * //System.err .println(" Adding sequencer run! "+((SequencerRun)sequencerRun ).getSequencerRunId()); } } }
         */
        return sequencerRuns;
    }

    /**
     * {@inheritDoc}
     *
     * Finds an instance of SequencerRun in the database by the SequencerRun name.
     */
    @Override
    public SequencerRun findByName(String name) {
        String query = "from SequencerRun as sequencerRun where sequencerRun.name = ?";
        SequencerRun sequencerRun = null;
        Object[] parameters = { name };
        List list = this.getHibernateTemplate().find(query, parameters);
        if (list.size() > 0) {
            sequencerRun = (SequencerRun) list.get(0);
        }
        return sequencerRun;
    }

    /**
     * {@inheritDoc}
     *
     * Finds an instance of SequencerRun in the database by the SequencerRun ID.
     */
    @Override
    public SequencerRunWizardDTO findByID(Integer expID) {
        String query = "from SequencerRunWizardDTO as sequencerRun where sequencerRun.sequencerRunId = ?";
        SequencerRunWizardDTO sequencerRun = null;
        Object[] parameters = { expID };
        List list = this.getHibernateTemplate().find(query, parameters);
        if (list.size() > 0) {
            sequencerRun = (SequencerRunWizardDTO) list.get(0);
        }
        return sequencerRun;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public SequencerRun findBySWAccession(Integer swAccession) {
        String query = "from SequencerRun as sequencerRun where sequencerRun.swAccession = ?";
        SequencerRun sequencerRun = null;
        Object[] parameters = { swAccession };
        List<SequencerRun> list = (List<SequencerRun>) this.getHibernateTemplate().find(query, parameters);
        if (list.size() > 0) {
            sequencerRun = list.get(0);
        }
        return sequencerRun;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public List<SequencerRun> findByOwnerID(Integer registrationId) {
        String query = "from SequencerRun as sequencerRun where sequencerRun.owner.registrationId = ?";
        Object[] parameters = { registrationId };
        return (List<SequencerRun>) this.getHibernateTemplate().find(query, parameters);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public List<SequencerRun> findByCriteria(String criteria, boolean isCaseSens) {
        String queryStringCase = "from SequencerRun as sr where " + " sr.description like :description "
                + " or cast(sr.swAccession as string) like :sw " + " or sr.name like :name order by sr.description ";
        String queryStringICase = "from SequencerRun as sr where " + " lower(sr.description) like :description "
                + " or cast(sr.swAccession as string) like :sw " + " or lower(sr.name) like :name order by sr.description ";
        Query query = isCaseSens ? this.currentSession().createQuery(queryStringCase) : this.currentSession().createQuery(queryStringICase);
        if (!isCaseSens) {
            criteria = criteria.toLowerCase();
        }
        criteria = "%" + criteria + "%";
        query.setString("description", criteria);
        query.setString("sw", criteria);
        query.setString("name", criteria);
        List<SequencerRun> res = query.list();
        filterResult(res);
        return res;
    }

    /**
     * Filter WizardDTO classes here
     *
     * @param res
     */
    private void filterResult(List<SequencerRun> res) {
        Iterator<SequencerRun> iter = res.iterator();
        while (iter.hasNext()) {
            SequencerRun val = iter.next();
            if (val instanceof SequencerRunWizardDTO) {
                iter.remove();
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public SequencerRun updateDetached(SequencerRun sequencerRun) {
        SequencerRun dbObject = reattachSequencerRun(sequencerRun);
        try {
            BeanUtilsBean beanUtils = new NullBeanUtils();
            beanUtils.copyProperties(dbObject, sequencerRun);
            return (SequencerRun) this.getHibernateTemplate().merge(dbObject);
        } catch (IllegalAccessException | InvocationTargetException e) {
            localLogger.error("Error updating detached SequencerRun", e);
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public List<SequencerRun> list() {
        ArrayList<SequencerRun> sequencerRuns = new ArrayList<>();

        // SEQWARE-1489
        // bizarre, my initial thought was to restrict this to the base class
        // however, certain SequencerRuns in the test database get dropped that way
        String query = "from SequencerRunWizardDTO as sr";

        List list = this.getHibernateTemplate().find(query);
        Log.trace("Hibernate query found " + list.size() + "sequencer runs");

        for (Object obj : list) {
            SequencerRun sr = (SequencerRun) obj;
            sequencerRuns.add(sr);
        }
        return sequencerRuns;

    }

    /** {@inheritDoc} */
    @Override
    public void update(Registration registration, SequencerRun sequencerRun) {
        SequencerRun dbObject = reattachSequencerRun(sequencerRun);
        if (registration == null) {
            localLogger.error("SequencerRunDAOHibernate update registration is null");
        } else if (registration.isLIMSAdmin() || (sequencerRun.givesPermission(registration) && dbObject.givesPermission(registration))) {
            localLogger.info("Updating sequencer run object");
            update(sequencerRun);
        } else {
            localLogger.error("sequencerRunDAOHibernate update not authorized");
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public Integer insert(Registration registration, SequencerRun sequencerRun) {
        if (registration == null) {
            localLogger.error("SequencerRunDAOHibernate insert SequencerRun registration is null");
        } else if (registration.isLIMSAdmin() || sequencerRun.givesPermission(registration)) {
            localLogger.info("insert sequencer run object");
            insert(sequencerRun);
            return (sequencerRun.getSwAccession());
        } else {
            localLogger.error("sequencerRunDAOHibernate insert not authorized");
        }
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public Integer insert(Registration registration, SequencerRunWizardDTO sequencerRun) {
        if (registration == null) {
            localLogger.error("SequencerRunDAOHibernate insert SequencerRunWizardDTO registration is null");
        } else if (registration.isLIMSAdmin() || sequencerRun.givesPermission(registration)) {
            localLogger.info("insert SequencerRunWizardDTO object");
            insert(sequencerRun);
            return (sequencerRun.getSwAccession());
        } else {
            localLogger.error("sequencerRunDAOHibernate insert not authorized");
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public SequencerRun updateDetached(Registration registration, SequencerRun sequencerRun) {
        SequencerRun dbObject = reattachSequencerRun(sequencerRun);
        if (registration == null) {
            localLogger.error("SequencerRunDAOHibernate updateDetached registration is null");
        } else if (registration.isLIMSAdmin() || dbObject.givesPermission(registration)) {
            localLogger.info("updateDetached SequencerRun object");
            return updateDetached(sequencerRun);
        } else {
            localLogger.error("sequencerRunDAOHibernate updateDetached not authorized");
        }
        return null;
    }

    private SequencerRun reattachSequencerRun(SequencerRun sequencerRun) throws IllegalStateException, DataAccessResourceFailureException {
        SequencerRun dbObject = sequencerRun;
        if (!currentSession().contains(sequencerRun)) {
            dbObject = findByID(sequencerRun.getSequencerRunId());
        }
        return dbObject;
    }
}
