package net.sourceforge.seqware.common.dao.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import net.sourceforge.seqware.common.dao.ProcessingIUSDAO;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingIus;
import net.sourceforge.seqware.common.util.NullBeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

/**
 * <p>
 * ProcessingIUSDAOHibernate class.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public class ProcessingIUSDAOHibernate extends HibernateDaoSupport implements ProcessingIUSDAO {

    /** {@inheritDoc} */
    @Override
    public void insert(ProcessingIus processingIus) {
        this.getHibernateTemplate().save(processingIus);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.seqware.common.dao.hibernate.ProcessingIusDAO#update ( net.sourceforge.seqware.common.model.ProcessingIus)
     */
    /** {@inheritDoc} */
    @Override
    public void update(ProcessingIus processingIus) {
        this.getHibernateTemplate().update(processingIus);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.seqware.common.dao.hibernate.ProcessingIusDAO#delete ( net.sourceforge.seqware.common.model.ProcessingIus)
     */
    /** {@inheritDoc} */
    @Override
    public void delete(ProcessingIus processingIus) {
        this.getHibernateTemplate().delete(processingIus);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.seqware.common.dao.hibernate.ProcessingIUSDAO#
     * findByProcessingIUS(net.sourceforge.seqware.common.model.Processing, net.sourceforge.seqware.common.model.IUS)
     */
    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("rawtypes")
    public ProcessingIus findByProcessingIUS(Processing processing, IUS ius) {
        String query = "from ProcessingIus as pe where pe.processing.processingId = ? and pe.ius.iusId = ?";
        ProcessingIus obj = null;
        Object[] parameters = { processing.getProcessingId(), ius.getIusId() };
        List list = this.getHibernateTemplate().find(query, parameters);
        if (list.size() > 0) {
            obj = (ProcessingIus) list.get(0);
        }
        return obj;
    }

    /** {@inheritDoc} */
    @Override
    public ProcessingIus updateDetached(ProcessingIus processingIus) {
        ProcessingIus dbObject = findByProcessingIUS(processingIus.getProcessing(), processingIus.getIus());
        try {
            BeanUtilsBean beanUtils = new NullBeanUtils();
            beanUtils.copyProperties(dbObject, processingIus);
            return this.getHibernateTemplate().merge(dbObject);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public List<ProcessingIus> list() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
