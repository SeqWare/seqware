package net.sourceforge.seqware.common.dao.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.seqware.common.dao.StudyTypeDAO;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.StudyType;
import net.sourceforge.seqware.common.util.NullBeanUtils;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class StudyTypeDAOHibernate extends HibernateDaoSupport implements StudyTypeDAO {
  public StudyTypeDAOHibernate() {
    super();
  }

  public void insert(StudyType studyType) {
    this.getHibernateTemplate().save(studyType);
  }

  public void update(StudyType studyType) {
    this.getHibernateTemplate().update(studyType);
  }

  public List<StudyType> list(Registration registration) {
    ArrayList<StudyType> studyTypes = new ArrayList<StudyType>();
    if (registration == null)
      return studyTypes;

    List expmts = this.getHibernateTemplate().find("from StudyType as studyType order by studyType.studyTypeId asc" // desc
    );

    // expmts =
    // this.getHibernateTemplate().find("from StudyType as studyType order by studyType.name desc");
    for (Object studyType : expmts) {
      studyTypes.add((StudyType) studyType);
    }
    return studyTypes;
  }

  /**
   * Finds an instance of StudyType in the database by the StudyType name.
   * 
   * @param name
   *          name of the StudyType
   * @return StudyType or null if not found
   */
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
   * Finds an instance of StudyType in the database by the StudyType ID.
   * 
   * @param expID
   *          ID of the StudyType
   * @return StudyType or null if not found
   */
  @SuppressWarnings("rawtypes")
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

  @Override
  public StudyType updateDetached(StudyType studyType) {
    StudyType dbObject = findByID(studyType.getStudyTypeId());
    try {
      BeanUtilsBean beanUtils = new NullBeanUtils();
      beanUtils.copyProperties(dbObject, studyType);
      return (StudyType) this.getHibernateTemplate().merge(dbObject);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    return null;
  }

    @Override
    public List<StudyType> list() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
