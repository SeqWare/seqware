package io.seqware.webservice.controller;

import io.seqware.webservice.generated.controller.AbstractFacade;

import javax.persistence.Query;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public abstract class SkippableEntityFacade<T> extends AbstractFacade<T> {

    private String entityIDFieldName;

    private String getEntityIDFieldName() {
        String nameFromMetamodel = this.getEntityManager().getMetamodel().entity(this.entityClass).getId(Integer.class).getName();
        return nameFromMetamodel;
    }

    public SkippableEntityFacade(Class<T> entityClass) {
        super(entityClass);
        // this.entityTableName = this.getEntityTableName();
        // this.populateEntityTableName();
        // this.entityIDFieldName = this.getEntityIDFieldName();
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
}
