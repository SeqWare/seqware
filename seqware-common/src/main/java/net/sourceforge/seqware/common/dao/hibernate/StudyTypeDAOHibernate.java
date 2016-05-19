package net.sourceforge.seqware.common.dao.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.seqware.common.dao.StudyTypeDAO;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.StudyType;
import net.sourceforge.seqware.common.util.NullBeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

/**
 * <p>
 * StudyTypeDAOHibernate class.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public class StudyTypeDAOHibernate extends HibernateDaoSupport implements StudyTypeDAO {

    /**
     * <p>
     * Constructor for StudyTypeDAOHibernate.
     * </p>
     */
    public StudyTypeDAOHibernate() {
        super();
    }

    /**
     * {@inheritDoc}
     * 
     * @param studyType
     */
    @Override
    public void insert(StudyType studyType) {
        this.getHibernateTemplate().save(studyType);
    }

    /**
     * {@inheritDoc}
     * 
     * @param studyType
     */
    @Override
    public void update(StudyType studyType) {
        this.getHibernateTemplate().update(studyType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<StudyType> list(Registration registration) {
        if (registration == null) {
            return null;
        }
        return (list());
    }

    /**
     * {@inheritDoc}
     * 
     * Finds an instance of StudyType in the database by the StudyType name.
     */
    @Override
    public StudyType findByName(String name) {
        String query = "from studyType as studyType where studyType.name = ?";
        StudyType studyType = null;
        Object[] parameters = { name };
        List list = this.getHibernateTemplate().find(query, parameters);
        if (list.size() > 0) {
            studyType = (StudyType) list.get(0);
        }
        return studyType;
    }

    /**
     * {@inheritDoc}
     * 
     * Finds an instance of StudyType in the database by the StudyType ID.
     * 
     * @param expID
     */
    @SuppressWarnings("rawtypes")
    @Override
    public StudyType findByID(Integer expID) {
        String query = "from StudyType as studyType where studyType.studyTypeId = ?";
        StudyType studyType = null;
        Object[] parameters = { expID };
        List list = this.getHibernateTemplate().find(query, parameters);
        if (list.size() > 0) {
            studyType = (StudyType) list.get(0);
        }
        return studyType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StudyType updateDetached(StudyType studyType) {
        StudyType dbObject = findByID(studyType.getStudyTypeId());
        try {
            BeanUtilsBean beanUtils = new NullBeanUtils();
            beanUtils.copyProperties(dbObject, studyType);
            return (StudyType) this.getHibernateTemplate().merge(dbObject);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<StudyType> list() {
        ArrayList<StudyType> studyTypes = new ArrayList<>();

        List expmts = this.getHibernateTemplate().find("from StudyType as studyType order by studyType.studyTypeId asc" // desc
        );

        // expmts =
        // this.getHibernateTemplate().find("from StudyType as studyType order by studyType.name desc");
        for (Object studyType : expmts) {
            studyTypes.add((StudyType) studyType);
        }
        return studyTypes;
    }
}
