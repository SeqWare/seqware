/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.seqware.webservice.generated.controller;

import java.lang.annotation.Annotation;
import java.sql.SQLException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.metamodel.EntityType;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import com.sun.jersey.spi.inject.Errors.ErrorMessage;

/**
 * 
 * @author boconnor
 * @param <T>
 */
public abstract class AbstractFacade<T> {
    protected Class<T> entityClass;

    protected String entityTableName;
    
    public AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract EntityManager getEntityManager();

    public void create(T entity) {
        getEntityManager().persist(entity);
    }

    public void edit(T entity) {
        getEntityManager().merge(entity);
    }

    public void remove(T entity) {
        getEntityManager().remove(getEntityManager().merge(entity));
    }

    public T find(Object id) {
        return getEntityManager().find(entityClass, id);
    }
    
    public void populateEntityTableName()
    {
        this.entityTableName = getEntityTableName();
    }
    
    protected String getEntityTableName()
    {
        String tableName = entityClass.getSimpleName(); 
        for (Annotation a : entityClass.getAnnotations())
        {
            if (a.annotationType().getName().equals("javax.persistence.Table"))
            {
                try {
                    if (a.annotationType().getField("name") != null)
                    {
                       tableName =  (String) a.annotationType().getField("name").get(entityTableName);
                       if (tableName == null || entityTableName.equals("") || entityTableName.equals("null"))
                       {
                           tableName = entityClass.getSimpleName();       
                       }
                       return tableName;
                    }
                } catch (NoSuchFieldException e) {
                    tableName = entityClass.getSimpleName();
                    return tableName;
                } catch (SecurityException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return tableName;
    }

    @Path("/where/{field}/matches/{value}")
    @GET
    @Produces({ "application/xml", "application/json" })
    public List<T> findByField(@PathParam("field") String field, @PathParam("value") String value)
    {
        //CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        //CriteriaQuery<T> critQuery = builder.createQuery(this.entityClass);
        EntityType<T> entity = getEntityManager().getMetamodel().entity(this.entityClass);
        //Root<T> root = critQuery.from(entity);
        entity.getName();
        String entityName = entity.getName();//this.getEntityTableName();
        String fieldName = entity.getAttribute(field).getName();
        //Query q = getEntityManager().createNativeQuery("SELECT * FROM "+entityName+" WHERE "+fieldName+" = ?");
        Query q = getEntityManager().createQuery("SELECT e FROM "+entityName+" e WHERE e."+fieldName+" = :value");
        //q.setParameter(1, entityName);
        //q.setParameter(2, fieldName);
        //q.setParameter(3, value);
        //q.setParameter(1, value);
        q.setParameter("value", value);
        
        List<T> results = null;
//        try
//        {
            results = q.getResultList();
//        }
//        catch (Exception e)
//        {
//            throw new WebApplicationException (e.getMessage());
//        }
        return results; 
    }

    public List<T> findAll() {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq).getResultList();
    }

    public List<T> findRange(int[] range) {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(range[1] - range[0]);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

    public int count() {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        javax.persistence.criteria.Root<T> rt = cq.from(entityClass);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

}
