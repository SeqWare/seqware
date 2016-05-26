package net.sourceforge.seqware.common.dao.hibernate;

import net.sourceforge.seqware.common.dao.ExperimentDAO;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.util.NullBeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * ExperimentDAOHibernate class.
 * </p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ExperimentDAOHibernate extends HibernateDaoSupport implements ExperimentDAO {

    final Logger localLogger = LoggerFactory.getLogger(ExperimentDAOHibernate.class);

    /**
     * <p>
     * Constructor for ExperimentDAOHibernate.
     * </p>
     */
    public ExperimentDAOHibernate() {
        super();
    }

    /**
     * {@inheritDoc}
     *
     * @param experiment
     */
    @Override
    public Integer insert(Experiment experiment) {
        this.getHibernateTemplate().save(experiment);
        this.currentSession().flush();
        return experiment.getSwAccession();
    }

    /**
     * {@inheritDoc}
     *
     * @param experiment
     */
    @Override
    public void update(Experiment experiment) {
        this.getHibernateTemplate().update(experiment);
    }

    /** {@inheritDoc} */
    @Override
    public void merge(Experiment experiment) {
        this.getHibernateTemplate().merge(experiment);
    }

    /**
     * {@inheritDoc}
     *
     * This deletion will result in just the experiment being deleted but the samples and IUS will remain. This will potentially cause
     * orphans which is not really at all good. A better solution is to never delete but just use a deletion attribute.
     *
     * @param experiment
     */
    @Override
    public void delete(Experiment experiment) {
        // remove partent study
        experiment.getStudy().getExperiments().remove(experiment);
        // clear samples
        for (Sample s : experiment.getSamples()) {
            s.setExperiment(null);
            // this.getHibernateTemplate().update(s);
        }
        experiment.setSamples(null);
        // don't delete processing
        for (Processing p : experiment.getProcessings()) {
            p.getExperiments().remove(experiment);
        }
        experiment.setProcessings(null);
        // flush the change
        this.getHibernateTemplate().update(experiment);
        this.getHibernateTemplate().flush();
        // and delete the study
        this.getHibernateTemplate().delete(experiment);
    }

    /** {@inheritDoc} */
    @Override
    public List<Experiment> list(Registration registration) {
        ArrayList<Experiment> experiments = new ArrayList<>();
        if (registration == null) {
            return experiments;
        }

        List expmts;
        if (registration.isLIMSAdmin()) {
            // The user can see all experiments
            expmts = this.getHibernateTemplate().find("from Experiment as experiment order by experiment.name desc");
        } else {
            // Limit the experiments to those owned by the user
            expmts = this.getHibernateTemplate().find("from Experiment as experiment where owner = ? order by experiment.name desc",
                    registration);
        }

        // expmts =
        // this.getHibernateTemplate().find("from Experiment as experiment order by experiment.name desc");
        for (Object experiment : expmts) {
            experiments.add((Experiment) experiment);
        }
        return experiments;
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
        ArrayList<Experiment> experiments = new ArrayList<>();
        if (study == null) {
            return experiments;
        }

        List expmts;
        // Limit the experiments to those owned by the user
        expmts = this.getHibernateTemplate().find("from Experiment as experiment where study = ? order by experiment.name desc", study);

        // expmts =
        // this.getHibernateTemplate().find("from Experiment as experiment order by experiment.name desc");
        for (Object experiment : expmts) {
            experiments.add((Experiment) experiment);
        }
        return experiments;
    }

    /** {@inheritDoc} */
    @Override
    public List<File> getFiles(Integer experimentId) {
        List<File> files = new ArrayList<>();
        String query = "WITH RECURSIVE processing_root_to_leaf (child_id, parent_id) AS ( "
                + "SELECT p.child_id as child_id, p.parent_id "
                + "FROM processing_relationship p inner join processing_ius pr_i on (pr_i.processing_id = p.parent_id) "
                + "inner join ius i on (i.ius_id = pr_i.ius_id) "
                + "inner join sample sam on (sam.sample_id = i.sample_id) "
                + "where sam.experiment_id = ? "
                + "UNION "
                + "SELECT p.child_id as child_id, p.parent_id "
                + "FROM processing_relationship p inner join processing_experiments p_ex on (p_ex.processing_id = p.parent_id) "
                + "where p_ex.experiment_id = ? "
                + "UNION "
                + "SELECT p.child_id as child_id, p.parent_id "
                + "FROM processing_relationship p inner join processing_samples p_sam on (p_sam.processing_id = p.parent_id) "
                + "inner join sample sam on (p_sam.sample_id = sam.sample_id) "
                + "where sam.experiment_id = ? "
                + // nested samples ius rec
                "UNION( "
                + "WITH RECURSIVE sample_root_to_leaf (child_id, parent_id) AS ( "
                + "SELECT s_rec.child_id as child_id, s_rec.parent_id FROM sample_relationship s_rec "
                + "INNER JOIN sample sam on (s_rec.parent_id = sam.sample_id) "
                + "where sam.experiment_id = ? "
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
                + "where sam.experiment_id = ? "
                + "UNION ALL "
                + "SELECT s_rec.child_id, s_rl.parent_id "
                + "FROM sample_root_to_leaf s_rl, sample_relationship s_rec "
                + "WHERE s_rec.parent_id = s_rl.child_id ) "
                + "select p.child_id as child_id, p.parent_id from sample_root_to_leaf s_rec, processing_samples p_sam, processing_relationship p "
                + "where (s_rec.parent_id = p_sam.sample_id or s_rec.child_id = p_sam.sample_id) "
                + "and p.parent_id = p_sam.processing_id ) "
                + "UNION ALL "
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
                + "where s.experiment_id = ? "
                + "UNION "
                + "select distinct file_id from processing_files pf inner join processing_experiments p_ex "
                + "on (p_ex.processing_id = pf.processing_id) "
                + "where p_ex.experiment_id = ? "
                + "UNION "
                + "select distinct file_id from processing_files pf inner join processing_samples p_sam "
                + "on (p_sam.processing_id = pf.processing_id) "
                + "inner join sample sam on (p_sam.sample_id = sam.sample_id) "
                + "where sam.experiment_id = ? "
                + // nested samples ius, first files
                "UNION ( "
                + "WITH RECURSIVE sample_root_to_leaf (child_id, parent_id) AS ( "
                + "SELECT s_rec.child_id as child_id, s_rec.parent_id FROM sample_relationship s_rec "
                + "INNER JOIN sample sam on (s_rec.parent_id = sam.sample_id) "
                + "where sam.experiment_id = ? "
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
                + "INNER JOIN sample sam on (s_rec.parent_id = sam.sample_id) " + "where sam.experiment_id = ? " + "UNION ALL "
                + "SELECT s_rec.child_id, s_rl.parent_id " + "FROM sample_root_to_leaf s_rl, sample_relationship s_rec "
                + "WHERE s_rec.parent_id = s_rl.child_id ) "
                + "select distinct pf.file_id from sample_root_to_leaf s_rec, processing_samples p_sam, processing_files pf "
                + "where (s_rec.parent_id = p_sam.sample_id or s_rec.child_id = p_sam.sample_id) "
                + "and p_sam.processing_id = pf.processing_id ) )";

        List list = this.currentSession().createSQLQuery(query).addEntity(File.class).setInteger(0, experimentId).setInteger(1, experimentId)
                .setInteger(2, experimentId).setInteger(3, experimentId).setInteger(4, experimentId).setInteger(5, experimentId)
                .setInteger(6, experimentId).setInteger(7, experimentId).setInteger(8, experimentId).setInteger(9, experimentId).list();

        for (Object file : list) {
            File fl = (File) file;
            files.add(fl);
        }

        return files;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isHasFile(Integer experimentId) {
        boolean isHasFile;
        /*
         * String query = "WITH RECURSIVE processing_root_to_leaf (child_id, parent_id) AS ( " + "SELECT p.child_id as child_id, p.parent_id
         * " + "FROM processing_relationship p inner join processing_ius pr_i on (pr_i.processing_id = p.parent_id) " + "inner join ius i on
         * (i.ius_id = pr_i.ius_id) " + "inner join sample sam on (sam.sample_id = i.sample_id) " + "where sam.experiment_id = ? " + "UNION
         * " + "SELECT p.child_id as child_id, p.parent_id " + "FROM processing_relationship p inner join processing_experiments p_ex on
         * (p_ex.processing_id = p.parent_id) " + "where p_ex.experiment_id = ? " + "UNION " + "SELECT p.child_id as child_id, p.parent_id
         * " + "FROM processing_relationship p inner join processing_samples p_sam on (p_sam.processing_id = p.parent_id) " + "inner join
         * sample sam on (p_sam.sample_id = sam.sample_id) " + "where sam.experiment_id = ? " + "UNION ALL " +
         * "SELECT p.child_id, rl.parent_id " + "FROM processing_root_to_leaf rl, processing_relationship p " + "WHERE p.parent_id =
         * rl.child_id) " + "select * from File myfile where myfile.file_id in( " + "select distinct file_id from processing_root_to_leaf p,
         * processing_files pf " + "where p.parent_id = processing_id " + "UNION ALL " + "select distinct file_id from
         * processing_root_to_leaf p, processing_files pf " + "where p.child_id = processing_id " + "UNION ALL " + "select distinct file_id
         * from processing_files pf inner join processing_ius pr_i " + "on (pr_i.processing_id = pf.processing_id) " + "inner join ius i on
         * (i.ius_id = pr_i.ius_id) " + "inner join sample s on (s.sample_id = i.sample_id) " + "where s.experiment_id = ? " + "UNION
         * " + "select distinct file_id from processing_files pf inner join processing_experiments p_ex " + "on (p_ex.processing_id =
         * pf.processing_id) " + "where p_ex.experiment_id = ? " + "UNION " + "select distinct file_id from processing_files pf inner join
         * processing_samples p_sam " + "on (p_sam.processing_id = pf.processing_id) " + "inner join sample sam on (p_sam.sample_id =
         * sam.sample_id) " + "where sam.experiment_id = ? ) LIMIT 1";
         */
        String query = "WITH RECURSIVE processing_root_to_leaf (child_id, parent_id) AS ( "
                + "SELECT p.child_id as child_id, p.parent_id "
                + "FROM processing_relationship p inner join processing_lanes l on (l.processing_id = p.parent_id) "
                + "inner join lane ln on (ln.lane_id = l.lane_id) "
                + "inner join ius i on (i.lane_id = l.lane_id) "
                + "inner join sample s on (s.sample_id = i.sample_id)  "
                + "where s.experiment_id = ? "
                + "UNION "
                + "SELECT p.child_id as child_id, p.parent_id "
                + "FROM processing_relationship p inner join processing_ius pr_i on (pr_i.processing_id = p.parent_id) "
                + "inner join ius i on (i.ius_id = pr_i.ius_id) "
                + "inner join sample sam on (sam.sample_id = i.sample_id) "
                + "where sam.experiment_id = ? "
                + "UNION "
                + "SELECT p.child_id as child_id, p.parent_id "
                + "FROM processing_relationship p inner join processing_experiments p_ex on (p_ex.processing_id = p.parent_id) "
                + "where p_ex.experiment_id = ? "
                + "UNION "
                + "SELECT p.child_id as child_id, p.parent_id "
                + "FROM processing_relationship p inner join processing_samples p_sam on (p_sam.processing_id = p.parent_id) "
                + "inner join sample sam on (p_sam.sample_id = sam.sample_id) "
                + "where sam.experiment_id = ? "
                + // nested samples ius rec
                "UNION( "
                + "WITH RECURSIVE sample_root_to_leaf (child_id, parent_id) AS ( "
                + "SELECT s_rec.child_id as child_id, s_rec.parent_id FROM sample_relationship s_rec "
                + "INNER JOIN sample sam on (s_rec.parent_id = sam.sample_id) "
                + "where sam.experiment_id = ? "
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
                + "where sam.experiment_id = ? "
                + "UNION ALL "
                + "SELECT s_rec.child_id, s_rl.parent_id "
                + "FROM sample_root_to_leaf s_rl, sample_relationship s_rec "
                + "WHERE s_rec.parent_id = s_rl.child_id ) "
                + "select p.child_id as child_id, p.parent_id from sample_root_to_leaf s_rec, processing_samples p_sam, processing_relationship p "
                + "where (s_rec.parent_id = p_sam.sample_id or s_rec.child_id = p_sam.sample_id) "
                + "and p.parent_id = p_sam.processing_id ) "
                + "UNION ALL "
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
                + "where s.experiment_id = ? "
                + "UNION "
                + "select distinct file_id from processing_files pf inner join processing_experiments p_ex "
                + "on (p_ex.processing_id = pf.processing_id) "
                + "where p_ex.experiment_id = ? "
                + "UNION "
                + "select distinct file_id from processing_files pf inner join processing_samples p_sam "
                + "on (p_sam.processing_id = pf.processing_id) "
                + "inner join sample sam on (p_sam.sample_id = sam.sample_id) "
                + "where sam.experiment_id = ? "
                + // nested samples ius, first files
                "UNION ( "
                + "WITH RECURSIVE sample_root_to_leaf (child_id, parent_id) AS ( "
                + "SELECT s_rec.child_id as child_id, s_rec.parent_id FROM sample_relationship s_rec "
                + "INNER JOIN sample sam on (s_rec.parent_id = sam.sample_id) "
                + "where sam.experiment_id = ? "
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
                + "INNER JOIN sample sam on (s_rec.parent_id = sam.sample_id) " + "where sam.experiment_id = ? " + "UNION ALL "
                + "SELECT s_rec.child_id, s_rl.parent_id " + "FROM sample_root_to_leaf s_rl, sample_relationship s_rec "
                + "WHERE s_rec.parent_id = s_rl.child_id ) "
                + "select distinct pf.file_id from sample_root_to_leaf s_rec, processing_samples p_sam, processing_files pf "
                + "where (s_rec.parent_id = p_sam.sample_id or s_rec.child_id = p_sam.sample_id) "
                + "and p_sam.processing_id = pf.processing_id ) ) LIMIT 1";

        List list = this.currentSession().createSQLQuery(query).addEntity(File.class).setInteger(0, experimentId).setInteger(1, experimentId)
                .setInteger(2, experimentId).setInteger(3, experimentId).setInteger(4, experimentId).setInteger(5, experimentId)
                .setInteger(6, experimentId).setInteger(7, experimentId).setInteger(8, experimentId).setInteger(9, experimentId)
                .setInteger(10, experimentId).list();

        isHasFile = (list.size() > 0);

        return isHasFile;
    }

    /**
     * {@inheritDoc}
     *
     * @param experimentId
     */
    @Override
    public List<File> getFiles(Integer experimentId, String metaType) {
        List<File> files = new ArrayList<>();
        String query = "WITH RECURSIVE processing_root_to_leaf (child_id, parent_id) AS ( "
                + "SELECT p.child_id as child_id, p.parent_id "
                + "FROM processing_relationship p inner join processing_ius pr_i on (pr_i.processing_id = p.parent_id) "
                + "inner join ius i on (i.ius_id = pr_i.ius_id) "
                + "inner join sample sam on (sam.sample_id = i.sample_id) "
                + "where sam.experiment_id = ? "
                + "UNION "
                + "SELECT p.child_id as child_id, p.parent_id "
                + "FROM processing_relationship p inner join processing_experiments p_ex on (p_ex.processing_id = p.parent_id) "
                + "where p_ex.experiment_id = ? "
                + "UNION "
                + "SELECT p.child_id as child_id, p.parent_id "
                + "FROM processing_relationship p inner join processing_samples p_sam on (p_sam.processing_id = p.parent_id) "
                + "inner join sample sam on (p_sam.sample_id = sam.sample_id) "
                + "where sam.experiment_id = ? "
                + // nested samples ius rec
                "UNION( "
                + "WITH RECURSIVE sample_root_to_leaf (child_id, parent_id) AS ( "
                + "SELECT s_rec.child_id as child_id, s_rec.parent_id FROM sample_relationship s_rec "
                + "INNER JOIN sample sam on (s_rec.parent_id = sam.sample_id) "
                + "where sam.experiment_id = ? "
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
                + "where sam.experiment_id = ? "
                + "UNION ALL "
                + "SELECT s_rec.child_id, s_rl.parent_id "
                + "FROM sample_root_to_leaf s_rl, sample_relationship s_rec "
                + "WHERE s_rec.parent_id = s_rl.child_id ) "
                + "select p.child_id as child_id, p.parent_id from sample_root_to_leaf s_rec, processing_samples p_sam, processing_relationship p "
                + "where (s_rec.parent_id = p_sam.sample_id or s_rec.child_id = p_sam.sample_id) "
                + "and p.parent_id = p_sam.processing_id ) "
                + "UNION ALL "
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
                + "where s.experiment_id = ? "
                + "UNION "
                + "select distinct file_id from processing_files pf inner join processing_experiments p_ex "
                + "on (p_ex.processing_id = pf.processing_id) "
                + "where p_ex.experiment_id = ? "
                + "UNION "
                + "select distinct file_id from processing_files pf inner join processing_samples p_sam "
                + "on (p_sam.processing_id = pf.processing_id) "
                + "inner join sample sam on (p_sam.sample_id = sam.sample_id) "
                + "where sam.experiment_id = ? "
                + // nested samples ius, first files
                "UNION ( "
                + "WITH RECURSIVE sample_root_to_leaf (child_id, parent_id) AS ( "
                + "SELECT s_rec.child_id as child_id, s_rec.parent_id FROM sample_relationship s_rec "
                + "INNER JOIN sample sam on (s_rec.parent_id = sam.sample_id) "
                + "where sam.experiment_id = ? "
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
                + "INNER JOIN sample sam on (s_rec.parent_id = sam.sample_id) " + "where sam.experiment_id = ? " + "UNION ALL "
                + "SELECT s_rec.child_id, s_rl.parent_id " + "FROM sample_root_to_leaf s_rl, sample_relationship s_rec "
                + "WHERE s_rec.parent_id = s_rl.child_id ) "
                + "select distinct pf.file_id from sample_root_to_leaf s_rec, processing_samples p_sam, processing_files pf "
                + "where (s_rec.parent_id = p_sam.sample_id or s_rec.child_id = p_sam.sample_id) "
                + "and p_sam.processing_id = pf.processing_id ) )";

        List list = this.currentSession().createSQLQuery(query).addEntity(File.class).setInteger(0, experimentId).setInteger(1, experimentId)
                .setInteger(2, experimentId).setInteger(3, experimentId).setInteger(4, experimentId).setString(5, metaType)
                .setInteger(6, experimentId).setInteger(7, experimentId).setInteger(8, experimentId).setInteger(9, experimentId)
                .setInteger(10, experimentId).list();

        for (Object file : list) {
            File fl = (File) file;
            files.add(fl);
        }

        return files;
    }

    /**
     * {@inheritDoc}
     *
     * @param experimentId
     */
    @Override
    public boolean isHasFile(Integer experimentId, String metaType) {
        boolean isHasFile;
        String query = "WITH RECURSIVE processing_root_to_leaf (child_id, parent_id) AS ( "
                + "SELECT p.child_id as child_id, p.parent_id "
                + "FROM processing_relationship p inner join processing_ius pr_i on (pr_i.processing_id = p.parent_id) "
                + "inner join ius i on (i.ius_id = pr_i.ius_id) "
                + "inner join sample sam on (sam.sample_id = i.sample_id) "
                + "where sam.experiment_id = ? "
                + "UNION "
                + "SELECT p.child_id as child_id, p.parent_id "
                + "FROM processing_relationship p inner join processing_experiments p_ex on (p_ex.processing_id = p.parent_id) "
                + "where p_ex.experiment_id = ? "
                + "UNION "
                + "SELECT p.child_id as child_id, p.parent_id "
                + "FROM processing_relationship p inner join processing_samples p_sam on (p_sam.processing_id = p.parent_id) "
                + "inner join sample sam on (p_sam.sample_id = sam.sample_id) "
                + "where sam.experiment_id = ? "
                + // nested samples ius rec
                "UNION( "
                + "WITH RECURSIVE sample_root_to_leaf (child_id, parent_id) AS ( "
                + "SELECT s_rec.child_id as child_id, s_rec.parent_id FROM sample_relationship s_rec "
                + "INNER JOIN sample sam on (s_rec.parent_id = sam.sample_id) "
                + "where sam.experiment_id = ? "
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
                + "where sam.experiment_id = ? "
                + "UNION ALL "
                + "SELECT s_rec.child_id, s_rl.parent_id "
                + "FROM sample_root_to_leaf s_rl, sample_relationship s_rec "
                + "WHERE s_rec.parent_id = s_rl.child_id ) "
                + "select p.child_id as child_id, p.parent_id from sample_root_to_leaf s_rec, processing_samples p_sam, processing_relationship p "
                + "where (s_rec.parent_id = p_sam.sample_id or s_rec.child_id = p_sam.sample_id) "
                + "and p.parent_id = p_sam.processing_id ) "
                + "UNION ALL "
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
                + "where s.experiment_id = ? "
                + "UNION "
                + "select distinct file_id from processing_files pf inner join processing_experiments p_ex "
                + "on (p_ex.processing_id = pf.processing_id) "
                + "where p_ex.experiment_id = ? "
                + "UNION "
                + "select distinct file_id from processing_files pf inner join processing_samples p_sam "
                + "on (p_sam.processing_id = pf.processing_id) "
                + "inner join sample sam on (p_sam.sample_id = sam.sample_id) "
                + "where sam.experiment_id = ? "
                + // nested samples ius, first files
                "UNION ( "
                + "WITH RECURSIVE sample_root_to_leaf (child_id, parent_id) AS ( "
                + "SELECT s_rec.child_id as child_id, s_rec.parent_id FROM sample_relationship s_rec "
                + "INNER JOIN sample sam on (s_rec.parent_id = sam.sample_id) "
                + "where sam.experiment_id = ? "
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
                + "INNER JOIN sample sam on (s_rec.parent_id = sam.sample_id) " + "where sam.experiment_id = ? " + "UNION ALL "
                + "SELECT s_rec.child_id, s_rl.parent_id " + "FROM sample_root_to_leaf s_rl, sample_relationship s_rec "
                + "WHERE s_rec.parent_id = s_rl.child_id ) "
                + "select distinct pf.file_id from sample_root_to_leaf s_rec, processing_samples p_sam, processing_files pf "
                + "where (s_rec.parent_id = p_sam.sample_id or s_rec.child_id = p_sam.sample_id) "
                + "and p_sam.processing_id = pf.processing_id ) ) LIMIT 1";

        List list = this.currentSession().createSQLQuery(query).addEntity(File.class).setInteger(0, experimentId).setInteger(1, experimentId)
                .setInteger(2, experimentId).setInteger(3, experimentId).setInteger(4, experimentId).setString(5, metaType)
                .setInteger(6, experimentId).setInteger(7, experimentId).setInteger(8, experimentId).setInteger(9, experimentId)
                .setInteger(10, experimentId).list();

        isHasFile = (list.size() > 0);

        return isHasFile;
    }

    /**
     * {@inheritDoc}
     *
     * Finds an instance of Experiment in the database by the Experiment name.
     */
    @Override
    public Experiment findByTitle(String title) {
        String query = "from Experiment as experiment where lower(experiment.title) = ?";
        Experiment experiment = null;
        Object[] parameters = { title.toLowerCase() };
        List list = this.getHibernateTemplate().find(query, parameters);
        if (list.size() > 0) {
            experiment = (Experiment) list.get(0);
        }
        return experiment;
    }

    /**
     * {@inheritDoc}
     *
     * Finds an instance of Experiment in the database by the Experiment ID.
     *
     * @param expID
     */
    @Override
    public Experiment findByID(Integer expID) {
        String query = "from Experiment as experiment where experiment.experimentId = ?";
        Experiment experiment = null;
        Object[] parameters = { expID };
        List list = this.getHibernateTemplate().find(query, parameters);
        if (list.size() > 0) {
            experiment = (Experiment) list.get(0);
        }
        return experiment;
    }

    /** {@inheritDoc} */
    @SuppressWarnings({ "unchecked" })
    @Override
    public Experiment findBySWAccession(Integer swAccession) {
        String query = "from Experiment as experiment where experiment.swAccession = ?";
        Experiment experiment = null;
        Object[] parameters = { swAccession };
        List<Experiment> list = (List<Experiment>) this.getHibernateTemplate().find(query, parameters);
        if (list.size() > 0) {
            experiment = list.get(0);
        }
        return experiment;
    }

    /** {@inheritDoc} */
    @Override
    public Experiment updateDetached(Experiment experiment) {
        Experiment dbObject = findByID(experiment.getExperimentId());
        try {
            BeanUtilsBean beanUtils = new NullBeanUtils();
            beanUtils.copyProperties(dbObject, experiment);
            return this.getHibernateTemplate().merge(dbObject);
        } catch (IllegalAccessException | InvocationTargetException e) {
            localLogger.error("Could not update detached experiment", e);
        }
        return null;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public List<Experiment> findByOwnerID(Integer registrationID) {
        String query = "from Experiment as experiment where experiment.owner.registrationId = ?";
        Object[] parameters = { registrationID };
        return (List<Experiment>) this.getHibernateTemplate().find(query, parameters);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public List<Experiment> findByCriteria(String criteria, boolean isCaseSens) {
        String queryStringCase = "from Experiment as ex where ex.title like :title " + " or ex.description like :description "
                + " or cast(ex.swAccession as string) like :sw " + " or ex.name like :name order by ex.title, ex.name, ex.description";
        String queryStringICase = "from Experiment as ex where lower(ex.title) like :title "
                + " or lower(ex.description) like :description " + " or cast(ex.swAccession as string) like :sw "
                + " or lower(ex.name) like :name order by ex.title, ex.name, ex.description";
        Query query = isCaseSens ? this.currentSession().createQuery(queryStringCase) : this.currentSession().createQuery(queryStringICase);
        if (!isCaseSens) {
            criteria = criteria.toLowerCase();
        }
        criteria = "%" + criteria + "%";
        query.setString("title", criteria);
        query.setString("description", criteria);
        query.setString("sw", criteria);
        query.setString("name", criteria);

        return query.list();
    }

    /** {@inheritDoc} */
    @Override
    public List<Experiment> list() {
        ArrayList<Experiment> l = new ArrayList<>();

        String query = "from Experiment as ex order by ex.title, ex.name, ex.description";

        List list = this.getHibernateTemplate().find(query);

        for (Object e : list) {
            l.add((Experiment) e);
        }

        return l;
    }

    /** {@inheritDoc} */
    @Override
    public void update(Registration registration, Experiment experiment) {
        Experiment dbObject = reattachExperiment(experiment);
        if (registration == null) {
            localLogger.error("ExperimentDAOHibernate update registration is null");
        } else if (registration.isLIMSAdmin() || (experiment.givesPermission(registration) && dbObject.givesPermission(registration))) {
            localLogger.info("updating experiment object");
            update(experiment);
            currentSession().flush();
        } else {
            localLogger.error("ExperimentDAOHibernate update not authorized");
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param experiment
     */
    @Override
    public Integer insert(Registration registration, Experiment experiment) {
        Integer swAccession = 0;
        if (registration == null) {
            localLogger.error("ExperimentDAOHibernate insert registration is null");
        } else if (registration.isLIMSAdmin() || experiment.givesPermission(registration)) {
            localLogger.info("insert experiment object");
            swAccession = insert(experiment);
            currentSession().flush();
        } else {
            localLogger.error("ExperimentDAOHibernate insert not authorized");
        }
        return (swAccession);
    }

    /** {@inheritDoc} */
    @Override
    public Experiment updateDetached(Registration registration, Experiment experiment) {
        Experiment dbObject = reattachExperiment(experiment);
        if (registration == null) {
            localLogger.error("ExperimentDAOHibernate updateDetached registration is null");
        } else if (registration.isLIMSAdmin() || dbObject.givesPermission(registration)) {
            localLogger.info("updateDetached experiment object");
            return updateDetached(experiment);
        } else {
            localLogger.error("ExperimentDAOHibernate updateDetached not authorized");
        }
        return null;
    }

    private Experiment reattachExperiment(Experiment experiment) throws IllegalStateException, DataAccessResourceFailureException {
        Experiment dbObject = experiment;
        if (!currentSession().contains(experiment)) {
            dbObject = findByID(experiment.getExperimentId());
        }
        return dbObject;
    }
}
