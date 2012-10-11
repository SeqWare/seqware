package net.sourceforge.seqware.common.dao.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.seqware.common.dao.OrganismDAO;
import net.sourceforge.seqware.common.model.Organism;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.util.NullBeanUtils;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class OrganismDAOHibernate extends HibernateDaoSupport implements OrganismDAO {

    public OrganismDAOHibernate() {
        super();
    }

    public List<Organism> list(Registration registration) {
        ArrayList<Organism> organisms = new ArrayList<Organism>();
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

    public Organism findByID(Integer id) {
        String query = "from Organism as p where p.organismId = ?";
        Organism obj = null;
        Object[] parameters = {id};
        List list = this.getHibernateTemplate().find(query, parameters);
        if (list.size() > 0) {
            obj = (Organism) list.get(0);
        }
        return obj;
    }

    @Override
    public Organism updateDetached(Organism organism) {
        Organism dbObject = findByID(organism.getOrganismId());
        try {
            BeanUtilsBean beanUtils = new NullBeanUtils();
            beanUtils.copyProperties(dbObject, organism);
            return (Organism) this.getHibernateTemplate().merge(dbObject);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Organism> list() {
        ArrayList<Organism> organisms = new ArrayList<Organism>();

        List expmts = this.getHibernateTemplate().find("from Organism as organism order by organism.name asc" // desc
                );

        for (Object organism : expmts) {
            organisms.add((Organism) organism);
        }
        return organisms;
    }
}
