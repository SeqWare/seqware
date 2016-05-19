package net.sourceforge.seqware.common.dao.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sourceforge.seqware.common.dao.SampleDAO;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.WorkflowRun;
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
 * SampleDAOHibernate class.
 * </p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class SampleDAOHibernate extends HibernateDaoSupport implements SampleDAO {

    final Logger localLogger = LoggerFactory.getLogger(SampleDAOHibernate.class);

    /**
     * <p>
     * Constructor for SampleDAOHibernate.
     * </p>
     */
    public SampleDAOHibernate() {
        super();
    }

    /**
     * {@inheritDoc}
     *
     * Inserts an instance of Sample into the database.
     */
    @Override
    public Integer insert(Sample sample) {
        this.getHibernateTemplate().save(sample);
        this.currentSession().flush();
        return sample.getSwAccession();
    }

    /**
     * {@inheritDoc}
     *
     * Updates an instance of Sample in the database.
     */
    @Override
    public void update(Sample sample) {

        this.getHibernateTemplate().update(sample);
        currentSession().flush();
    }

    /**
     * {@inheritDoc}
     *
     * This deletion will result in just the sample being deleted but the IUS will remain. This will potentially cause orphans which is not
     * really at all good. A better solution is to never delete but just use a deletion attribute.
     */
    @Override
    public void delete(Sample sample) {
        // remove parent experiment
        sample.getExperiment().getSamples().remove(sample);
        // clear IUS
        for (IUS i : sample.getIUS()) {
            for (Processing p : i.getProcessings()) {
                p.getIUS().remove(i);
                this.getHibernateTemplate().update(p);
            }
            i.setProcessings(null);
            for (WorkflowRun wr : i.getWorkflowRuns()) {
                wr.getIus().remove(i);
                this.getHibernateTemplate().update(wr);
            }
            i.setWorkflowRuns(null);
        }
        // don't remove processings
        for (Processing p : sample.getProcessings()) {
            p.getSamples().remove(sample);
        }
        sample.setProcessings(null);
        // flush
        this.getHibernateTemplate().update(sample);
        this.getHibernateTemplate().flush();
        // delete
        this.getHibernateTemplate().delete(sample);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<File> getFiles(Integer sampleId) {
        List<File> files = new ArrayList<>();

        String query = "WITH RECURSIVE processing_root_to_leaf (child_id, parent_id) AS ( "
                + "SELECT p.child_id as child_id, p.parent_id "
                + "FROM processing_relationship p inner join processing_ius pr_i on (pr_i.processing_id = p.parent_id) "
                + "inner join ius i on (i.ius_id = pr_i.ius_id) "
                + "where i.sample_id = ? "
                + "UNION "
                + "SELECT p.child_id as child_id, p.parent_id "
                + "FROM processing_relationship p inner join processing_samples p_sam on (p_sam.processing_id = p.parent_id) "
                + "where p_sam.sample_id = ? "
                + // nested samples ius rec
                "UNION( "
                + "WITH RECURSIVE sample_root_to_leaf (child_id, parent_id) AS ( "
                + "SELECT s_rec.child_id as child_id, s_rec.parent_id "
                + "FROM sample_relationship s_rec where s_rec.parent_id = ? "
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
                + "SELECT s_rec.child_id as child_id, s_rec.parent_id "
                + "FROM sample_relationship s_rec where s_rec.parent_id = ? "
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
                + "where i.sample_id = ? "
                + "UNION "
                + "select distinct file_id from processing_files pf inner join processing_samples p_sam "
                + "on (p_sam.processing_id = pf.processing_id) "
                + "where p_sam.sample_id = ? "
                + // nested samples ius, first files
                "UNION ( "
                + "WITH RECURSIVE sample_root_to_leaf (child_id, parent_id) AS ( "
                + "SELECT s_rec.child_id as child_id, s_rec.parent_id FROM sample_relationship s_rec "
                + "where s_rec.parent_id = ? "
                + "UNION ALL "
                + "SELECT s_rec.child_id, s_rl.parent_id "
                + "FROM sample_root_to_leaf s_rl, sample_relationship s_rec "
                + "WHERE s_rec.parent_id = s_rl.child_id ) "
                + "select distinct pf.file_id from sample_root_to_leaf s_rec, ius i, processing_ius pr_i, processing_files pf "
                + "where (s_rec.parent_id = i.sample_id or s_rec.child_id = i.sample_id) "
                + "and pr_i.ius_id = i.ius_id and pf.processing_id = pr_i.processing_id ) "
                + // nested samples processing_samples, first files
                "UNION ( " + "WITH RECURSIVE sample_root_to_leaf (child_id, parent_id) AS ( "
                + "SELECT s_rec.child_id as child_id, s_rec.parent_id FROM sample_relationship s_rec " + "where s_rec.parent_id = ? "
                + "UNION ALL " + "SELECT s_rec.child_id, s_rl.parent_id " + "FROM sample_root_to_leaf s_rl, sample_relationship s_rec "
                + "WHERE s_rec.parent_id = s_rl.child_id ) "
                + "select distinct pf.file_id from sample_root_to_leaf s_rec, processing_samples p_sam, processing_files pf "
                + "where (s_rec.parent_id = p_sam.sample_id or s_rec.child_id = p_sam.sample_id) "
                + "and p_sam.processing_id = pf.processing_id ) )";

        List list = this.currentSession().createSQLQuery(query).addEntity(File.class).setInteger(0, sampleId).setInteger(1, sampleId)
                .setInteger(2, sampleId).setInteger(3, sampleId).setInteger(4, sampleId).setInteger(5, sampleId).setInteger(6, sampleId)
                .setInteger(7, sampleId).list();

        for (Object file : list) {
            File fl = (File) file;
            files.add(fl);
        }

        return files;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHasFile(Integer sampleId) {
        boolean isHasFile;
        /*
         * String query = "WITH RECURSIVE processing_root_to_leaf (child_id, parent_id) AS ( " + "SELECT p.child_id as child_id, p.parent_id
         * " + "FROM processing_relationship p inner join processing_ius pr_i on (pr_i.processing_id = p.parent_id) " + "inner join ius i on
         * (i.ius_id = pr_i.ius_id) " + "where i.sample_id = ? " + "UNION " + "SELECT p.child_id as child_id, p.parent_id " + "FROM
         * processing_relationship p inner join processing_samples p_sam on (p_sam.processing_id = p.parent_id) " + "where p_sam.sample_id =
         * ? " + "UNION ALL " + "SELECT p.child_id, rl.parent_id " + "FROM processing_root_to_leaf rl, processing_relationship p " + "WHERE
         * p.parent_id = rl.child_id) " + "select * from File myfile where myfile.file_id in( " + "select distinct file_id from
         * processing_root_to_leaf p, processing_files pf " + "where p.parent_id = processing_id " + "UNION ALL " + "select distinct file_id
         * from processing_root_to_leaf p, processing_files pf " + "where p.child_id = processing_id " + "UNION ALL " + "select distinct
         * file_id from processing_files pf inner join processing_ius pr_i " + "on (pr_i.processing_id = pf.processing_id) " + "inner join
         * ius i on (i.ius_id = pr_i.ius_id) " + "where i.sample_id = ? " + "UNION " + "select distinct file_id from processing_files pf
         * inner join processing_samples p_sam " + "on (p_sam.processing_id = pf.processing_id) " + "where p_sam.sample_id = ? ) LIMIT 1";
         */
        String query = "WITH RECURSIVE processing_root_to_leaf (child_id, parent_id) AS ( "
                + "SELECT p.child_id as child_id, p.parent_id "
                + "FROM processing_relationship p inner join processing_ius pr_i on (pr_i.processing_id = p.parent_id) "
                + "inner join ius i on (i.ius_id = pr_i.ius_id) "
                + "where i.sample_id = ? "
                + "UNION "
                + "SELECT p.child_id as child_id, p.parent_id "
                + "FROM processing_relationship p inner join processing_samples p_sam on (p_sam.processing_id = p.parent_id) "
                + "where p_sam.sample_id = ? "
                + // nested samples ius rec
                "UNION( "
                + "WITH RECURSIVE sample_root_to_leaf (child_id, parent_id) AS ( "
                + "SELECT s_rec.child_id as child_id, s_rec.parent_id "
                + "FROM sample_relationship s_rec where s_rec.parent_id = ? "
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
                + "SELECT s_rec.child_id as child_id, s_rec.parent_id "
                + "FROM sample_relationship s_rec where s_rec.parent_id = ? "
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
                + "where i.sample_id = ? "
                + "UNION "
                + "select distinct file_id from processing_files pf inner join processing_samples p_sam "
                + "on (p_sam.processing_id = pf.processing_id) "
                + "where p_sam.sample_id = ? "
                + // nested samples ius, first files
                "UNION ( "
                + "WITH RECURSIVE sample_root_to_leaf (child_id, parent_id) AS ( "
                + "SELECT s_rec.child_id as child_id, s_rec.parent_id FROM sample_relationship s_rec "
                + "where s_rec.parent_id = ? "
                + "UNION ALL "
                + "SELECT s_rec.child_id, s_rl.parent_id "
                + "FROM sample_root_to_leaf s_rl, sample_relationship s_rec "
                + "WHERE s_rec.parent_id = s_rl.child_id ) "
                + "select distinct pf.file_id from sample_root_to_leaf s_rec, ius i, processing_ius pr_i, processing_files pf "
                + "where (s_rec.parent_id = i.sample_id or s_rec.child_id = i.sample_id) "
                + "and pr_i.ius_id = i.ius_id and pf.processing_id = pr_i.processing_id ) "
                + // nested samples processing_samples, first files
                "UNION ( " + "WITH RECURSIVE sample_root_to_leaf (child_id, parent_id) AS ( "
                + "SELECT s_rec.child_id as child_id, s_rec.parent_id FROM sample_relationship s_rec " + "where s_rec.parent_id = ? "
                + "UNION ALL " + "SELECT s_rec.child_id, s_rl.parent_id " + "FROM sample_root_to_leaf s_rl, sample_relationship s_rec "
                + "WHERE s_rec.parent_id = s_rl.child_id ) "
                + "select distinct pf.file_id from sample_root_to_leaf s_rec, processing_samples p_sam, processing_files pf "
                + "where (s_rec.parent_id = p_sam.sample_id or s_rec.child_id = p_sam.sample_id) "
                + "and p_sam.processing_id = pf.processing_id ) ) LIMIT 1";

        List list = this.currentSession().createSQLQuery(query).addEntity(File.class).setInteger(0, sampleId).setInteger(1, sampleId)
                .setInteger(2, sampleId).setInteger(3, sampleId).setInteger(4, sampleId).setInteger(5, sampleId).setInteger(6, sampleId)
                .setInteger(7, sampleId).list();

        isHasFile = (list.size() > 0);

        return isHasFile;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<File> getFiles(Integer sampleId, String metaType) {
        List<File> files = new ArrayList<>();
        /*
         * String query = "WITH RECURSIVE processing_root_to_leaf (child_id, parent_id) AS ( " + "SELECT p.child_id as child_id, p.parent_id
         * " + "FROM processing_relationship p inner join processing_ius pr_i on (pr_i.processing_id = p.parent_id) " + "inner join ius i on
         * (i.ius_id = pr_i.ius_id) " + "where i.sample_id = ? " + "UNION " + "SELECT p.child_id as child_id, p.parent_id " + "FROM
         * processing_relationship p inner join processing_samples p_sam on (p_sam.processing_id = p.parent_id) " + "where p_sam.sample_id =
         * ? " + "UNION ALL " + "SELECT p.child_id, rl.parent_id " + "FROM processing_root_to_leaf rl, processing_relationship p " + "WHERE
         * p.parent_id = rl.child_id) " + "select * from File myfile where myfile.meta_type=? and myfile.file_id in( " + "select distinct
         * file_id from processing_root_to_leaf p, processing_files pf " + "where p.parent_id = processing_id " + "UNION ALL " + "select
         * distinct file_id from processing_root_to_leaf p, processing_files pf " + "where p.child_id = processing_id " + "UNION ALL
         * " + "select distinct file_id from processing_files pf inner join processing_ius pr_i " + "on (pr_i.processing_id =
         * pf.processing_id) " + "inner join ius i on (i.ius_id = pr_i.ius_id) " + "where i.sample_id = ? " + "UNION " + "select distinct
         * file_id from processing_files pf inner join processing_samples p_sam " + "on (p_sam.processing_id = pf.processing_id) " + "where
         * p_sam.sample_id = ? )";
         */
        String query = "WITH RECURSIVE processing_root_to_leaf (child_id, parent_id) AS ( "
                + "SELECT p.child_id as child_id, p.parent_id "
                + "FROM processing_relationship p inner join processing_ius pr_i on (pr_i.processing_id = p.parent_id) "
                + "inner join ius i on (i.ius_id = pr_i.ius_id) "
                + "where i.sample_id = ? "
                + "UNION "
                + "SELECT p.child_id as child_id, p.parent_id "
                + "FROM processing_relationship p inner join processing_samples p_sam on (p_sam.processing_id = p.parent_id) "
                + "where p_sam.sample_id = ? "
                + // nested samples ius rec
                "UNION( "
                + "WITH RECURSIVE sample_root_to_leaf (child_id, parent_id) AS ( "
                + "SELECT s_rec.child_id as child_id, s_rec.parent_id "
                + "FROM sample_relationship s_rec where s_rec.parent_id = ? "
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
                + "SELECT s_rec.child_id as child_id, s_rec.parent_id "
                + "FROM sample_relationship s_rec where s_rec.parent_id = ? "
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
                + "where i.sample_id = ? "
                + "UNION "
                + "select distinct file_id from processing_files pf inner join processing_samples p_sam "
                + "on (p_sam.processing_id = pf.processing_id) "
                + "where p_sam.sample_id = ? "
                + // nested samples ius, first files
                "UNION ( "
                + "WITH RECURSIVE sample_root_to_leaf (child_id, parent_id) AS ( "
                + "SELECT s_rec.child_id as child_id, s_rec.parent_id FROM sample_relationship s_rec "
                + "where s_rec.parent_id = ? "
                + "UNION ALL "
                + "SELECT s_rec.child_id, s_rl.parent_id "
                + "FROM sample_root_to_leaf s_rl, sample_relationship s_rec "
                + "WHERE s_rec.parent_id = s_rl.child_id ) "
                + "select distinct pf.file_id from sample_root_to_leaf s_rec, ius i, processing_ius pr_i, processing_files pf "
                + "where (s_rec.parent_id = i.sample_id or s_rec.child_id = i.sample_id) "
                + "and pr_i.ius_id = i.ius_id and pf.processing_id = pr_i.processing_id ) "
                + // nested samples processing_samples, first files
                "UNION ( " + "WITH RECURSIVE sample_root_to_leaf (child_id, parent_id) AS ( "
                + "SELECT s_rec.child_id as child_id, s_rec.parent_id FROM sample_relationship s_rec " + "where s_rec.parent_id = ? "
                + "UNION ALL " + "SELECT s_rec.child_id, s_rl.parent_id " + "FROM sample_root_to_leaf s_rl, sample_relationship s_rec "
                + "WHERE s_rec.parent_id = s_rl.child_id ) "
                + "select distinct pf.file_id from sample_root_to_leaf s_rec, processing_samples p_sam, processing_files pf "
                + "where (s_rec.parent_id = p_sam.sample_id or s_rec.child_id = p_sam.sample_id) "
                + "and p_sam.processing_id = pf.processing_id ) )";

        // "select * from File myfile where myfile.meta_type=? and myfile.file_id in( "
        // +

        List list = this.currentSession().createSQLQuery(query).addEntity(File.class).setInteger(0, sampleId).setInteger(1, sampleId)
                .setInteger(2, sampleId).setInteger(3, sampleId).setString(4, metaType).setInteger(5, sampleId).setInteger(6, sampleId)
                .setInteger(7, sampleId).setInteger(8, sampleId).list();

        for (Object file : list) {
            File fl = (File) file;
            files.add(fl);
        }

        return files;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHasFile(Integer sampleId, String metaType) {
        boolean isHasFile;
        String query = "WITH RECURSIVE processing_root_to_leaf (child_id, parent_id) AS ( "
                + "SELECT p.child_id as child_id, p.parent_id "
                + "FROM processing_relationship p inner join processing_ius pr_i on (pr_i.processing_id = p.parent_id) "
                + "inner join ius i on (i.ius_id = pr_i.ius_id) "
                + "where i.sample_id = ? "
                + "UNION "
                + "SELECT p.child_id as child_id, p.parent_id "
                + "FROM processing_relationship p inner join processing_samples p_sam on (p_sam.processing_id = p.parent_id) "
                + "where p_sam.sample_id = ? "
                + // nested samples ius rec
                "UNION( "
                + "WITH RECURSIVE sample_root_to_leaf (child_id, parent_id) AS ( "
                + "SELECT s_rec.child_id as child_id, s_rec.parent_id "
                + "FROM sample_relationship s_rec where s_rec.parent_id = ? "
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
                + "SELECT s_rec.child_id as child_id, s_rec.parent_id "
                + "FROM sample_relationship s_rec where s_rec.parent_id = ? "
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
                + "where i.sample_id = ? "
                + "UNION "
                + "select distinct file_id from processing_files pf inner join processing_samples p_sam "
                + "on (p_sam.processing_id = pf.processing_id) "
                + "where p_sam.sample_id = ? "
                + // nested samples ius, first files
                "UNION ( "
                + "WITH RECURSIVE sample_root_to_leaf (child_id, parent_id) AS ( "
                + "SELECT s_rec.child_id as child_id, s_rec.parent_id FROM sample_relationship s_rec "
                + "where s_rec.parent_id = ? "
                + "UNION ALL "
                + "SELECT s_rec.child_id, s_rl.parent_id "
                + "FROM sample_root_to_leaf s_rl, sample_relationship s_rec "
                + "WHERE s_rec.parent_id = s_rl.child_id ) "
                + "select distinct pf.file_id from sample_root_to_leaf s_rec, ius i, processing_ius pr_i, processing_files pf "
                + "where (s_rec.parent_id = i.sample_id or s_rec.child_id = i.sample_id) "
                + "and pr_i.ius_id = i.ius_id and pf.processing_id = pr_i.processing_id ) "
                + // nested samples processing_samples, first files
                "UNION ( " + "WITH RECURSIVE sample_root_to_leaf (child_id, parent_id) AS ( "
                + "SELECT s_rec.child_id as child_id, s_rec.parent_id FROM sample_relationship s_rec " + "where s_rec.parent_id = ? "
                + "UNION ALL " + "SELECT s_rec.child_id, s_rl.parent_id " + "FROM sample_root_to_leaf s_rl, sample_relationship s_rec "
                + "WHERE s_rec.parent_id = s_rl.child_id ) "
                + "select distinct pf.file_id from sample_root_to_leaf s_rec, processing_samples p_sam, processing_files pf "
                + "where (s_rec.parent_id = p_sam.sample_id or s_rec.child_id = p_sam.sample_id) "
                + "and p_sam.processing_id = pf.processing_id ) ) LIMIT 1";

        List list = this.currentSession().createSQLQuery(query).addEntity(File.class).setInteger(0, sampleId).setInteger(1, sampleId)
                .setInteger(2, sampleId).setInteger(3, sampleId).setString(4, metaType).setInteger(5, sampleId).setInteger(6, sampleId)
                .setInteger(7, sampleId).setInteger(8, sampleId).list();

        isHasFile = (list.size() > 0);

        return isHasFile;
    }

    /*
     * public List<Sample> listWithUIData(Integer expId){ List<Sample> samples = new ArrayList<Sample>(); String query = "WITH RECURSIVE
     * processing_root_to_leaf (child_id, parent_id, sample_id) AS ( " + "SELECT p.child_id as child_id, p.parent_id, s.sample_id " + "FROM
     * processing_relationship p inner join processing_lanes l on (l.processing_id = p.parent_id) " + "inner join lane ln on (ln.lane_id =
     * l.lane_id) " + "inner join sample s on (s.sample_id = ln.sample_id) " + "where s.experiment_id = ? " +
     *
     * "UNION ALL " + "SELECT p.child_id, rl.parent_id, rl.sample_id " + "FROM processing_root_to_leaf rl, processing_relationship p
     * " + "WHERE p.parent_id = rl.child_id) " +
     *
     * "select s1.sample_id, count(f) from Sample s1 left join ( " + "select file_id f, p.sample_id sam from processing_root_to_leaf p, " +
     * "processing_files pf where p.parent_id = processing_id " + "UNION ALL " +
     * "select file_id f, p.sample_id sam from processing_root_to_leaf p, " + "processing_files pf where p.child_id = processing_id " +
     * "UNION ALL " + "select file_id f, s.sample_id sam from processing_files pf inner join processing_lanes l " + "on (l.processing_id =
     * pf.processing_id) " + "inner join lane ln on (ln.lane_id = l.lane_id) " + "inner join sample s on (s.sample_id = ln.sample_id)
     * " + "where s.experiment_id = 2) q on q.sam=s1.sample_id where s1.experiment_id = ? group by s1.sample_id" ;
     *
     * List list = this.currentSession().createSQLQuery(query).addEntity(Sample.class) .setInteger(0, expId).setInteger(1, expId).list();
     *
     * for (Object sample : list) { samples.add((Sample)sample); }
     *
     * return samples; }
     */
    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Integer, Integer> getCountFiles(Integer expId) {
        String query = "WITH RECURSIVE processing_root_to_leaf (child_id, parent_id, sample_id) AS ( "
                + "SELECT p.child_id as child_id, p.parent_id, s.sample_id "
                + "FROM processing_relationship p inner join processing_ius pr_i on (pr_i.processing_id = p.parent_id) "
                + "inner join ius i on (i.ius_id = pr_i.ius_id) " + "inner join sample s on (s.sample_id = i.sample_id) "
                + "where s.experiment_id = ? " + "UNION ALL " + "SELECT p.child_id, rl.parent_id, rl.sample_id "
                + "FROM processing_root_to_leaf rl, processing_relationship p " + "WHERE p.parent_id = rl.child_id) "
                + "select s1.sample_id, count(f) as countFile from Sample s1 left join ( "
                + "select file_id f, p.sample_id sam from processing_root_to_leaf p, "
                + "processing_files pf where p.parent_id = processing_id " + "UNION ALL "
                + "select file_id f, p.sample_id sam from processing_root_to_leaf p, "
                + "processing_files pf where p.child_id = processing_id " + "UNION ALL "
                + "select file_id f, s.sample_id sam from processing_files pf inner join processing_ius pr_i "
                + "on (pr_i.processing_id = pf.processing_id) " + "inner join ius i on (i.ius_id = pr_i.ius_id) "
                + "inner join sample s on (s.sample_id = i.sample_id) "
                + "where s.experiment_id = ?) q on q.sam=s1.sample_id where s1.experiment_id = ?  group by s1.sample_id";

        // , count(f) as countFile

        List list = this.currentSession().createSQLQuery(query).setInteger(0, expId).setInteger(1, expId).setInteger(2, expId).list();

        Map<Integer, Integer> countFiles = new HashMap<>();
        for (Object resSet : list) {
            // Sample sm = (Sample)sample;
            // samples.add(sm);
            // Integer sampleId = resSet.
            // logger.debug("Sample id = " + sm.getSampleId());
            // logger.debug("Res set = " + resSet.toString());
            Object[] res = (Object[]) resSet;
            Integer sampleId = Integer.parseInt(res[0].toString());

            Integer fileCount = Integer.parseInt(res[1].toString());

            countFiles.put(sampleId, fileCount);

            localLogger.debug("Sample id = " + sampleId + "; File Co = " + fileCount);
        }
        return countFiles;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Integer, Integer> getCountFiles(Integer expId, String metaType) {
        String query = "WITH RECURSIVE processing_root_to_leaf (child_id, parent_id, sample_id) AS ( "
                + "SELECT p.child_id as child_id, p.parent_id, s.sample_id "
                + "FROM processing_relationship p inner join processing_ius pr_i " + "on (pr_i.processing_id = p.parent_id) "
                + "inner join ius i on (i.ius_id = pr_i.ius_id) " + "inner join sample s on (s.sample_id = i.sample_id) "
                + "where s.experiment_id = ? " + "UNION ALL " + "SELECT p.child_id, rl.parent_id, rl.sample_id "
                + "FROM processing_root_to_leaf rl, processing_relationship p " + "WHERE p.parent_id = rl.child_id) "
                + "select s1.sample_id, count(F) from Sample s1 left join ( "
                + "select pf.file_id f, p.sample_id sam from processing_root_to_leaf p, "
                + "processing_files pf, file where p.parent_id = processing_id and  file.file_id = pf.file_id  "
                + "and file.meta_type = ? " + "UNION ALL " + "select pf.file_id f, p.sample_id sam from processing_root_to_leaf p, "
                + "processing_files pf, file where p.child_id = processing_id and  file.file_id = pf.file_id  " + "and file.meta_type = ? "
                + "UNION ALL " + "select pf.file_id f, s.sample_id sam from processing_files pf inner join processing_ius pr_i "
                + "on (pr_i.processing_id = pf.processing_id) " + "inner join ius i on (i.ius_id = pr_i.ius_id) "
                + "inner join sample s on (s.sample_id = i.sample_id) " + "inner join file file on (file.file_id = pf.file_id) "
                + "where file.meta_type = ? and s.experiment_id = ? )  "
                + "q on q.sam=s1.sample_id where s1.experiment_id = ?  group by s1.sample_id";

        // , count(f) as countFile

        List list = this.currentSession().createSQLQuery(query).setInteger(0, expId).setString(1, metaType).setString(2, metaType)
                .setString(3, metaType).setInteger(4, expId).setInteger(5, expId).list();

        Map<Integer, Integer> countFiles = new HashMap<>();
        for (Object resSet : list) {
            Object[] res = (Object[]) resSet;
            Integer sampleId = Integer.parseInt(res[0].toString());

            Integer fileCount = Integer.parseInt(res[1].toString());

            countFiles.put(sampleId, fileCount);

            localLogger.debug("Sample id = " + sampleId + "; File Co = " + fileCount);
        }
        return countFiles;
    }

    /**
     * {@inheritDoc}
     *
     * Finds an instance of Sample in the database by the Experiment name.
     */
    @Override
    public Sample findByTitle(String title) {
        String query = "from Sample as sample where lower(sample.title) = ?";
        Sample sample = null;
        Object[] parameters = { title.toLowerCase() };
        List list = this.getHibernateTemplate().find(query, parameters);
        if (list.size() > 0) {
            sample = (Sample) list.get(0);
        }
        return sample;
    }

    /**
     * {@inheritDoc}
     *
     * Finds an instance of Sample in the database by the Sample emailAddress.
     */
    @Override
    public Sample findByName(String name) {
        String query = "from Sample as sample where sample.name = ?";
        Sample sample = null;
        Object[] parameters = { name };
        List list = this.getHibernateTemplate().find(query, parameters);
        if (list.size() > 0) {
            sample = (Sample) list.get(0);
        }
        return sample;
    }

    /**
     * {@inheritDoc}
     *
     * Finds an instance of Sample in the database by the Sample emailAddress.
     */
    @Override
    public List<Sample> matchName(String name) {
        String query = "from Sample as sample where sample.name like ?";
        Object[] parameters = { name };
        List list = this.getHibernateTemplate().find(query, parameters);
        return (List<Sample>) list;
    }

    /**
     * {@inheritDoc}
     *
     * Finds an instance of Sample in the database by the Sample ID.
     *
     * @param id
     */
    @Override
    public Sample findByID(Integer id) {
        String query = "from Sample as sample where sample.sampleId = ?";
        Sample sample = null;
        Object[] parameters = { id };
        List list = this.getHibernateTemplate().find(query, parameters);
        if (list.size() > 0) {
            sample = (Sample) list.get(0);
        }
        return sample;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public Sample findBySWAccession(Integer swAccession) {
        String query = "from Sample as sample where sample.swAccession = ?";
        Sample sample = null;
        Object[] parameters = { swAccession };
        List<Sample> list = (List<Sample>) this.getHibernateTemplate().find(query, parameters);
        if (list.size() > 0) {
            sample = list.get(0);
        }
        return sample;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Sample> findByOwnerID(Integer registrationId) {
        String query = "from Sample as sample where sample.owner.registrationId = ?";
        Object[] parameters = { registrationId };
        return (List<Sample>) this.getHibernateTemplate().find(query, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Sample> findByCriteria(String criteria, boolean isCaseSens) {
        String queryStringCase = "from Sample as s where s.title like :title " + " or s.description like :description "
                + " or cast(s.swAccession as string) like :sw " + " or s.name like :name order by s.title, s.name, s.description";
        String queryStringICase = "from Sample as s where lower(s.title) like :title " + " or lower(s.description) like :description "
                + " or cast(s.swAccession as string) like :sw " + " or lower(s.name) like :name order by s.title, s.name, s.description";
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

    /**
     * <p>
     * listComplete.
     * </p>
     *
     * @return a {@link java.util.List} object.
     */
    @Override
    public List<Sample> listComplete() {
        List<Sample> list;
        List<Sample> filteredList = new ArrayList<>();
        String query = "from Sample as sample";
        Object[] parameters = {};
        list = (List<Sample>) this.getHibernateTemplate().find(query, parameters);
        Log.stderr("The list: " + list);
        for (Sample sample : list) {
            Log.stderr("Curr sample: " + sample);
            Log.stderr("sample lanes: " + sample.getLanes());
            Log.stderr("sample expected num runs: " + sample.getExpectedNumRuns());
            if (sample.getLanes() != null && sample.getExpectedNumRuns() != null && sample.getLanes().size() >= sample.getExpectedNumRuns()) {
                filteredList.add(sample);
            }
        }
        return (filteredList);
    }

    /**
     * <p>
     * listIncomplete.
     * </p>
     *
     * @return a {@link java.util.List} object.
     */
    @Override
    public List<Sample> listIncomplete() {
        List<Sample> list;
        List<Sample> filteredList = new ArrayList<>();
        String query = "from Sample as sample";
        Object[] parameters = {};
        list = (List<Sample>) this.getHibernateTemplate().find(query, parameters);
        for (Sample sample : list) {
            if (sample.getLanes() == null || sample.getExpectedNumRuns() == null || sample.getLanes().size() < sample.getExpectedNumRuns()) {
                filteredList.add(sample);
            }
        }
        return (filteredList);
    }

    /**
     * {@inheritDoc}
     *
     * @param registaration
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Sample> listSample(Registration registaration) {
        Integer ownerId = registaration.getRegistrationId();
        List<Sample> list;
        String query = "from Sample as sample where sample.owner.registrationId = ?";
        Object[] parameters = { ownerId };

        if (registaration.isLIMSAdmin()) {
            query = "from Sample as sample";
            parameters = null;
        }

        list = (List<Sample>) this.getHibernateTemplate().find(query, parameters);
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Sample> getRootSamples(Study study) {
        List<Sample> list;
        String query = "from Sample as sample where sample.experiment.study.studyId = ?";
        Object parameter = study.getStudyId();
        list = (List<Sample>) this.getHibernateTemplate().find(query, parameter);
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Sample getRootSample(Sample sample) {
        Sample upSample = sample;
        while (!upSample.getParents().isEmpty() /*
                                                 * == null
                                                 */) {
            upSample = upSample.getParents().iterator().next();
        }
        // String query = "WITH RECURSIVE child_to_root(parent_id, child_id) AS ("
        // +
        // " SELECT parent_id, child_id FROM sample_relationship WHERE child_id = ? UNION"
        // +
        // " SELECT sr.parent_id, cr.child_id FROM sample_relationship sr, child_to_root cr"
        // +
        // " WHERE cr.parent_id = sr.child_id ) SELECT s.* FROM child_to_root cr, sample s"
        // +
        // " WHERE cr.parent_id not in (SELECT child_id FROM sample_relationship) AND s.sample_id = cr.parent_id"
        // + " UNION" + " SELECT s.* FROM sample s"
        // + " WHERE s.sample_id not in (select parent_id FROM sample_relationship)"
        // + " AND s.sample_id not in (select child_id FROM sample_relationship)" +
        // " AND s.sample_id = ?";
        // Sample rootSample = (Sample)
        // this.currentSession().createSQLQuery(query).addEntity(Sample.class)
        // .setInteger(0, sample.getSampleId()).setInteger(1,
        // sample.getSampleId()).uniqueResult();
        // return rootSample;
        return upSample;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Sample updateDetached(Sample sample) {
        Sample dbObject = reattachSample(sample);
        try {
            BeanUtilsBean beanUtils = new NullBeanUtils();
            beanUtils.copyProperties(dbObject, sample);
            return this.getHibernateTemplate().merge(dbObject);
        } catch (IllegalAccessException | InvocationTargetException e) {
            localLogger.error("Error updating detached sample", e);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Sample> list() {
        ArrayList<Sample> l = new ArrayList<>();

        String query = "from Sample";

        List list = this.getHibernateTemplate().find(query);

        for (Object e : list) {
            l.add((Sample) e);
        }

        return l;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(Registration registration, Sample sample) {
        Sample dbObject = reattachSample(sample);
        if (registration == null) {
            localLogger.error("SampleDAOHibernate update registration is null");
        } else if (registration.isLIMSAdmin() || (sample.givesPermission(registration) && dbObject.givesPermission(registration))) {
            localLogger.info("updating sample object");
            update(sample);
        } else {
            localLogger.error("SampleDAOHibernate update not authorized");
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer insert(Registration registration, Sample sample) {
        Integer swAccession = 0;
        if (registration == null) {
            localLogger.error("SampleDAOHibernate insert registration is null");
        } else if (registration.isLIMSAdmin() || sample.givesPermission(registration)) {
            localLogger.info("insert sample object");
            insert(sample);
            this.currentSession().flush();
            swAccession = sample.getSwAccession();
        } else {
            localLogger.error("SampleDAOHibernate insert not authorized");
        }
        return (swAccession);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Sample updateDetached(Registration registration, Sample sample) {
        Sample dbObject = reattachSample(sample);
        if (registration == null) {
            localLogger.error("SampleDAOHibernate updateDetached registration is null");
        } else if (registration.isLIMSAdmin() || dbObject.givesPermission(registration)) {
            localLogger.info("updateDetached sample object");
            return updateDetached(sample);
        } else {
            localLogger.error("SampleDAOHibernate updateDetached not authorized");
        }
        return null;
    }

    private Sample reattachSample(Sample sample) throws IllegalStateException, DataAccessResourceFailureException {
        Sample dbObject = sample;
        if (!currentSession().contains(sample)) {
            dbObject = findByID(sample.getSampleId());
        }
        return dbObject;
    }
}
