/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.seqware.webservice.generated.controller;

import io.seqware.webservice.annotations.ChildEntities;
import io.seqware.webservice.annotations.ParentEntity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.EntityType;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * 
 * @author boconnor
 * @param <T>
 */
public abstract class AbstractFacade<T> {
    protected Class<T> entityClass;

    protected String entityTableName;
    private String entityIDFieldName;
    
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
    
    @Path("/updateAndReturn")
    @POST
    @Consumes({ "application/xml", "application/json" })
    @Produces({ "application/xml" })
    public T updateAndReturn(T entity)
    {
        return this.getEntityManager().merge(entity);
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
        return getEntityManager().getMetamodel().entity(this.entityClass).getName();
    }

    /**
     * Generic method to create an entity and return it.
     * @param entity
     * @return
     */
    @Path("/createAndReturn")
    @POST
    @Consumes({ "application/xml", "application/json" })
    @Produces({ "application/xml" })
    public T createAndReturn(T entity)
    {
        //save the given entity into the database
        try {
            this.getEntityManager().persist(entity);
            
            // To persist seqware attributes (and other child entities), we need to know:
            // 1) if this entity has any children, and if so, what is the accessor method.
            // 2) how to set *this* entity as the parent of the children.
            // We'll use the magic of annotations to do this!
            // In the entity classes, @ChildEntities will mark a method that
            // can return a set of child entities and @ParentEntity will be used to mark a method that can set a reference
            // to a parent. We'll search through the given entity and any children looking for these methods so we can use them.
            for (Method getChildrenMethod : entityClass.getMethods())
            {
                //We found a method that creates child entities!
                if (getChildrenMethod.isAnnotationPresent(ChildEntities.class))
                {
                    Class<?> childType = getChildrenMethod.getAnnotation(ChildEntities.class).childType();
                    for (Method setParentMethod : childType.getMethods())
                    {
                        //Look to see if there's a method that can set the parent entity.
                        //TODO: Error handling if these annotations are not properly set. Also, add some logic to break this *inner* loop 
                        //once this method is found. 
                        if (setParentMethod.isAnnotationPresent(ParentEntity.class)
                                && setParentMethod.getAnnotation(ParentEntity.class).parentType().equals(entityClass))
                        {
                            try {
                                List children = (List) getChildrenMethod.invoke(entity);
                                //TODO: It would be nice if I could use childType here instead of Object.
                                for (Object child : children)
                                {
                                    setParentMethod.invoke(child, entity);
                                    this.getEntityManager().persist(child);
                                }
                            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
                            }
                        }
                    }
                }
            }
        }
        catch (ConstraintViolationException e)
        {
            String errMsg = "Constraint violation detected: "+e.getMessage()+"; ";
            for (ConstraintViolation<?> e1 : e.getConstraintViolations()) {
                errMsg += e1.getMessage() +"; "+ e1.getPropertyPath();
            }
            Response response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(errMsg).build();
            throw new WebApplicationException(response);
        }
        return entity;
    }
    
    /**
     * Generic method to search for an entity where a specific field matches a specific value.
     * @param field
     * @param value
     * @return
     */
    @Path("/where/{field}/matches/{value}")
    @GET
    @Produces({ "application/xml", "application/json" })
    public List<T> findByField(@PathParam("field") String field, @PathParam("value") String value)
    {
        EntityType<T> entity = getEntityManager().getMetamodel().entity(this.entityClass);
        entity.getName();
        String entityName = entity.getName();
        //NOTE: if you try to access an entity via an ID of another entity (such as query "Experiment" where the studyId = something),
        //JPA will try to match the Experiment table against a Study OBJECT. This might require a new generic method that
        //matches an entity against another entity...
        String fieldName = entity.getAttribute(field).getName();

        //TODO: Look into using string-based criteria queries for this: http://docs.oracle.com/javaee/6/tutorial/doc/gkjbq.html
        Query q = getEntityManager().createQuery("SELECT e FROM "+entityName+" e WHERE e."+fieldName+" = :value");

        //If the field chosen is some kind of foreign key, we must get that other entity first. 
        if (entity.getAttribute(field).getPersistentAttributeType() != PersistentAttributeType.BASIC)
        {
            Class<?> otherEntityType = entity.getAttribute(field).getJavaType();
            //System.out.println("other entity field: "+entity.getAttribute(field).getName());
            //System.out.println("other entity type: "+otherEntityType.getName());
            //Before we can search, we need to case "value" to the correct PK type.
            Class<?> otherEntityIDType =  this.getEntityManager().getMetamodel().entity(otherEntityType).getIdType().getJavaType();
            Object valueToSearch = null;
            //If the othe entity ID field type is numeric, you MUST run the query with a Number object or you'll get a class-cast error because you can't cast
            //from String to Integer, and it seems like the JRE doesn't try to do a parseInt for you, so the getNumericValue will try
            //to parse the string into a number.
            if (Number.class.isAssignableFrom(otherEntityIDType))
            {
                valueToSearch = getNumericValue(value, otherEntityIDType);
            }
            else
            {
                valueToSearch = otherEntityIDType.cast(value);
            }
            Object otherEntity = getEntityManager().find(otherEntityType,valueToSearch);
            q.setParameter("value", otherEntity);
        }
        else
        {
            q.setParameter("value", value);
        }
        
        List<T> results = null;
        try
        {
            results = q.getResultList();
        }
        catch (Exception e)
        {
            Response response = Response.status(Status.INTERNAL_SERVER_ERROR).entity("Could not find entity by field: "+e.getMessage()).build();
            throw new WebApplicationException(response);
        }
        return results; 
    }

