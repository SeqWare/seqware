package net.sourceforge.seqware.common.dao.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.seqware.common.dao.PlatformDAO;
import net.sourceforge.seqware.common.model.Platform;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.util.NullBeanUtils;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * <p>PlatformDAOHibernate class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class PlatformDAOHibernate extends HibernateDaoSupport implements PlatformDAO {

    /**
     * <p>Constructor for PlatformDAOHibernate.</p>
     */
    public PlatformDAOHibernate() {
        super();
    }

    /** {@inheritDoc} */
    public List<Platform> list(Registration registration) {
        ArrayList<Platform> platforms = new ArrayList<Platform>();
        if (registration == null) {
            return platforms;
        }

        List expmts = this.getHibernateTemplate().find("from Platform as platform order by platform.platformId asc" // desc
                );

        // expmts =
        // this.getHibernateTemplate().find("from Platform as platform order by platform.name desc");
        for (Object platform : expmts) {
            platforms.add((Platform) platform);
        }
        return platforms;
    }

    /** {@inheritDoc} */
    public Platform findByID(Integer id) {
        String query = "from Platform as p where p.platformId = ?";
        Platform obj = null;
        Object[] parameters = {id};
        List list = this.getHibernateTemplate().find(query, parameters);
        if (list.size() > 0) {
            obj = (Platform) list.get(0);
        }
        return obj;
    }

    /** {@inheritDoc} */
    @Override
    public Platform updateDetached(Platform platform) {
        Platform dbObject = findByID(platform.getPlatformId());
        try {
            BeanUtilsBean beanUtils = new NullBeanUtils();
            beanUtils.copyProperties(dbObject, platform);
            return (Platform) this.getHibernateTemplate().merge(dbObject);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public List<Platform> list() {
        ArrayList<Platform> platforms = new ArrayList<Platform>();

        List expmts = this.getHibernateTemplate().find("from Platform as platform order by platform.platformId asc" // desc
                );

        for (Object platform : expmts) {
            platforms.add((Platform) platform);
        }
        return platforms;
    }
}
