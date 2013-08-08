package net.sourceforge.seqware.common.dao.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.seqware.common.dao.StudyDAO;
import net.sourceforge.seqware.common.hibernate.FindAllTheFiles;
import net.sourceforge.seqware.common.hibernate.PropagateOwnership;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.NullBeanUtils;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.log4j.Logger;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Query;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * <p>StudyDAOHibernate class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class StudyDAOHibernate extends HibernateDaoSupport implements StudyDAO {

  private Logger logger;

  /**
   * <p>Constructor for StudyDAOHibernate.</p>
   */
  public StudyDAOHibernate() {
    super();
    logger = Logger.getLogger(StudyDAOHibernate.class);
  }

  /** {@inheritDoc} */
  public Integer insert(Study study) {
    this.getHibernateTemplate().save(study);
    this.getSession().flush();
    return study.getSwAccession();
  }

  /** {@inheritDoc} */
  public void update(Study study) {
    this.getHibernateTemplate().update(study);
    getSession().flush();
  }

  /** {@inheritDoc} */
  @Override
  public void merge(Study study) {
    this.getHibernateTemplate().merge(study);
    getSession().flush();
  }

  /** {@inheritDoc} 
   * 
   * This deletion will result in just the study and experiments being deleted but the samples and IUS will remain.
   * This will potentially cause orphans which is not really at all good.  A better solution 
   * is to never delete but just use a deletion attribute.
   * 
   */
  public void delete(Study study) {
    // clear experiments
    for (Experiment e : study.getExperiments()) {
      for (Sample s : e.getSamples()) {
        s.setExperiment(null);
        this.getHibernateTemplate().update(s);
      }
      e.setSamples(null);
      this.getHibernateTemplate().update(e);
    }
    //study.getExperiments().clear();
    // flush the change
    this.getHibernateTemplate().update(study);
    this.getHibernateTemplate().flush();
    // and delete the study
    this.getHibernateTemplate().delete(study);
  }

  /** {@inheritDoc} */
  public List<Study> list(Registration registration, Boolean isAsc) {
    ArrayList<Study> studys = new ArrayList<Study>();
    logger.debug("Get Study LIST for " + registration.getEmailAddress());
    /*
     * if(registration == null) return studys;
     */

    /*
     * Criteria criteria = this.getSession().createCriteria(Study.class);
     * criteria.add(Expression.eq("owner_id",
     * registration.getRegistrationId()));
     * criteria.addOrder(Order.asc("create_tstmp"));
     * criteria.setFirstResult(100); criteria.setMaxResults(50); List
     * pageResults=criteria.list();
     */
    /*
     * List studies = this.getHibernateTemplate().find( "from Study as study
     * order by study.title desc");
     */
    String query = "";
    Object[] parameters = { registration.getRegistrationId() };

    String sortValue = (isAsc) ? "asc" : "desc";

    if (registration.isLIMSAdmin()) {
      query = "from Study as study order by study.title " + sortValue;
      parameters = null;
    } else {
      query = "from Study as study where study.owner.registrationId=? order by study.title " + sortValue;
    }

    List list = this.getHibernateTemplate().find(query, parameters);

    for (Object study : list) {
      studys.add((Study) study);
    }

    // List studies = this.getHibernateTemplate().find(
    // "from Study as study order by study.createTimestamp desc");

    // Limit the studys to those owned by the user
    /*
     * expmts = this.getHibernateTemplate().find( "from Study as study where
     * owner = ? order by study.title desc", registration );
     */

    // filter by ownership
    /*
     * HashMap map = new HashMap(); for(Object study : studies) { if
     * (!map.containsKey(((Study)study).getStudyId())) { boolean add = false;
     * Registration currOwner = ((Study)study).getOwner(); //if
     * (registration.isLIMSAdmin()) add = true; //else if (currOwner != null &&
     * currOwner.getRegistrationId().equals(registration.getRegistrationId()))
     * add = true; /* else { for (Experiment exp :
     * (((Study)study).getExperiments())) { if (exp.getOwner() != null &&
     * exp.getOwner
     * ().getRegistrationId().equals(registration.getRegistrationId())) add =
     * true; for (Sample sample : exp.getSamples()) { if (sample.getOwner() !=
     * null &&
     * sample.getOwner().getRegistrationId().equals(registration.getRegistrationId
     * ())) add = true; } } }
     */
    /*
     * if (add) { studys.add((Study)study); map.put(((Study)study).getStudyId(),
     * ""); } } }
     */
    return studys;
  }

  /** {@inheritDoc} */
  @SuppressWarnings("rawtypes")
  @Override
  public List<Study> list() {
    ArrayList<Study> studys = new ArrayList<Study>();

    String query = "from Study as study order by study.title ";

    List list = this.getHibernateTemplate().find(query);

    for (Object study : list) {
      studys.add((Study) study);
    }

    return studys;
  }

  /** {@inheritDoc} */
  public List<Study> listMyShared(Registration registration, Boolean isAsc) {
    List<Study> sharedStudies = new ArrayList<Study>();

    String sortValue = (isAsc) ? "asc" : "desc";

    String query = "from Study as study where study.owner.registrationId=? "
        + "and study.sharedStudies.size > 0 order by study.title " + sortValue;
    Object[] parameters = { registration.getRegistrationId() };
    List list = this.getHibernateTemplate().find(query, parameters);

    for (Object study : list) {
      sharedStudies.add((Study) study);
    }
    /*
     * List<Study> studys = list(registration); for (Study study : studys) {
     * if(!study.getSharedStudies().isEmpty()){ sharedStudies.add(study); } }
     */
    return sharedStudies;
  }

  /** {@inheritDoc} */
  public List<Study> listSharedWithMe(Registration registration, Boolean isAsc) {
    ArrayList<Study> studys = new ArrayList<Study>();

    String sortValue = (isAsc) ? "asc" : "desc";

    String query = "select study from Study as study inner join study.sharedStudies as shSt"
        + " where shSt.registration.registrationId = ? order by study.title " + sortValue;
    Object[] parameters = { registration.getRegistrationId() };
    List list = this.getHibernateTemplate().find(query, parameters);

    for (Object study : list) {
      studys.add((Study) study);
    }

    /*
     * List studies = this.getHibernateTemplate().find( "from Study as study
     * order by study.createTimestamp desc");
     * 
     * // filter by ownership HashMap map = new HashMap(); for(Object study :
     * studies) { if (!map.containsKey(((Study)study).getStudyId())) { boolean
     * add = false; Registration currOwner = ((Study)study).getOwner();
     * 
     * Set<ShareStudy> set = ((Study)study).getSharedStudies();
     * 
     * // logger.debug("Emails:"); for (ShareStudy shareStudy : set) { //String
     * email = ((ShareStudy)it.next()).getEmail();
     * 
     * // logger.debug("	email:" + email + ";"); // logger.debug("	user :" +
     * currOwner.getEmailAddress() + ";"); //
     * if(email.equals(registration.getEmailAddress())){
     * if(registration.equals(shareStudy.getRegistration())){ add = true; } }
     * 
     * if (add) { studys.add((Study)study); map.put(((Study)study).getStudyId(),
     * ""); } } }
     */
    return studys;
  }

  /** {@inheritDoc} */
  public List<File> getFiles(Integer studyId) {
    List<File> files = new ArrayList<File>();
    String query = "WITH RECURSIVE processing_root_to_leaf (child_id, parent_id) AS ( "
        + "SELECT p.child_id as child_id, p.parent_id "
        + "FROM processing_relationship p inner join processing_ius pr_i on (pr_i.processing_id = p.parent_id) "
        + "inner join ius i on (i.ius_id = pr_i.ius_id) "
        + "inner join sample s on (s.sample_id = i.sample_id) "
        + "inner join experiment ex on (ex.experiment_id = s.experiment_id) "
        + "where ex.study_id = ? "
        + "UNION "
        + "SELECT p.child_id as child_id, p.parent_id "
        + "FROM processing_relationship p inner join processing_studies ps on (ps.processing_id = p.parent_id) "
        + "where ps.study_id = ? "
        + "UNION "
        + "SELECT p.child_id as child_id, p.parent_id "
        + "FROM processing_relationship p inner join processing_experiments p_ex on (p_ex.processing_id = p.parent_id) "
        + "inner join experiment ex on (p_ex.experiment_id = ex.experiment_id) "
        + "where ex.study_id = ? "
        + "UNION "
        + "SELECT p.child_id as child_id, p.parent_id "
        + "FROM processing_relationship p inner join processing_samples p_sam on (p_sam.processing_id = p.parent_id) "
        + "inner join sample sam on (p_sam.sample_id = sam.sample_id) "
        + "inner join experiment ex on (sam.experiment_id = ex.experiment_id) "
        + "where ex.study_id = ? "
        + // --------------------------------------------------------------------------------
        // nested samples ius rec
        "UNION( "
        + "WITH RECURSIVE sample_root_to_leaf (child_id, parent_id) AS ( "
        + "SELECT s_rec.child_id as child_id, s_rec.parent_id FROM sample_relationship s_rec "
        + "INNER JOIN sample sam on (s_rec.parent_id = sam.sample_id) "
        + "INNER JOIN experiment ex on (sam.experiment_id = ex.experiment_id) "
        + "where ex.study_id = ? "
        + "UNION ALL "
        + "SELECT s_rec.child_id, s_rl.parent_id "
        + "FROM sample_root_to_leaf s_rl, sample_relationship s_rec "
        + "WHERE s_rec.parent_id = s_rl.child_id ) "
        + "select p.child_id as child_id, p.parent_id from sample_root_to_leaf s_rec, ius i_rec, processing_ius pr_i, processing_relationship p "
        + "where (s_rec.parent_id = i_rec.sample_id or s_rec.child_id = i_rec.sample_id) "
        + "and pr_i.ius_id = i_rec.ius_id and p.parent_id = pr_i.processing_id ) "
        + // nested samples processing_samples rec
        "UNION( "
        + "WITH RECURSIVE sample_root_to_leaf (child_id, parent_id) AS ( "
        + "SELECT s_rec.child_id as child_id, s_rec.parent_id FROM sample_relationship s_rec "
        + "INNER JOIN sample sam on (s_rec.parent_id = sam.sample_id) "
        + "INNER JOIN experiment ex on (sam.experiment_id = ex.experiment_id) "
        + "where ex.study_id = ? "
        + "UNION ALL "
        + "SELECT s_rec.child_id, s_rl.parent_id "
        + "FROM sample_root_to_leaf s_rl, sample_relationship s_rec "
        + "WHERE s_rec.parent_id = s_rl.child_id ) "
        + "select p.child_id as child_id, p.parent_id from sample_root_to_leaf s_rec, processing_samples p_sam, processing_relationship p "
        + "where (s_rec.parent_id = p_sam.sample_id or s_rec.child_id = p_sam.sample_id) "
        + "and p.parent_id = p_sam.processing_id ) "
        + // --------------------------------------------------------------------------------
        "UNION ALL "
        + "SELECT p.child_id, rl.parent_id "
        + "FROM processing_root_to_leaf rl, processing_relationship p "
        + "WHERE p.parent_id = rl.child_id) "
        + "select * from File myfile where myfile.file_id in( "
        + "select distinct file_id from processing_root_to_leaf p, processing_files pf "
        + "where p.parent_id = processing_id "
        + "UNION ALL "
        + "select distinct file_id from processing_root_to_leaf p, processing_files pf "
        + "where p.child_id = processing_id "
        + "UNION ALL "
        + "select distinct file_id from processing_files pf inner join processing_ius pr_i "
        + "on (pr_i.processing_id = pf.processing_id) "
        + "inner join ius i on (i.ius_id = pr_i.ius_id) "
        + "inner join sample s on (s.sample_id = i.sample_id) "
        + "inner join experiment ex on (ex.experiment_id = s.experiment_id) "
        + "where ex.study_id = ? "
        + "UNION "
        + "select distinct file_id from processing_files pf inner join processing_studies ps "
        + "on (ps.processing_id = pf.processing_id) "
        + "where ps.study_id = ? "
        + "UNION "
        + "select distinct file_id from processing_files pf inner join processing_experiments p_ex "
        + "on (p_ex.processing_id = pf.processing_id) "
        + "inner join experiment ex on (p_ex.experiment_id = ex.experiment_id) "
        + "where ex.study_id = ? "
        + "UNION "
        + "select distinct file_id from processing_files pf inner join processing_samples p_sam "
        + "on (p_sam.processing_id = pf.processing_id) "
        + "inner join sample sam on (p_sam.sample_id = sam.sample_id) "
        + "inner join experiment ex on (ex.experiment_id = sam.experiment_id) "
        + "where ex.study_id = ? "
        + // nested samples ius, first files
        "UNION ( "
        + "WITH RECURSIVE sample_root_to_leaf (child_id, parent_id) AS ( "
        + "SELECT s_rec.child_id as child_id, s_rec.parent_id FROM sample_relationship s_rec "
        + "INNER JOIN sample sam on (s_rec.parent_id = sam.sample_id) "
        + "INNER JOIN experiment ex on (sam.experiment_id = ex.experiment_id) "
        + "where ex.study_id = ? "
        + "UNION ALL "
        + "SELECT s_rec.child_id, s_rl.parent_id "
        + "FROM sample_root_to_leaf s_rl, sample_relationship s_rec "
        + "WHERE s_rec.parent_id = s_rl.child_id ) "
        + "select distinct pf.file_id from sample_root_to_leaf s_rec, ius i, processing_ius pr_i, processing_files pf "
        + "where (s_rec.parent_id = i.sample_id or s_rec.child_id = i.sample_id) "
        + "and pr_i.ius_id = i.ius_id and pf.processing_id = pr_i.processing_id ) "
        + // nested samples processing_samples, first files
        "UNION ( " + "WITH RECURSIVE sample_root_to_leaf (child_id, parent_id) AS ( "
        + "SELECT s_rec.child_id as child_id, s_rec.parent_id FROM sample_relationship s_rec "
        + "INNER JOIN sample sam on (s_rec.parent_id = sam.sample_id) "
        + "INNER JOIN experiment ex on (sam.experiment_id = ex.experiment_id) " + "where ex.study_id = ? "
        + "UNION ALL " + "SELECT s_rec.child_id, s_rl.parent_id "
        + "FROM sample_root_to_leaf s_rl, sample_relationship s_rec " + "WHERE s_rec.parent_id = s_rl.child_id ) "
        + "select distinct pf.file_id from sample_root_to_leaf s_rec, processing_samples p_sam, processing_files pf "
        + "where (s_rec.parent_id = p_sam.sample_id or s_rec.child_id = p_sam.sample_id) "
        + "and p_sam.processing_id = pf.processing_id ) )";

    List list = this.getSession().createSQLQuery(query).addEntity(File.class).setInteger(0, studyId)
        .setInteger(1, studyId).setInteger(2, studyId).setInteger(3, studyId).setInteger(4, studyId)
        .setInteger(5, studyId).setInteger(6, studyId).setInteger(7, studyId).setInteger(8, studyId)
        .setInteger(9, studyId).setInteger(10, studyId).setInteger(11, studyId).list();

    for (Object file : list) {
      File fl = (File) file;
      files.add(fl);
    }
    return files;
  }

  /** {@inheritDoc} */
  public boolean isHasFile(Integer studyId) {
    boolean isHasFile = false;

    /*
     * String query = "WITH RECURSIVE processing_root_to_leaf (child_id,
     * parent_id) AS ( " + "SELECT p.child_id as child_id, p.parent_id " + "FROM
     * processing_relationship p inner join processing_ius pr_i on
     * (pr_i.processing_id = p.parent_id) " + "inner join ius i on (i.ius_id =
     * pr_i.ius_id) " + "inner join sample s on (s.sample_id = i.sample_id)
     * " + "inner join experiment ex on (ex.experiment_id = s.experiment_id)
     * " + "where ex.study_id = ? " + "UNION " + "SELECT p.child_id as child_id,
     * p.parent_id " + "FROM processing_relationship p inner join
     * processing_studies ps on (ps.processing_id = p.parent_id) " + "where
     * ps.study_id = ? " + "UNION " + "SELECT p.child_id as child_id,
     * p.parent_id " + "FROM processing_relationship p inner join
     * processing_experiments p_ex on (p_ex.processing_id = p.parent_id)
     * " + "inner join experiment ex on (p_ex.experiment_id = ex.experiment_id)
     * " + "where ex.study_id = ? " + "UNION " + "SELECT p.child_id as child_id,
     * p.parent_id " + "FROM processing_relationship p inner join
     * processing_samples p_sam on (p_sam.processing_id = p.parent_id)
     * " + "inner join sample sam on (p_sam.sample_id = sam.sample_id)
     * " + "inner join experiment ex on (sam.experiment_id = ex.experiment_id)
     * " + "where ex.study_id = ? " + "UNION ALL " +
     * "SELECT p.child_id, rl.parent_id " + "FROM processing_root_to_leaf rl,
     * processing_relationship p " + "WHERE p.parent_id = rl.child_id) " +
     * "select * from File myfile where myfile.file_id in( " + "select distinct
     * file_id from processing_root_to_leaf p, processing_files pf " + "where
     * p.parent_id = processing_id " + "UNION ALL " + "select distinct file_id
     * from processing_root_to_leaf p, processing_files pf " + "where p.child_id
     * = processing_id " + "UNION ALL " + "select distinct file_id from
     * processing_files pf inner join processing_ius pr_i " + "on
     * (pr_i.processing_id = pf.processing_id) " + "inner join ius i on
     * (i.ius_id = pr_i.ius_id) " + "inner join sample s on (s.sample_id =
     * i.sample_id) " + "inner join experiment ex on (ex.experiment_id =
     * s.experiment_id) " + "where ex.study_id = ? " + "UNION " + "select
     * distinct file_id from processing_files pf inner join processing_studies
     * ps " + "on (ps.processing_id = pf.processing_id) " + "where ps.study_id =
     * ? " + "UNION " + "select distinct file_id from processing_files pf inner
     * join processing_experiments p_ex " + "on (p_ex.processing_id =
     * pf.processing_id) " + "inner join experiment ex on (p_ex.experiment_id =
     * ex.experiment_id) " + "where ex.study_id = ? " + "UNION " + "select
     * distinct file_id from processing_files pf inner join processing_samples
     * p_sam " + "on (p_sam.processing_id = pf.processing_id) " + "inner join
     * sample sam on (p_sam.sample_id = sam.sample_id) " + "inner join
     * experiment ex on (ex.experiment_id = sam.experiment_id) " + "where
     * ex.study_id = ? ) LIMIT 1";
     */
    // FIXME: doens't this need IUS?
    String query = "WITH RECURSIVE processing_root_to_leaf (child_id, parent_id) AS ( "
        + "SELECT p.child_id as child_id, p.parent_id "
        + "FROM processing_relationship p inner join processing_lanes l on (l.processing_id = p.parent_id) "
        + "inner join lane ln on (ln.lane_id = l.lane_id) "
        + "inner join sample s on (s.sample_id = ln.sample_id)  "
        + "inner join experiment ex on (ex.experiment_id = s.experiment_id) "
        + "inner join study st on (st.study_id = ex.study_id) "
        + "where st.study_id = ? "
        + "UNION ALL "
        + "SELECT p.child_id as child_id, p.parent_id "
        + "FROM processing_relationship p inner join processing_ius pr_i on (pr_i.processing_id = p.parent_id) "
        + "inner join ius i on (i.ius_id = pr_i.ius_id) "
        + "inner join sample s on (s.sample_id = i.sample_id) "
        + "inner join experiment ex on (ex.experiment_id = s.experiment_id) "
        + "where ex.study_id = ? "
        + "UNION "
        + "SELECT p.child_id as child_id, p.parent_id "
        + "FROM processing_relationship p inner join processing_studies ps on (ps.processing_id = p.parent_id) "
        + "where ps.study_id = ? "
        + "UNION "
        + "SELECT p.child_id as child_id, p.parent_id "
        + "FROM processing_relationship p inner join processing_experiments p_ex on (p_ex.processing_id = p.parent_id) "
        + "inner join experiment ex on (p_ex.experiment_id = ex.experiment_id) "
        + "where ex.study_id = ? "
        + "UNION "
        + "SELECT p.child_id as child_id, p.parent_id "
        + "FROM processing_relationship p inner join processing_samples p_sam on (p_sam.processing_id = p.parent_id) "
        + "inner join sample sam on (p_sam.sample_id = sam.sample_id) "
        + "inner join experiment ex on (sam.experiment_id = ex.experiment_id) "
        + "where ex.study_id = ? "
        + // --------------------------------------------------------------------------------
        // nested samples ius rec
        "UNION( "
        + "WITH RECURSIVE sample_root_to_leaf (child_id, parent_id) AS ( "
        + "SELECT s_rec.child_id as child_id, s_rec.parent_id FROM sample_relationship s_rec "
        + "INNER JOIN sample sam on (s_rec.parent_id = sam.sample_id) "
        + "INNER JOIN experiment ex on (sam.experiment_id = ex.experiment_id) "
        + "where ex.study_id = ? "
        + "UNION ALL "
        + "SELECT s_rec.child_id, s_rl.parent_id "
        + "FROM sample_root_to_leaf s_rl, sample_relationship s_rec "
        + "WHERE s_rec.parent_id = s_rl.child_id ) "
        + "select p.child_id as child_id, p.parent_id from sample_root_to_leaf s_rec, ius i_rec, processing_ius pr_i, processing_relationship p "
        + "where (s_rec.parent_id = i_rec.sample_id or s_rec.child_id = i_rec.sample_id) "
        + "and pr_i.ius_id = i_rec.ius_id and p.parent_id = pr_i.processing_id ) "
        + // nested samples processing_samples rec
        "UNION( "
        + "WITH RECURSIVE sample_root_to_leaf (child_id, parent_id) AS ( "
        + "SELECT s_rec.child_id as child_id, s_rec.parent_id FROM sample_relationship s_rec "
        + "INNER JOIN sample sam on (s_rec.parent_id = sam.sample_id) "
        + "INNER JOIN experiment ex on (sam.experiment_id = ex.experiment_id) "
        + "where ex.study_id = ? "
        + "UNION ALL "
        + "SELECT s_rec.child_id, s_rl.parent_id "
        + "FROM sample_root_to_leaf s_rl, sample_relationship s_rec "
        + "WHERE s_rec.parent_id = s_rl.child_id ) "
        + "select p.child_id as child_id, p.parent_id from sample_root_to_leaf s_rec, processing_samples p_sam, processing_relationship p "
        + "where (s_rec.parent_id = p_sam.sample_id or s_rec.child_id = p_sam.sample_id) "
        + "and p.parent_id = p_sam.processing_id ) "
        + // --------------------------------------------------------------------------------
        "UNION ALL "
        + "SELECT p.child_id, rl.parent_id "
        + "FROM processing_root_to_leaf rl, processing_relationship p "
        + "WHERE p.parent_id = rl.child_id) "
        + "select * from File myfile where myfile.file_id in( "
        + "select distinct file_id from processing_root_to_leaf p, processing_files pf "
        + "where p.parent_id = processing_id "
        + "UNION ALL "
        + "select distinct file_id from processing_root_to_leaf p, processing_files pf "
        + "where p.child_id = processing_id "
        + "UNION ALL "
        + "select distinct file_id from processing_files pf inner join processing_ius pr_i "
        + "on (pr_i.processing_id = pf.processing_id) "
        + "inner join ius i on (i.ius_id = pr_i.ius_id) "
        + "inner join sample s on (s.sample_id = i.sample_id) "
        + "inner join experiment ex on (ex.experiment_id = s.experiment_id) "
        + "where ex.study_id = ? "
        + "UNION "
        + "select distinct file_id from processing_files pf inner join processing_studies ps "
        + "on (ps.processing_id = pf.processing_id) "
        + "where ps.study_id = ? "
        + "UNION "
        + "select distinct file_id from processing_files pf inner join processing_experiments p_ex "
        + "on (p_ex.processing_id = pf.processing_id) "
        + "inner join experiment ex on (p_ex.experiment_id = ex.experiment_id) "
        + "where ex.study_id = ? "
        + "UNION "
        + "select distinct file_id from processing_files pf inner join processing_samples p_sam "
        + "on (p_sam.processing_id = pf.processing_id) "
        + "inner join sample sam on (p_sam.sample_id = sam.sample_id) "
        + "inner join experiment ex on (ex.experiment_id = sam.experiment_id) "
        + "where ex.study_id = ? "
        + // nested samples ius, first files
        "UNION ( "
        + "WITH RECURSIVE sample_root_to_leaf (child_id, parent_id) AS ( "
        + "SELECT s_rec.child_id as child_id, s_rec.parent_id FROM sample_relationship s_rec "
        + "INNER JOIN sample sam on (s_rec.parent_id = sam.sample_id) "
        + "INNER JOIN experiment ex on (sam.experiment_id = ex.experiment_id) "
        + "where ex.study_id = ? "
        + "UNION ALL "
        + "SELECT s_rec.child_id, s_rl.parent_id "
        + "FROM sample_root_to_leaf s_rl, sample_relationship s_rec "
        + "WHERE s_rec.parent_id = s_rl.child_id ) "
        + "select distinct pf.file_id from sample_root_to_leaf s_rec, ius i, processing_ius pr_i, processing_files pf "
        + "where (s_rec.parent_id = i.sample_id or s_rec.child_id = i.sample_id) "
        + "and pr_i.ius_id = i.ius_id and pf.processing_id = pr_i.processing_id ) "
        + // nested samples processing_samples, first files
        "UNION ( " + "WITH RECURSIVE sample_root_to_leaf (child_id, parent_id) AS ( "
        + "SELECT s_rec.child_id as child_id, s_rec.parent_id FROM sample_relationship s_rec "
        + "INNER JOIN sample sam on (s_rec.parent_id = sam.sample_id) "
        + "INNER JOIN experiment ex on (sam.experiment_id = ex.experiment_id) " + "where ex.study_id = ? "
        + "UNION ALL " + "SELECT s_rec.child_id, s_rl.parent_id "
        + "FROM sample_root_to_leaf s_rl, sample_relationship s_rec " + "WHERE s_rec.parent_id = s_rl.child_id ) "
        + "select distinct pf.file_id from sample_root_to_leaf s_rec, processing_samples p_sam, processing_files pf "
        + "where (s_rec.parent_id = p_sam.sample_id or s_rec.child_id = p_sam.sample_id) "
        + "and p_sam.processing_id = pf.processing_id ) ) LIMIT 1";

    List list = this.getSession().createSQLQuery(query).addEntity(File.class).setInteger(0, studyId)
        .setInteger(1, studyId).setInteger(2, studyId).setInteger(3, studyId).setInteger(4, studyId)
        .setInteger(5, studyId).setInteger(6, studyId).setInteger(7, studyId).setInteger(8, studyId)
        .setInteger(9, studyId).setInteger(10, studyId).setInteger(11, studyId).setInteger(12, studyId).list();

    isHasFile = (list.size() > 0) ? true : false;
    return isHasFile;
  }

  /** {@inheritDoc} */
  public List<File> getFiles(Integer studyId, String metaType) {
    List<File> files = new ArrayList<File>();
    String query = "WITH RECURSIVE processing_root_to_leaf (child_id, parent_id) AS ( "
        + "SELECT p.child_id as child_id, p.parent_id "
        + "FROM processing_relationship p inner join processing_ius pr_i on (pr_i.processing_id = p.parent_id) "
        + "inner join ius i on (i.ius_id = pr_i.ius_id) "
        + "inner join sample s on (s.sample_id = i.sample_id) "
        + "inner join experiment ex on (ex.experiment_id = s.experiment_id) "
        + "where ex.study_id = ? "
        + "UNION "
        + "SELECT p.child_id as child_id, p.parent_id "
        + "FROM processing_relationship p inner join processing_studies ps on (ps.processing_id = p.parent_id) "
        + "where ps.study_id = ? "
        + "UNION "
        + "SELECT p.child_id as child_id, p.parent_id "
        + "FROM processing_relationship p inner join processing_experiments p_ex on (p_ex.processing_id = p.parent_id) "
        + "inner join experiment ex on (p_ex.experiment_id = ex.experiment_id) "
        + "where ex.study_id = ? "
        + "UNION "
        + "SELECT p.child_id as child_id, p.parent_id "
        + "FROM processing_relationship p inner join processing_samples p_sam on (p_sam.processing_id = p.parent_id) "
        + "inner join sample sam on (p_sam.sample_id = sam.sample_id) "
        + "inner join experiment ex on (sam.experiment_id = ex.experiment_id) "
        + "where ex.study_id = ? "
        + // --------------------------------------------------------------------------------
        // nested samples ius rec
        "UNION( "
        + "WITH RECURSIVE sample_root_to_leaf (child_id, parent_id) AS ( "
        + "SELECT s_rec.child_id as child_id, s_rec.parent_id FROM sample_relationship s_rec "
        + "INNER JOIN sample sam on (s_rec.parent_id = sam.sample_id) "
        + "INNER JOIN experiment ex on (sam.experiment_id = ex.experiment_id) "
        + "where ex.study_id = ? "
        + "UNION ALL "
        + "SELECT s_rec.child_id, s_rl.parent_id "
        + "FROM sample_root_to_leaf s_rl, sample_relationship s_rec "
        + "WHERE s_rec.parent_id = s_rl.child_id ) "
        + "select p.child_id as child_id, p.parent_id from sample_root_to_leaf s_rec, ius i_rec, processing_ius pr_i, processing_relationship p "
        + "where (s_rec.parent_id = i_rec.sample_id or s_rec.child_id = i_rec.sample_id) "
        + "and pr_i.ius_id = i_rec.ius_id and p.parent_id = pr_i.processing_id ) "
        + // nested samples processing_samples rec
        "UNION( "
        + "WITH RECURSIVE sample_root_to_leaf (child_id, parent_id) AS ( "
        + "SELECT s_rec.child_id as child_id, s_rec.parent_id FROM sample_relationship s_rec "
        + "INNER JOIN sample sam on (s_rec.parent_id = sam.sample_id) "
        + "INNER JOIN experiment ex on (sam.experiment_id = ex.experiment_id) "
        + "where ex.study_id = ? "
        + "UNION ALL "
        + "SELECT s_rec.child_id, s_rl.parent_id "
        + "FROM sample_root_to_leaf s_rl, sample_relationship s_rec "
        + "WHERE s_rec.parent_id = s_rl.child_id ) "
        + "select p.child_id as child_id, p.parent_id from sample_root_to_leaf s_rec, processing_samples p_sam, processing_relationship p "
        + "where (s_rec.parent_id = p_sam.sample_id or s_rec.child_id = p_sam.sample_id) "
        + "and p.parent_id = p_sam.processing_id ) "
        + // --------------------------------------------------------------------------------
        "UNION ALL "
        + "SELECT p.child_id, rl.parent_id "
        + "FROM processing_root_to_leaf rl, processing_relationship p "
        + "WHERE p.parent_id = rl.child_id) "
        + "select * from File myfile where myfile.meta_type=? and myfile.file_id in( "
        + "select distinct file_id from processing_root_to_leaf p, processing_files pf "
        + "where p.parent_id = processing_id "
        + "UNION ALL "
        + "select distinct file_id from processing_root_to_leaf p, processing_files pf "
        + "where p.child_id = processing_id "
        + "UNION ALL "
        + "select distinct file_id from processing_files pf inner join processing_ius pr_i "
        + "on (pr_i.processing_id = pf.processing_id) "
        + "inner join ius i on (i.ius_id = pr_i.ius_id) "
        + "inner join sample s on (s.sample_id = i.sample_id) "
        + "inner join experiment ex on (ex.experiment_id = s.experiment_id) "
        + "where ex.study_id = ? "
        + "UNION "
        + "select distinct file_id from processing_files pf inner join processing_studies ps "
        + "on (ps.processing_id = pf.processing_id) "
        + "where ps.study_id = ? "
        + "UNION "
        + "select distinct file_id from processing_files pf inner join processing_experiments p_ex "
        + "on (p_ex.processing_id = pf.processing_id) "
        + "inner join experiment ex on (p_ex.experiment_id = ex.experiment_id) "
        + "where ex.study_id = ? "
        + "UNION "
        + "select distinct file_id from processing_files pf inner join processing_samples p_sam "
        + "on (p_sam.processing_id = pf.processing_id) "
        + "inner join sample sam on (p_sam.sample_id = sam.sample_id) "
        + "inner join experiment ex on (ex.experiment_id = sam.experiment_id) "
        + "where ex.study_id = ? "
        + // nested samples ius, first files
        "UNION ( "
        + "WITH RECURSIVE sample_root_to_leaf (child_id, parent_id) AS ( "
        + "SELECT s_rec.child_id as child_id, s_rec.parent_id FROM sample_relationship s_rec "
        + "INNER JOIN sample sam on (s_rec.parent_id = sam.sample_id) "
        + "INNER JOIN experiment ex on (sam.experiment_id = ex.experiment_id) "
        + "where ex.study_id = ? "
        + "UNION ALL "
        + "SELECT s_rec.child_id, s_rl.parent_id "
        + "FROM sample_root_to_leaf s_rl, sample_relationship s_rec "
        + "WHERE s_rec.parent_id = s_rl.child_id ) "
        + "select distinct pf.file_id from sample_root_to_leaf s_rec, ius i, processing_ius pr_i, processing_files pf "
        + "where (s_rec.parent_id = i.sample_id or s_rec.child_id = i.sample_id) "
        + "and pr_i.ius_id = i.ius_id and pf.processing_id = pr_i.processing_id ) "
        + // nested samples processing_samples, first files
        "UNION ( " + "WITH RECURSIVE sample_root_to_leaf (child_id, parent_id) AS ( "
        + "SELECT s_rec.child_id as child_id, s_rec.parent_id FROM sample_relationship s_rec "
        + "INNER JOIN sample sam on (s_rec.parent_id = sam.sample_id) "
        + "INNER JOIN experiment ex on (sam.experiment_id = ex.experiment_id) " + "where ex.study_id = ? "
        + "UNION ALL " + "SELECT s_rec.child_id, s_rl.parent_id "
        + "FROM sample_root_to_leaf s_rl, sample_relationship s_rec " + "WHERE s_rec.parent_id = s_rl.child_id ) "
        + "select distinct pf.file_id from sample_root_to_leaf s_rec, processing_samples p_sam, processing_files pf "
        + "where (s_rec.parent_id = p_sam.sample_id or s_rec.child_id = p_sam.sample_id) "
        + "and p_sam.processing_id = pf.processing_id ) )";

    List list = this.getSession().createSQLQuery(query).addEntity(File.class).setInteger(0, studyId)
        .setInteger(1, studyId).setInteger(2, studyId).setInteger(3, studyId).setInteger(4, studyId)
        .setInteger(5, studyId).setString(6, metaType).setInteger(7, studyId).setInteger(8, studyId)
        .setInteger(9, studyId).setInteger(10, studyId).setInteger(11, studyId).setInteger(12, studyId).list();

    for (Object file : list) {
      File fl = (File) file;
      files.add(fl);
    }
    return files;
  }

  /** {@inheritDoc} */
  public boolean isHasFile(Integer studyId, String metaType) {
    boolean isHasFile = false;

    String query = "WITH RECURSIVE processing_root_to_leaf (child_id, parent_id) AS ( "
        + "SELECT p.child_id as child_id, p.parent_id "
        + "FROM processing_relationship p inner join processing_ius pr_i on (pr_i.processing_id = p.parent_id) "
        + "inner join ius i on (i.ius_id = pr_i.ius_id) "
        + "inner join sample s on (s.sample_id = i.sample_id) "
        + "inner join experiment ex on (ex.experiment_id = s.experiment_id) "
        + "where ex.study_id = ? "
        + "UNION "
        + "SELECT p.child_id as child_id, p.parent_id "
        + "FROM processing_relationship p inner join processing_studies ps on (ps.processing_id = p.parent_id) "
        + "where ps.study_id = ? "
        + "UNION "
        + "SELECT p.child_id as child_id, p.parent_id "
        + "FROM processing_relationship p inner join processing_experiments p_ex on (p_ex.processing_id = p.parent_id) "
        + "inner join experiment ex on (p_ex.experiment_id = ex.experiment_id) "
        + "where ex.study_id = ? "
        + "UNION "
        + "SELECT p.child_id as child_id, p.parent_id "
        + "FROM processing_relationship p inner join processing_samples p_sam on (p_sam.processing_id = p.parent_id) "
        + "inner join sample sam on (p_sam.sample_id = sam.sample_id) "
        + "inner join experiment ex on (sam.experiment_id = ex.experiment_id) "
        + "where ex.study_id = ? "
        + // --------------------------------------------------------------------------------
        // nested samples ius rec
        "UNION( "
        + "WITH RECURSIVE sample_root_to_leaf (child_id, parent_id) AS ( "
        + "SELECT s_rec.child_id as child_id, s_rec.parent_id FROM sample_relationship s_rec "
        + "INNER JOIN sample sam on (s_rec.parent_id = sam.sample_id) "
        + "INNER JOIN experiment ex on (sam.experiment_id = ex.experiment_id) "
        + "where ex.study_id = ? "
        + "UNION ALL "
        + "SELECT s_rec.child_id, s_rl.parent_id "
        + "FROM sample_root_to_leaf s_rl, sample_relationship s_rec "
        + "WHERE s_rec.parent_id = s_rl.child_id ) "
        + "select p.child_id as child_id, p.parent_id from sample_root_to_leaf s_rec, ius i_rec, processing_ius pr_i, processing_relationship p "
        + "where (s_rec.parent_id = i_rec.sample_id or s_rec.child_id = i_rec.sample_id) "
        + "and pr_i.ius_id = i_rec.ius_id and p.parent_id = pr_i.processing_id ) "
        + // nested samples processing_samples rec
        "UNION( "
        + "WITH RECURSIVE sample_root_to_leaf (child_id, parent_id) AS ( "
        + "SELECT s_rec.child_id as child_id, s_rec.parent_id FROM sample_relationship s_rec "
        + "INNER JOIN sample sam on (s_rec.parent_id = sam.sample_id) "
        + "INNER JOIN experiment ex on (sam.experiment_id = ex.experiment_id) "
        + "where ex.study_id = ? "
        + "UNION ALL "
        + "SELECT s_rec.child_id, s_rl.parent_id "
        + "FROM sample_root_to_leaf s_rl, sample_relationship s_rec "
        + "WHERE s_rec.parent_id = s_rl.child_id ) "
        + "select p.child_id as child_id, p.parent_id from sample_root_to_leaf s_rec, processing_samples p_sam, processing_relationship p "
        + "where (s_rec.parent_id = p_sam.sample_id or s_rec.child_id = p_sam.sample_id) "
        + "and p.parent_id = p_sam.processing_id ) "
        + // --------------------------------------------------------------------------------
        "UNION ALL "
        + "SELECT p.child_id, rl.parent_id "
        + "FROM processing_root_to_leaf rl, processing_relationship p "
        + "WHERE p.parent_id = rl.child_id) "
        + "select * from File myfile where myfile.meta_type=? and myfile.file_id in( "
        + "select distinct file_id from processing_root_to_leaf p, processing_files pf "
        + "where p.parent_id = processing_id "
        + "UNION ALL "
        + "select distinct file_id from processing_root_to_leaf p, processing_files pf "
        + "where p.child_id = processing_id "
        + "UNION ALL "
        + "select distinct file_id from processing_files pf inner join processing_ius pr_i "
        + "on (pr_i.processing_id = pf.processing_id) "
        + "inner join ius i on (i.ius_id = pr_i.ius_id) "
        + "inner join sample s on (s.sample_id = i.sample_id) "
        + "inner join experiment ex on (ex.experiment_id = s.experiment_id) "
        + "where ex.study_id = ? "
        + "UNION "
        + "select distinct file_id from processing_files pf inner join processing_studies ps "
        + "on (ps.processing_id = pf.processing_id) "
        + "where ps.study_id = ? "
        + "UNION "
        + "select distinct file_id from processing_files pf inner join processing_experiments p_ex "
        + "on (p_ex.processing_id = pf.processing_id) "
        + "inner join experiment ex on (p_ex.experiment_id = ex.experiment_id) "
        + "where ex.study_id = ? "
        + "UNION "
        + "select distinct file_id from processing_files pf inner join processing_samples p_sam "
        + "on (p_sam.processing_id = pf.processing_id) "
        + "inner join sample sam on (p_sam.sample_id = sam.sample_id) "
        + "inner join experiment ex on (ex.experiment_id = sam.experiment_id) "
        + "where ex.study_id = ? "
        + // nested samples ius, first files
        "UNION ( "
        + "WITH RECURSIVE sample_root_to_leaf (child_id, parent_id) AS ( "
        + "SELECT s_rec.child_id as child_id, s_rec.parent_id FROM sample_relationship s_rec "
        + "INNER JOIN sample sam on (s_rec.parent_id = sam.sample_id) "
        + "INNER JOIN experiment ex on (sam.experiment_id = ex.experiment_id) "
        + "where ex.study_id = ? "
        + "UNION ALL "
        + "SELECT s_rec.child_id, s_rl.parent_id "
        + "FROM sample_root_to_leaf s_rl, sample_relationship s_rec "
        + "WHERE s_rec.parent_id = s_rl.child_id ) "
        + "select distinct pf.file_id from sample_root_to_leaf s_rec, ius i, processing_ius pr_i, processing_files pf "
        + "where (s_rec.parent_id = i.sample_id or s_rec.child_id = i.sample_id) "
        + "and pr_i.ius_id = i.ius_id and pf.processing_id = pr_i.processing_id ) "
        + // nested samples processing_samples, first files
        "UNION ( " + "WITH RECURSIVE sample_root_to_leaf (child_id, parent_id) AS ( "
        + "SELECT s_rec.child_id as child_id, s_rec.parent_id FROM sample_relationship s_rec "
        + "INNER JOIN sample sam on (s_rec.parent_id = sam.sample_id) "
        + "INNER JOIN experiment ex on (sam.experiment_id = ex.experiment_id) " + "where ex.study_id = ? "
        + "UNION ALL " + "SELECT s_rec.child_id, s_rl.parent_id "
        + "FROM sample_root_to_leaf s_rl, sample_relationship s_rec " + "WHERE s_rec.parent_id = s_rl.child_id ) "
        + "select distinct pf.file_id from sample_root_to_leaf s_rec, processing_samples p_sam, processing_files pf "
        + "where (s_rec.parent_id = p_sam.sample_id or s_rec.child_id = p_sam.sample_id) "
        + "and p_sam.processing_id = pf.processing_id ) ) LIMIT 1";

    List list = this.getSession().createSQLQuery(query).addEntity(File.class).setInteger(0, studyId)
        .setInteger(1, studyId).setInteger(2, studyId).setInteger(3, studyId).setInteger(4, studyId)
        .setInteger(5, studyId).setString(6, metaType).setInteger(7, studyId).setInteger(8, studyId)
        .setInteger(9, studyId).setInteger(10, studyId).setInteger(11, studyId).setInteger(12, studyId).list();

    isHasFile = (list.size() > 0) ? true : false;
    return isHasFile;
  }

  /** {@inheritDoc} */
  public List<Study> listStudyHasFile(Registration registration, String metaType, Boolean isAsc) {
    List<Study> studies = new ArrayList<Study>();
    /*
     * String query = "WITH RECURSIVE processing_root_to_leaf (child_id,
     * parent_id, study_id) AS ( " + "SELECT p.child_id as child_id,
     * p.parent_id, st.study_id " + "FROM processing_relationship p inner join
     * processing_ius pr_i on (pr_i.processing_id = p.parent_id) " +
     * "inner join ius i on (i.ius_id = pr_i.ius_id) " + "inner join sample s on
     * (s.sample_id = i.sample_id) " + "inner join experiment ex on
     * (ex.experiment_id = s.experiment_id) " + "inner join study st on
     * (st.study_id = ex.study_id) where st.owner_id=? " + "UNION ALL " +
     * "SELECT p.child_id, rl.parent_id, rl.study_id " + "FROM
     * processing_root_to_leaf rl, processing_relationship p " + "WHERE
     * p.parent_id = rl.child_id) " + "select distinct study_id from ( " +
     * "select distinct prl.study_id from processing_root_to_leaf prl,
     * processing_files pf, file f " + "where prl.parent_id = pf.processing_id
     * and f.file_id=pf.file_id and f.meta_type=? " + "UNION ALL " + "select
     * distinct prl.study_id from processing_root_to_leaf prl, processing_files
     * pf, file f " + "where prl.child_id = pf.processing_id and
     * f.file_id=pf.file_id and f.meta_type=?) q" ;
     * 
     * String query = "WITH RECURSIVE processing_root_to_leaf (child_id,
     * parent_id, study_id) AS ( " + "SELECT p.child_id as child_id,
     * p.parent_id, st.study_id " + "FROM processing_relationship p inner join
     * processing_ius pr_i on (pr_i.processing_id = p.parent_id) " +
     * "inner join ius i on (i.ius_id = pr_i.ius_id) " + "inner join sample s on
     * (s.sample_id = i.sample_id) " + "inner join experiment ex on
     * (ex.experiment_id = s.experiment_id) " + "inner join study st on
     * (st.study_id = ex.study_id) " + ownerSubQuery + //where st.owner_id=?
     * " + "UNION ALL " + "SELECT p.child_id, rl.parent_id, rl.study_id " +
     * "FROM processing_root_to_leaf rl, processing_relationship p " +
     * "WHERE p.parent_id = rl.child_id) " + "select * from study where study_id
     * in ( " + "select distinct study_id from ( " + "select distinct
     * prl.study_id from processing_root_to_leaf prl, processing_files pf, file
     * f " + "where prl.parent_id = pf.processing_id and f.file_id=pf.file_id
     * and f.meta_type=? " + "UNION ALL " + "select distinct prl.study_id from
     * processing_root_to_leaf prl, processing_files pf, file f " + "where
     * prl.child_id = pf.processing_id and f.file_id=pf.file_id and
     * f.meta_type=? " + "UNION ALL " + "select distinct st.study_id from
     * processing_files pf inner join processing_ius pr_i " + "on
     * (pr_i.processing_id = pf.processing_id) " + "inner join ius i on
     * (i.ius_id = pr_i.ius_id) " + "inner join sample s on (s.sample_id =
     * i.sample_id) " + "inner join experiment ex on (ex.experiment_id =
     * s.experiment_id) " + "inner join study st on (st.study_id = ex.study_id)
     * inner join file f on (pf.file_id=f.file_id) " + "where f.meta_type=? )
     * q" + " ) order by title " + sortValue;
     */
    String ownerSubQuery = "";
    if (!registration.isLIMSAdmin()) {
      ownerSubQuery = " where st.owner_id=? ";
    }

    String sortValue = (isAsc) ? "asc" : "desc";

    String query = "WITH RECURSIVE processing_root_to_leaf (child_id, parent_id, study_id) AS ( "
        + "SELECT p.child_id as child_id, p.parent_id, st.study_id "
        + "FROM processing_relationship p inner join processing_ius pr_i on (pr_i.processing_id = p.parent_id) "
        + "inner join ius i on (i.ius_id = pr_i.ius_id) " + "inner join sample s on (s.sample_id = i.sample_id) "
        + "inner join experiment ex on (ex.experiment_id = s.experiment_id) "
        + "inner join study st on (st.study_id = ex.study_id) "
        + ownerSubQuery
        + // where st.owner_id=? " +
        "UNION "
        + "SELECT p.child_id as child_id, p.parent_id, st.study_id "
        + "FROM processing_relationship p inner join processing_lanes pr_l on (pr_l.processing_id = p.parent_id) "
        + "inner join lane l on (l.lane_id = pr_l.lane_id) "
        + "inner join ius i on (i.lane_id = l.lane_id) "
        + "inner join sample s on (s.sample_id = i.sample_id) "
        + "inner join experiment ex on (ex.experiment_id = s.experiment_id) "
        + "inner join study st on (st.study_id = ex.study_id) "
        + ownerSubQuery
        + // where st.owner_id=? " +
        "UNION "
        + "SELECT p.child_id as child_id, p.parent_id, st.study_id "
        + "FROM processing_relationship p inner join processing_studies ps on (ps.processing_id = p.parent_id) "
        + "inner join study st on (st.study_id = ps.study_id) "
        + ownerSubQuery
        + // where st.owner_id=? " +
        "UNION "
        + "SELECT p.child_id as child_id, p.parent_id, st.study_id "
        + "FROM processing_relationship p inner join processing_experiments p_ex on (p_ex.processing_id = p.parent_id) "
        + "inner join experiment ex on (ex.experiment_id = p_ex.experiment_id) "
        + "inner join study st on (st.study_id = ex.study_id) "
        + ownerSubQuery
        + // where st.owner_id=? " +
        "UNION "
        + "SELECT p.child_id as child_id, p.parent_id, st.study_id "
        + "FROM processing_relationship p inner join processing_samples p_sam on (p_sam.processing_id = p.parent_id) "
        + "inner join sample sam on (sam.sample_id = p_sam.sample_id) "
        + "inner join experiment ex on (ex.experiment_id = sam.experiment_id) "
        + "inner join study st on (st.study_id = ex.study_id) "
        + ownerSubQuery
        + // where st.owner_id=? " +
        // --------------------------------------------------------------------------------
        // nested samples ius rec
        "UNION( "
        + "WITH RECURSIVE sample_root_to_leaf (child_id, parent_id, study_id) AS ( "
        + "SELECT s_rec.child_id as child_id, s_rec.parent_id, st.study_id FROM sample_relationship s_rec "
        + "INNER JOIN sample sam on (s_rec.parent_id = sam.sample_id)  "
        + "INNER JOIN experiment ex on (sam.experiment_id = ex.experiment_id) "
        + "inner join study st on (st.study_id = ex.study_id) "
        + ownerSubQuery
        + "UNION ALL "
        + "SELECT s_rec.child_id, s_rl.parent_id, s_rl.study_id "
        + "FROM sample_root_to_leaf s_rl, sample_relationship s_rec "
        + "WHERE s_rec.parent_id = s_rl.child_id ) "
        + "select p.child_id as child_id, p.parent_id, s_rec.study_id from sample_root_to_leaf s_rec, ius i_rec, processing_ius pr_i, processing_relationship p "
        + "where (s_rec.parent_id = i_rec.sample_id or s_rec.child_id = i_rec.sample_id) "
        + "and pr_i.ius_id = i_rec.ius_id and p.parent_id = pr_i.processing_id ) "
        + // nested samples processing_samples rec
        "UNION( "
        + "WITH RECURSIVE sample_root_to_leaf (child_id, parent_id, study_id) AS ( "
        + "SELECT s_rec.child_id as child_id, s_rec.parent_id, st.study_id FROM sample_relationship s_rec "
        + "INNER JOIN sample sam on (s_rec.parent_id = sam.sample_id)  "
        + "INNER JOIN experiment ex on (sam.experiment_id = ex.experiment_id) "
        + "inner join study st on (st.study_id = ex.study_id) "
        + ownerSubQuery
        + "UNION ALL "
        + "SELECT s_rec.child_id, s_rl.parent_id, s_rl.study_id "
        + "FROM sample_root_to_leaf s_rl, sample_relationship s_rec "
        + "WHERE s_rec.parent_id = s_rl.child_id ) "
        + "select p.child_id as child_id, p.parent_id, s_rec.study_id from sample_root_to_leaf s_rec, processing_samples p_sam, processing_relationship p "
        + "where (s_rec.parent_id = p_sam.sample_id or s_rec.child_id = p_sam.sample_id) "
        + "and p.parent_id = p_sam.processing_id ) "
        + // --------------------------------------------------------------------------------
        "UNION ALL "
        + "SELECT p.child_id, rl.parent_id, rl.study_id "
        + "FROM processing_root_to_leaf rl, processing_relationship p "
        + "WHERE p.parent_id = rl.child_id) "
        + "select * from study where study_id in ( "
        + "select distinct study_id from ( "
        + "select distinct prl.study_id from processing_root_to_leaf prl, processing_files pf, file f "
        + "where prl.parent_id = pf.processing_id and f.file_id=pf.file_id and f.meta_type=? "
        + "UNION ALL "
        + "select distinct prl.study_id from processing_root_to_leaf prl, processing_files pf, file f "
        + "where prl.child_id = pf.processing_id and f.file_id=pf.file_id and f.meta_type=? "
        + "UNION ALL "
        + "select distinct st.study_id from  processing_files pf inner join processing_ius pr_i "
        + "on (pr_i.processing_id = pf.processing_id) "
        + "inner join ius i on (i.ius_id = pr_i.ius_id) "
        + "inner join sample s on (s.sample_id = i.sample_id) "
        + "inner join experiment ex on (ex.experiment_id = s.experiment_id) "
        + "inner join study st on (st.study_id = ex.study_id)  inner join file f on (pf.file_id=f.file_id) "
        + "where f.meta_type=? "
        + "UNION "
        + "select distinct st.study_id from  processing_files pf inner join processing_studies ps "
        + "on (ps.processing_id = pf.processing_id) "
        + "inner join study st on (st.study_id = ps.study_id) "
        + "inner join file f on (pf.file_id=f.file_id) "
        + "where f.meta_type=? "
        + "UNION "
        + "select distinct st.study_id from processing_files pf inner join processing_experiments p_ex "
        + "on (p_ex.processing_id = pf.processing_id) "
        + "inner join experiment ex on (ex.experiment_id = p_ex.experiment_id) "
        + "inner join study st on (st.study_id = ex.study_id) "
        + "inner join file f on (pf.file_id=f.file_id) "
        + "where f.meta_type=? "
        + "UNION "
        + "select distinct st.study_id from processing_files pf inner join processing_samples p_sam "
        + "on (p_sam.processing_id = pf.processing_id) "
        + "inner join sample sam on (sam.sample_id = p_sam.sample_id) "
        + "inner join experiment ex on (ex.experiment_id = sam.experiment_id) "
        + "inner join study st on (st.study_id = ex.study_id) "
        + "inner join file f on (pf.file_id=f.file_id) "
        + "where f.meta_type=? "
        + // nested samples ius, first files
        "UNION ( "
        + "WITH RECURSIVE sample_root_to_leaf (child_id, parent_id, study_id) AS ( "
        + "SELECT s_rec.child_id as child_id, s_rec.parent_id, st.study_id FROM sample_relationship s_rec "
        + "INNER JOIN sample sam on (s_rec.parent_id = sam.sample_id)  "
        + "INNER JOIN experiment ex on (sam.experiment_id = ex.experiment_id) "
        + "inner join study st on (st.study_id = ex.study_id) "
        + ownerSubQuery
        + "UNION ALL "
        + "SELECT s_rec.child_id, s_rl.parent_id, s_rl.study_id "
        + "FROM sample_root_to_leaf s_rl, sample_relationship s_rec "
        + "WHERE s_rec.parent_id = s_rl.child_id ) "
        + "select distinct s_rec.study_id from sample_root_to_leaf s_rec, processing_samples p_sam, processing_files pf, file f "
        + "where (s_rec.parent_id = p_sam.sample_id or s_rec.child_id = p_sam.sample_id) "
        + "and p_sam.processing_id = pf.processing_id and f.file_id=pf.file_id and f.meta_type=? ) "
        + // nested samples processing_samples, first files
        "UNION ( "
        + "WITH RECURSIVE sample_root_to_leaf (child_id, parent_id, study_id) AS ( "
        + "SELECT s_rec.child_id as child_id, s_rec.parent_id, st.study_id FROM sample_relationship s_rec "
        + "INNER JOIN sample sam on (s_rec.parent_id = sam.sample_id)  "
        + "INNER JOIN experiment ex on (sam.experiment_id = ex.experiment_id) "
        + "inner join study st on (st.study_id = ex.study_id) "
        + ownerSubQuery
        + "UNION ALL "
        + "SELECT s_rec.child_id, s_rl.parent_id, s_rl.study_id "
        + "FROM sample_root_to_leaf s_rl, sample_relationship s_rec "
        + "WHERE s_rec.parent_id = s_rl.child_id ) "
        + "select distinct s_rec.study_id from sample_root_to_leaf s_rec, ius i, processing_ius pr_i, processing_files pf, file f "
        + "where (s_rec.parent_id = i.sample_id or s_rec.child_id = i.sample_id) "
        + "and pr_i.ius_id = i.ius_id and pf.processing_id = pr_i.processing_id and f.file_id=pf.file_id and f.meta_type=? )"
        + ") q " + ") order by title " + sortValue;

    List list = null;

    if (registration.isLIMSAdmin()) {
      list = this.getSession().createSQLQuery(query).addEntity(Study.class).setString(0, metaType)
          .setString(1, metaType).setString(2, metaType).setString(3, metaType).setString(4, metaType)
          .setString(5, metaType).setString(6, metaType).setString(7, metaType).list();
    } else {
      list = this.getSession().createSQLQuery(query).addEntity(Study.class)
          .setInteger(0, registration.getRegistrationId()).setInteger(1, registration.getRegistrationId())
          .setInteger(2, registration.getRegistrationId()).setInteger(3, registration.getRegistrationId())
          .setInteger(4, registration.getRegistrationId()).setInteger(5, registration.getRegistrationId())
          .setInteger(6, registration.getRegistrationId()).setString(7, metaType).setString(8, metaType)
          .setString(9, metaType).setString(10, metaType).setString(11, metaType).setString(12, metaType)
          .setInteger(13, registration.getRegistrationId()).setString(14, metaType)
          .setInteger(15, registration.getRegistrationId()).setString(16, metaType).list();
    }

    for (Object st : list) {
      Study study = (Study) st;
      try {
        if (registration.equals(study.getOwner()) || registration.isLIMSAdmin()) {
          study.setIsHasFile(true);
          studies.add(study);
        }
      } catch (ObjectNotFoundException e) {
        // Registration is deleted, but studies left.
        logger.warn("Registration with #" + registration.getRegistrationId() + " not found");
      }
    }

    return studies;
  }

  /**
   * {@inheritDoc}
   *
   * Finds an instance of Study in the database by the Study name.
   */
  public Study findByTitle(String title) {
    String query = "from Study as study where lower(study.title) = ?";
    Study study = null;
    Object[] parameters = { title.toLowerCase() };
    List list = this.getHibernateTemplate().find(query, parameters);
    if (list.size() > 0) {
      study = (Study) list.get(0);
      logger.debug("In DAO. Study title = " + study.getTitle());
    }
    return study;
  }

  /**
   * {@inheritDoc}
   *
   * Finds an instance of Study in the database by the Study ID.
   */
  public Study findByID(Integer expID) {
    String query = "from Study as study where study.studyId = ?";
    Study study = null;
    Object[] parameters = { expID };
    List list = this.getHibernateTemplate().find(query, parameters);
    if (list.size() > 0) {
      study = (Study) list.get(0);
    }
    return study;
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  @Override
  public Study findBySWAccession(Integer swAccession) {
    String query = "from Study as study where study.swAccession = ?";
    Study study = null;
    Object[] parameters = { swAccession };
    List<Study> list = this.getHibernateTemplate().find(query, parameters);
    if (list.size() > 0) {
      study = (Study) list.get(0);
    }
    return study;
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  @Override
  public List<Study> findByOwnerID(Integer registrationId) {
    String query = "from Study as study where study.owner.registrationId = ?";
    Object[] parameters = { registrationId };
    return this.getHibernateTemplate().find(query, parameters);
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  @Override
  public List<Study> findByCriteria(String criteria, boolean isCaseSens) {
    String queryStringCase = "from Study as study where study.title like :title "
        + " or study.description like :description "
        + " or cast(study.swAccession as string) like :sw order by study.title, study.description";
    String queryStringICase = "from Study as study where lower(study.title) like :title "
        + " or lower(study.description) like :description "
        + " or cast(study.swAccession as string) like :sw order by study.title, study.description";
    Query query = isCaseSens ? this.getSession().createQuery(queryStringCase) : this.getSession().createQuery(
        queryStringICase);
    if (!isCaseSens) {
      criteria = criteria.toLowerCase();
    }
    criteria = "%" + criteria + "%";
    query.setString("title", criteria);
    query.setString("description", criteria);
    query.setString("sw", criteria);

    return query.list();
  }
  
    /** {@inheritDoc} */
    @Override
    public List<Study> findByCriteria(String criteria) {

        String queryStringCase = "from Study as s where ";
        Query query = this.getSession().createQuery(queryStringCase+" "+criteria);
        
        return query.list();
    }

  // @Override
  // public List search() {
  // Session session = this.getSession();
  // FullTextSession fullTextSession = Search.getFullTextSession(session);
  // Transaction tx = fullTextSession.beginTransaction();
  // // create native Lucene query unsing the query DSL
  // // alternatively you can write the Lucene query using the Lucene query
  // // parser
  // // or the Lucene programmatic API. The Hibernate Search DSL is recommended
  // // though
  // QueryBuilder qb =
  // fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(Study.class).get();
  // org.apache.lucene.search.Query query =
  // qb.keyword().onFields("description").matching("genome").createQuery();
  // // wrap Lucene query in a org.hibernate.Query
  // org.hibernate.Query hibQuery = fullTextSession.createFullTextQuery(query,
  // Study.class);
  // // execute search
  // List result = hibQuery.list();
  // tx.commit();
  // session.close();
  // return result;
  // }
  /** {@inheritDoc} */
  @Override
  public Study updateDetached(Study study) {
    Study dbObject = reattachStudy(study);
    try {
      BeanUtilsBean beanUtils = new NullBeanUtils();
      beanUtils.copyProperties(dbObject, study);
      return (Study) this.getHibernateTemplate().merge(dbObject);
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
    Study study = findBySWAccession(swAccession);
    List<ReturnValue> list = fatf.filesFromStudy(study);
    getSessionFactory().getCurrentSession().flush();
    getSessionFactory().getCurrentSession().clear();
    return list;
  }

  /** {@inheritDoc} */
  @Override
  public void updateOwners(Integer swAccession) {
    Study study = findBySWAccession(swAccession);
    PropagateOwnership po = new PropagateOwnership();
    po.filesFromStudy(study);
  }

  /** {@inheritDoc} */
  @Override
  public void update(Registration registration, Study study) {
    Study dbObject = reattachStudy(study);
    Logger logger = Logger.getLogger(StudyDAOHibernate.class);
    if (registration == null) {
      logger.error("StudyDAOHibernate update registration is null");
    } else if (registration.isLIMSAdmin()
        || (study.givesPermission(registration) && dbObject.givesPermission(registration))) {
      logger.info("Updating study object");
      update(study);
      getSession().flush();
    } else {
      logger.error("StudyDAOHibernate update Not authorized");
    }
  }

  /** {@inheritDoc} */
  @Override
  public Integer insert(Registration registration, Study study) {
    Integer swAccession = 0;
    Logger logger = Logger.getLogger(StudyDAOHibernate.class);
    if (registration == null) {
      logger.error("StudyDAOHibernate insert registration is null");
    } else if (registration.isLIMSAdmin() || study.givesPermission(registration)) {
      logger.info("insert study object");
      insert(study);
      this.getSession().flush();
      swAccession = study.getSwAccession();
    } else {
      logger.error("StudyDAOHibernate insert Not authorized");
    }
    return (swAccession);
  }

  /** {@inheritDoc} */
  @Override
  public Study updateDetached(Registration registration, Study study) {
    Study dbObject = reattachStudy(study);
    Logger logger = Logger.getLogger(StudyDAOHibernate.class);
    if (registration == null) {
      logger.error("StudyDAOHibernate updateDetached registration is null");
    } else if (registration.isLIMSAdmin() || dbObject.givesPermission(registration)) {
      logger.info("updateDetached study object");
      return updateDetached(study);
    } else {
      logger.error("StudyDAOHibernate updateDetached Not authorized");
    }

    return null;
  }

  private Study reattachStudy(Study study) throws IllegalStateException, DataAccessResourceFailureException {
    Study dbObject = study;
    if (!getSession().contains(study)) {
      dbObject = findByID(study.getStudyId());
    }
    return dbObject;
  }

  /** {@inheritDoc} */
  @Override
  public int getStatusCount(Study study, WorkflowRun.Status status) {
    String query = "WITH RECURSIVE all_the_runs(workflow_run_id) AS ("
        + " WITH RECURSIVE root_to_leaf(root_sample, child_id) AS (" + " SELECT sample_id, sample_id FROM sample s"
        + " JOIN experiment e ON (s.experiment_id = e.experiment_id)" + " JOIN study st ON (st.study_id = e.study_id)"
        + " WHERE st.study_id = :study" + " UNION" + " SELECT rl.root_sample, sr.child_id FROM sample_relationship sr"
        + " JOIN root_to_leaf rl ON (sr.parent_id = rl.child_id) )"

        + " SELECT distinct p.workflow_run_id from processing_samples ps"
        + " JOIN root_to_leaf rtl ON (ps.sample_id = rtl.child_id)"
        + " JOIN processing p ON (p.processing_id = rtl.child_id)"

        + " UNION"

        + " SELECT distinct iwr.workflow_run_id from ius i" + " JOIN root_to_leaf rtl ON (i.sample_id = rtl.child_id)"
        + " JOIN ius_workflow_runs iwr ON (iwr.ius_id = i.ius_id)" + ")"

        + " select count(*) from all_the_runs"
        + " JOIN workflow_run wr ON (wr.workflow_run_id = all_the_runs.workflow_run_id)" + " WHERE wr.status = :status";

    @SuppressWarnings("rawtypes")
    List list = this.getSession().createSQLQuery(query).setInteger("study", study.getStudyId())
        .setString("status", status.name()).list();

    return ((BigInteger) list.get(0)).intValue();
  }
}
