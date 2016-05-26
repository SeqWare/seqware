package net.sourceforge.seqware.common.dao.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.seqware.common.dao.OrganismDAO;
import net.sourceforge.seqware.common.model.Organism;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.util.NullBeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

/**
 * <p>
 * OrganismDAOHibernate class.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public class OrganismDAOHibernate extends HibernateDaoSupport implements OrganismDAO {

    /**
     * <p>
     * Constructor for OrganismDAOHibernate.
     * </p>
     */
    public OrganismDAOHibernate() {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public List<Organism> list(Registration registration) {
        ArrayList<Organism> organisms = new ArrayList<>();
        if (registration == null) {
            return organisms;
        }

        List expmts = this.getHibernateTemplate().find("from Organism as organism order by organism.name asc" // desc
        );

        for (Object organism : expmts) {
            organisms.add((Organism) organism);
        }
        return organisms;
    }

    /** {@inheritDoc} */
    @Override
    public Organism findByID(Integer id) {
        String query = "from Organism as p where p.organismId = ?";
        Organism obj = null;
        Object[] parameters = { id };
        List list = this.getHibernateTemplate().find(query, parameters);
        if (list.size() > 0) {
            obj = (Organism) list.get(0);
        }
        return obj;
    }

    /** {@inheritDoc} */
    @Override
    public Organism updateDetached(Organism organism) {
        Organism dbObject = findByID(organism.getOrganismId());
        try {
            BeanUtilsBean beanUtils = new NullBeanUtils();
            beanUtils.copyProperties(dbObject, organism);
            return this.getHibernateTemplate().merge(dbObject);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public List<Organism> list() {
        ArrayList<Organism> organisms = new ArrayList<>();

        List expmts = this.getHibernateTemplate().find("from Organism as organism order by organism.name asc" // desc
        );

        for (Object organism : expmts) {
            organisms.add((Organism) organism);
        }
        return organisms;
    }
}
