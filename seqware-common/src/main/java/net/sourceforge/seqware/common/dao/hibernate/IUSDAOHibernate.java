package net.sourceforge.seqware.common.dao.hibernate;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.seqware.common.dao.IUSDAO;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.util.NullBeanUtils;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class IUSDAOHibernate extends HibernateDaoSupport implements IUSDAO {

  public IUSDAOHibernate() {
    super();
  }

  /**
   * Inserts an instance of Lane into the database.
   */
  public void insert(IUS obj) {
    this.getHibernateTemplate().save(obj);
    getSession().flush();
  }

  /**
   * Updates an instance of Lane in the database.
   */
  public void update(IUS obj) {

    this.getHibernateTemplate().update(obj);
    getSession().flush();
  }

  public void delete(IUS obj) {

    this.getHibernateTemplate().delete(obj);
  }

  public List<File> getFiles(Integer iusId) {
    List<File> files = new ArrayList<File>();

    String query = "WITH RECURSIVE processing_root_to_leaf (child_id, parent_id) AS ( "
        + "SELECT p.child_id as child_id, p.parent_id "
        + "FROM processing_relationship p inner join processing_ius pr_i on (pr_i.processing_id = p.parent_id) "
        + "where pr_i.ius_id = ?" + "UNION ALL " + "SELECT p.child_id, rl.parent_id "
        + "FROM processing_root_to_leaf rl, processing_relationship p " + "WHERE p.parent_id = rl.child_id ) "
        + "select * from File myfile where myfile.file_id in( "
        + "select distinct file_id from processing_root_to_leaf p, processing_files pf "
        + "where p.parent_id = processing_id " + "UNION ALL "
        + "select distinct file_id from processing_root_to_leaf p, processing_files pf "
        + "where p.child_id = processing_id " + "UNION ALL " + "select distinct file_id from processing_files pf "
        + "inner join processing_ius pr_i on (pr_i.processing_id = pf.processing_id) " + "where pr_i.ius_id = ? )";

    List list = this.getSession().createSQLQuery(query).addEntity(File.class).setInteger(0, iusId).setInteger(1, iusId)
        .list();

    for (Object file : list) {
      File fl = (File) file;
      files.add(fl);
    }

    return files;
  }

  public boolean isHasFile(Integer iusId) {
    boolean isHasFile = false;
    String query = "WITH RECURSIVE processing_root_to_leaf (child_id, parent_id) AS ( "
        + "SELECT p.child_id as child_id, p.parent_id "
        + "FROM processing_relationship p inner join processing_ius pr_i on (pr_i.processing_id = p.parent_id) "
        + "where pr_i.ius_id = ?" + "UNION ALL " + "SELECT p.child_id, rl.parent_id "
        + "FROM processing_root_to_leaf rl, processing_relationship p " + "WHERE p.parent_id = rl.child_id ) "
        + "select * from File myfile where myfile.file_id in( "
        + "select distinct file_id from processing_root_to_leaf p, processing_files pf "
        + "where p.parent_id = processing_id " + "UNION ALL "
        + "select distinct file_id from processing_root_to_leaf p, processing_files pf "
        + "where p.child_id = processing_id " + "UNION ALL " + "select distinct file_id from processing_files pf "
        + "inner join processing_ius pr_i on (pr_i.processing_id = pf.processing_id) "
        + "where pr_i.ius_id = ? ) LIMIT 1";

    List list = this.getSession().createSQLQuery(query).addEntity(File.class).setInteger(0, iusId).setInteger(1, iusId)
        .list();

    isHasFile = (list.size() > 0) ? true : false;

    return isHasFile;
  }

  public List<File> getFiles(Integer iusId, String metaType) {
    List<File> files = new ArrayList<File>();

    String query = "WITH RECURSIVE processing_root_to_leaf (child_id, parent_id) AS ( "
        + "SELECT p.child_id as child_id, p.parent_id "
        + "FROM processing_relationship p inner join processing_ius pr_i on (pr_i.processing_id = p.parent_id) "
        + "where pr_i.ius_id = ?" + "UNION ALL " + "SELECT p.child_id, rl.parent_id "
        + "FROM processing_root_to_leaf rl, processing_relationship p " + "WHERE p.parent_id = rl.child_id ) "
        + "select * from File myfile where myfile.meta_type=? and myfile.file_id in( "
        + "select distinct file_id from processing_root_to_leaf p, processing_files pf "
        + "where p.parent_id = processing_id " + "UNION ALL "
        + "select distinct file_id from processing_root_to_leaf p, processing_files pf "
        + "where p.child_id = processing_id " + "UNION ALL " + "select distinct file_id from processing_files pf "
        + "inner join processing_ius pr_i on (pr_i.processing_id = pf.processing_id) " + "where pr_i.ius_id = ? )";

    List list = this.getSession().createSQLQuery(query).addEntity(File.class).setInteger(0, iusId)
        .setString(1, metaType).setInteger(2, iusId).list();

    for (Object file : list) {
      File fl = (File) file;
      files.add(fl);
    }

    return files;
  }

  public boolean isHasFile(Integer iusId, String metaType) {
    boolean isHasFile = false;
    String query = "WITH RECURSIVE processing_root_to_leaf (child_id, parent_id) AS ( "
        + "SELECT p.child_id as child_id, p.parent_id "
        + "FROM processing_relationship p inner join processing_ius pr_i on (pr_i.processing_id = p.parent_id) "
        + "where pr_i.ius_id = ?" + "UNION ALL " + "SELECT p.child_id, rl.parent_id "
        + "FROM processing_root_to_leaf rl, processing_relationship p " + "WHERE p.parent_id = rl.child_id ) "
        + "select * from File myfile where myfile.meta_type=? and myfile.file_id in( "
        + "select distinct file_id from processing_root_to_leaf p, processing_files pf "
        + "where p.parent_id = processing_id " + "UNION ALL "
        + "select distinct file_id from processing_root_to_leaf p, processing_files pf "
        + "where p.child_id = processing_id " + "UNION ALL " + "select distinct file_id from processing_files pf "
        + "inner join processing_ius pr_i on (pr_i.processing_id = pf.processing_id) "
        + "where pr_i.ius_id = ? ) LIMIT 1";

    List list = this.getSession().createSQLQuery(query).addEntity(File.class).setInteger(0, iusId)
        .setString(1, metaType).setInteger(2, iusId).list();

    isHasFile = (list.size() > 0) ? true : false;

    return isHasFile;
  }

  public IUS findByID(Integer id) {
    String query = "from IUS as ius where ius.iusId = ?";
    IUS obj = null;
    Object[] parameters = { id };
    List list = this.getHibernateTemplate().find(query, parameters);
    if (list.size() > 0) {
      obj = (IUS) list.get(0);
    }
    return obj;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<IUS> findByOwnerID(Integer registrationId) {
    String query = "from IUS as ius where ius.owner.registrationId = ?";
    Object[] parameters = { registrationId };
    return this.getHibernateTemplate().find(query, parameters);
  }

  @SuppressWarnings("unchecked")
  @Override
  public IUS findBySWAccession(Integer swAccession) {
    String query = "from IUS as ius where ius.swAccession = ?";
    IUS obj = null;
    Object[] parameters = { swAccession };
    List<IUS> list = this.getHibernateTemplate().find(query, parameters);
    if (list.size() > 0) {
      obj = (IUS) list.get(0);
    }
    return obj;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<IUS> findByCriteria(String criteria, boolean isCaseSens) {
    String queryStringCase = "from IUS as i where i.name like :name " + " or i.description like :description "
        + " or cast(i.swAccession as string) like :sw order by i.name, i.description";
    String queryStringICase = "from IUS as i where lower(i.name) like :name "
        + " or lower(i.description) like :description "
        + " or cast(i.swAccession as string) like :sw order by i.name, i.description";
    Query query = isCaseSens ? this.getSession().createQuery(queryStringCase) : this.getSession().createQuery(
        queryStringICase);
    if (!isCaseSens) {
      criteria = criteria.toLowerCase();
    }
    criteria = "%" + criteria + "%";
    query.setString("name", criteria);
    query.setString("description", criteria);
    query.setString("sw", criteria);

    return query.list();
  }

  @Override
  public IUS updateDetached(IUS ius) {
    IUS dbObject = findByID(ius.getIusId());
    try {
      BeanUtilsBean beanUtils = new NullBeanUtils();
      beanUtils.copyProperties(dbObject, ius);
      return (IUS) this.getHibernateTemplate().merge(dbObject);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    return null;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public List<IUS> findBelongsToStudy(Study study) {
    List<IUS> iuses = new ArrayList<IUS>();

    // Try without Recursive SQL
    for (Experiment exp : study.getExperiments()) {
      for (Sample sample : exp.getSamples()) {
        iuses.addAll(iusForSample(sample));
      }
    }

    // String query = "WITH RECURSIVE Rec(pid, id) as" +
    // " (select parent_id, child_id from sample_relationship"
    // + " union " + " select r.pid, sr.child_id from Rec r " +
    // " join sample_relationship sr "
    // + " on (r.id = sr.parent_id)) " + " select i.* from Rec r " +
    // " join sample s on (s.sample_id = r.id) "
    // + " join sample sp on (sp.sample_id = r.pid) " +
    // " join experiment e on (e.experiment_id = sp.experiment_id) "
    // + " join study st on (st.study_id = e.study_id) " +
    // " join ius i on (i.sample_id = r.id) "
    // + " where r.pid not in (select child_id from sample_relationship) " +
    // " and i.ius_id is not null "
    // + " and st.study_id = ? " + " union "
    // +
    // " select i.* from sample s join experiment e on (e.experiment_id = s.experiment_id) "
    // +
    // " join study st on (st.study_id = e.study_id) join ius i on (i.sample_id = s.sample_id) "
    // + " and st.study_id = ? ";
    //
    // List list =
    // this.getSession().createSQLQuery(query).addEntity(IUS.class).setInteger(0,
    // study.getStudyId())
    // .setInteger(1, study.getStudyId()).list();
    //
    // for (Object iusObj : list) {
    // IUS ius = (IUS) iusObj;
    // iuses.add(ius);
    // }
    return iuses;
  }

  private Set<IUS> iusForSample(Sample sample) {
    Set<IUS> iuses = new HashSet<IUS>();

    iuses.addAll(sample.getIUS());

    if (sample.getChildren() != null) {
      for (Sample child : sample.getChildren()) {
        Set<IUS> ius = iusForSample(child);
        iuses.addAll(ius);
      }
    }
    return iuses;
  }

  @Override
  public List<IUS> list() {
    ArrayList<IUS> l = new ArrayList<IUS>();

    String query = "from IUS";

    List list = this.getHibernateTemplate().find(query);

    for (Object e : list) {
      l.add((IUS) e);
    }

    return l;
  }

  @Override
  public void update(Registration registration, IUS ius) {
    IUS dbObject = reattachIUS(ius);
    Logger logger = Logger.getLogger(IUSDAOHibernate.class);
    if (registration == null) {
      logger.error("IUSDAOHibernate update registration is null");
    } else if (registration.isLIMSAdmin()
        || (ius.givesPermission(registration) && dbObject.givesPermission(registration))) {
      logger.info("Updating IUS object");
      update(ius);
    } else {
      logger.error("IUSDAOHibernate update not authorized");
    }

  }

  @Override
  public void insert(Registration registration, IUS obj) {
    Logger logger = Logger.getLogger(IUSDAOHibernate.class);
    if (registration == null) {
      logger.error("IUSDAOHibernate insert registration is null");
    } else if (registration.isLIMSAdmin() || obj.givesPermission(registration)) {
      logger.info("insert IUS object");
      insert(obj);
    } else {
      logger.error("IUSDAOHibernate insert not authorized");
    }
  }

  @Override
  public IUS updateDetached(Registration registration, IUS ius) {
    IUS dbObject = reattachIUS(ius);
    Logger logger = Logger.getLogger(IUSDAOHibernate.class);
    if (registration == null) {
      logger.error("IUSDAOHibernate updateDetached registration is null");
    } else if (registration.isLIMSAdmin() || dbObject.givesPermission(registration)) {
      logger.info("updateDetached IUS object");
      return updateDetached(ius);
    } else {
      logger.error("IUSDAOHibernate updateDetached not authorized");
    }
    return null;
  }

  private IUS reattachIUS(IUS ius) throws IllegalStateException, DataAccessResourceFailureException {
    IUS dbObject = ius;
    if (!getSession().contains(ius)) {
      dbObject = findByID(ius.getIusId());
    }
    return dbObject;
  }

  @Override
  public List<IUS> find(String sequencerRunName, Integer lane, String sampleName) {
    checkNotNull(sequencerRunName);
    checkNotNull(lane);
    checkState(lane > 0, "lane must greater than 0");
    Integer laneIndex = lane - 1;
    String queryString = "from IUS as i where i.lane.laneIndex = :laneIndex and i.lane.sequencerRun.name = :sequencerRunName";
    if (sampleName != null) {
      queryString += " and i.sample.name = :sampleName";
    }
    Query query = getSession().createQuery(queryString);
    query.setInteger("laneIndex", laneIndex);
    query.setString("sequencerRunName", sequencerRunName);
    if (sampleName != null) {
      query.setString("sampleName", sampleName);
    }
    @SuppressWarnings("unchecked")
    List<IUS> records = query.list();
    return records;
  }
}
