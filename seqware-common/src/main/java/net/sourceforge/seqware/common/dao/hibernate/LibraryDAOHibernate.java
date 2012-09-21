package net.sourceforge.seqware.common.dao.hibernate;

import java.util.List;
import java.util.Map;

import net.sourceforge.seqware.common.dao.LibraryDAO;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.SampleAttribute;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Repository
public class LibraryDAOHibernate implements LibraryDAO {

  @Autowired
  private SessionFactory sessionFactory;

  private Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public Sample findBySWAccession(Long swAccession) {
    String queryStringCase = "from Sample as s where s.swAccession = :swAccession";
    Query query = currentSession().createQuery(queryStringCase);
    query.setLong("swAccession", swAccession);

    return (Sample) query.uniqueResult();
  }

  @Override
  public List<Sample> getLibraries(String attributeName, String attributeValue) {
    String queryString = "select distinct sample from Sample as sample inner join sample.sampleAttributes as attribute where attribute.tag like :attributeName and attribute.value like :attributeValue";
    Query query = currentSession().createQuery(queryString);
    query.setString("attributeName", attributeName);
    query.setString("attributeValue", attributeValue);

    @SuppressWarnings("unchecked")
    List<Sample> records = query.list();

    Map<Sample, Sample> tmp = Maps.newHashMap();
    for (Sample sample : records) {
      findChildLibraries(tmp, sample);
    }

    List<Sample> result = Lists.newArrayList(tmp.values());

    return result;
  }

  private void findChildLibraries(Map<Sample, Sample> libraries, Sample node) {
    if (isLibrary(node)) {
      libraries.put(node, node);
    }
    for (Sample sample : node.getChildren()) {
      findChildLibraries(libraries, sample);
    }
  }

  private boolean isLibrary(Sample sample) {
    for (SampleAttribute sampleAttribute : sample.getSampleAttributes()) {
      if (sampleAttribute.getTag().equals("geo_reaction_id")) {
        return true;
      }
    }
    return false;
  }

  @Override
  public List<Sample> getLibraries() {
    String queryStringCase = "select distinct sample from Sample as sample inner join fetch sample.sampleAttributes as attribute where attribute.tag like 'geo_reaction_id'";

    Query query = currentSession().createQuery(queryStringCase);

    @SuppressWarnings("unchecked")
    List<Sample> records = query.list();

    return records;
  }
}