    private Object getNumericValue(String value, Class<?> otherEntityIDType) {
        Object numericValue = null;
        for (Method parseMethod : otherEntityIDType.getMethods())
        {
            //It would be nice it java.lang.Number had a general "parse" or "tryParse" method, instead of needing me to 
            //search for a parse* method and invoke that.
            if (parseMethod.getName().startsWith("parse"))
            {
                try {
                    numericValue = parseMethod.invoke(null, value);
                    return numericValue;
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return numericValue;
    }

    private String getEntityIDFieldName() {
        String nameFromMetamodel = this.getEntityManager().getMetamodel().entity(this.entityClass).getId(Integer.class).getName();
        return nameFromMetamodel;
    }
    
    private void checkEntityAndFieldNames() {
        // If you access non-public members of the parent class in the constructor, you get an EJB errors,
        // such as "javax.ejb.EJBException: Illegal non-business method access on no-interface view"
        // So, access to the non-public members of the parent class are now moved out of the constructor to here.
        if (this.entityTableName == null || this.entityTableName.equals("")) this.populateEntityTableName();
        if (this.entityIDFieldName == null || this.entityIDFieldName.equals("")) this.entityIDFieldName = this.getEntityIDFieldName();
    }
    
    @Path("/skip/{id}")
    @POST
    public void skip(@PathParam("id") String id) {
        // first check to see if this is a skippable entity.
        if (this.entityClass.isAnnotationPresent(io.seqware.webservice.annotations.SkippableEntity.class)) {
            this.checkEntityAndFieldNames();
            //Get the name of the skip field.
            String skipField = this.entityClass.getAnnotation(io.seqware.webservice.annotations.SkippableEntity.class).skipFieldName();
            Query updateLaneQuery = this.getEntityManager().createQuery(
                    "update " + this.entityClass.getSimpleName() + " set "+skipField+"=true where " + entityIDFieldName + "=:id");
            updateLaneQuery.setParameter("id", Integer.parseInt(id));
            int numAffected = updateLaneQuery.executeUpdate();
        } else {
            Response response = Response.status(500)
                                        .entity(new String("The entity " + this.getEntityTableName() + " is not skippable. The \"skip\" operation is not valid."))
                                        .build();
            throw new WebApplicationException(response);
        }
    }

    @Path("/unskip/{id}")
    @POST
    public void unskip(@PathParam("id") String id) {
        if (this.entityClass.isAnnotationPresent(io.seqware.webservice.annotations.SkippableEntity.class)) {
            this.checkEntityAndFieldNames();
            String skipField = this.entityClass.getAnnotation(io.seqware.webservice.annotations.SkippableEntity.class).skipFieldName();
            Query updateLaneQuery = this.getEntityManager().createQuery(
                    "update " + this.entityClass.getSimpleName() + " set "+skipField+"=false where " + entityIDFieldName + "=:id");
            updateLaneQuery.setParameter("id", Integer.parseInt(id));
            int numAffected = updateLaneQuery.executeUpdate();
        } else {
            Response response = Response.status(500)
                                        .entity(new String("The entity " + this.getEntityTableName() + " is not skippable. The \"unksip\" operation is not valid."))
                                        .build();
            throw new WebApplicationException(response);
        }
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
