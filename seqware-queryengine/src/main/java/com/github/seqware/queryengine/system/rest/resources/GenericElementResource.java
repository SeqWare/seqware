/*
 * Copyright (C) 2012 SeqWare
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.seqware.queryengine.system.rest.resources;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.impl.ProtobufSerialization;
import com.github.seqware.queryengine.impl.protobufIO.ProtobufTransferInterface;
import com.github.seqware.queryengine.model.Atom;
import com.github.seqware.queryengine.model.Molecule;
import com.github.seqware.queryengine.model.Tag;
import com.github.seqware.queryengine.model.interfaces.ACL;
import com.github.seqware.queryengine.system.rest.exception.InvalidIDException;
import com.github.seqware.queryengine.util.SeqWareIterable;
import com.google.gson.Gson;
import com.wordnik.swagger.annotations.ApiError;
import com.wordnik.swagger.annotations.ApiErrors;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Describes the basic operations that we wish to define on elements (which may
 * or may not be found inside sets).
 *
 * @author dyuen
 */
public abstract class GenericElementResource<T extends Atom> {

    public static final int INVALID_ID = 400;
    public static final int INVALID_SET = 404;
    public static final int INVALID_INPUT = 405;
    public static final int RESOURCE_EXISTS = 406;
    public static final String QE_STATUS = "QE-Status";

    /**
     * List available resources.
     *
     * @return list of resources
     */
    @GET
    @ApiOperation(value = "List all available elements by rowkey", notes = "This lists the raw rowkeys used to uniquely identify each chain of entities.", responseClass = "com.github.seqware.queryengine.model.Atom")
    public final Response featuresRequest() {
        // Check whether the dsn contains the type of store, or not:
        //        if (!dsn.matches("^[a-zA-Z]+[0-9a-zA-Z_]*\\.[a-zA-Z]+[0-9a-zA-Z_]*\\.[a-zA-Z]+[0-9a-zA-Z_]*$"))
        //            return this.getUnsupportedOperationResponse();
        List<String> stringList = new ArrayList<String>();
        for (Atom ts : getElements()) {
            stringList.add(ts.getSGID().getRowKey());
        }
        return Response.ok(new Gson().toJson(stringList).toString())/*.header("Access-Control-Allow-Origin", "*").header("X-DAS-Status", "200")*/.build();
    }

    /**
     * Retrieve resources.
     *
     * @param sgid rowkey of the resource to be accessed
     * @return listing of resources
     */
    @GET
    @Path(value = "/{sgid}")
    @ApiOperation(value = "Find a specific element by rowkey in JSON", notes = "Add extra notes here", responseClass = "com.github.seqware.queryengine.model.Atom")
    @ApiErrors(value = {
        @ApiError(code = INVALID_ID, reason = "Invalid ID supplied"),
        @ApiError(code = INVALID_SET, reason = "set not found")})
    @Produces(MediaType.APPLICATION_JSON)
    public final Response featureByIDRequest(
            @ApiParam(value = "id of set to be fetched", required = true)
            @PathParam(value = "sgid") String sgid) throws InvalidIDException {
        // Check whether the dsn contains the type of store, or not:
        //        if (!dsn.matches("^[a-zA-Z]+[0-9a-zA-Z_]*\\.[a-zA-Z]+[0-9a-zA-Z_]*\\.[a-zA-Z]+[0-9a-zA-Z_]*$"))
        //            return this.getUnsupportedOperationResponse();
        Atom latestAtomByRowKey = SWQEFactory.getQueryInterface().getLatestAtomByRowKey(sgid, getModelClass());
        if (latestAtomByRowKey == null) {
            // A genuinely bad request:
            // (see also http://www.biodas.org/documents/spec-1.6.html#response)
            throw new InvalidIDException(INVALID_ID, "ID not found");
        }

        String toString;
        ObjectMapper mapper = new ObjectMapper();
        VisibilityChecker<?> visibilityChecker = mapper.getVisibilityChecker().withFieldVisibility(Visibility.PUBLIC_ONLY);
        mapper.setVisibilityChecker(visibilityChecker);
        try {
            toString = mapper.writeValueAsString(latestAtomByRowKey);
        } catch (IOException ex) {
            Logger.getLogger(GenericElementResource.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        return Response.ok(toString.toString())/*.header("Access-Control-Allow-Origin", "*").header("QE-Status", "200")*/.build();
    }

    /**
     * Retrieve resources tagged with a particular key on a particular element.
     *
     * @param tagset_id
     * @param tag_key
     * @return
     */
    @GET
    @Path(value = "/tags")
    @ApiOperation(value = "List available elements filtered by a tagset and tag key", notes = "Add extra notes here", responseClass = "com.github.seqware.queryengine.model.Atom")
    @ApiErrors(value = {
        @ApiError(code = INVALID_ID, reason = "Invalid ID supplied"),
        @ApiError(code = INVALID_SET, reason = "set not found")})
    public final Response taggedRequest(
            @ApiParam(value = "rowkey of tagset to restrict matches to", required = true)
            @QueryParam(value = "tagset_id") String tagset_id,
            @ApiParam(value = "key of the tag to restrict matches to", required = true)
            @QueryParam(value = "tag_key") String tag_key) {
        return Response.ok("inception".toString())/*.header("Access-Control-Allow-Origin", "*").header("QE-Status", "200")*/.build();
    }

    /**
     * Retrieve resources and list their tags.
     *
     * @param tagset_id
     * @param tag_key
     * @return
     */
    @GET
    @Path(value = "/{sgid}/tags")
    @ApiOperation(value = "Find a specific element by rowkey and list its tags ", notes = "Add extra notes here", responseClass = "com.github.seqware.queryengine.model.Atom")
    @ApiErrors(value = {
        @ApiError(code = INVALID_ID, reason = "Invalid ID supplied"),
        @ApiError(code = INVALID_SET, reason = "set not found")})
    public final Response tagsOfElementRequest(
            @ApiParam(value = "id of element to be fetched", required = true)
            @PathParam(value = "sgid") String sgid) {
        Atom latestAtomByRowKey = SWQEFactory.getQueryInterface().getLatestAtomByRowKey(sgid, getModelClass());
        if (latestAtomByRowKey == null) {
            // A genuinely bad request:
            // (see also http://www.biodas.org/documents/spec-1.6.html#response)
            return Response.status(Response.Status.BAD_REQUEST).header(QE_STATUS, INVALID_ID).build();
        }
        List<Tag> tags = new ArrayList<Tag>();
        SeqWareIterable<Tag> tagsIterator = latestAtomByRowKey.getTags();
        for (Tag t : tagsIterator) {
            tags.add(t);
        }
        ObjectMapper mapper = new ObjectMapper();
        String json = null;
        try {
            json = mapper.writeValueAsString(tags);
        } catch (IOException ex) {
            Logger.getLogger(GenericElementResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.ok(json)/*.header("Access-Control-Allow-Origin", "*").header("QE-Status", "200")*/.build();
    }

    /**
     * Retrieve resources and list their version information.
     *
     * @param tagset_id
     * @param tag_key
     * @return
     */
    @GET
    @Path(value = "/{sgid}/version")
    @ApiOperation(value = "Find a specific element by rowkey and list its version information", notes = "Add extra notes here", responseClass = "com.github.seqware.queryengine.model.Atom")
    @ApiErrors(value = {
        @ApiError(code = INVALID_ID, reason = "Invalid ID supplied"),
        @ApiError(code = INVALID_SET, reason = "set not found")})
    public final Response versioningOfElementRequest(
            @ApiParam(value = "id of element to be fetched", required = true)
            @PathParam(value = "sgid") String sgid) {

        Atom latestAtomByRowKey = SWQEFactory.getQueryInterface().getLatestAtomByRowKey(sgid, getModelClass());
        if (latestAtomByRowKey == null) {
            // A genuinely bad request:
            // (see also http://www.biodas.org/documents/spec-1.6.html#response)
            return Response.status(Response.Status.BAD_REQUEST).header(QE_STATUS, INVALID_ID).build();
        }
        if (!(latestAtomByRowKey instanceof Molecule)) {
            // A genuinely bad request:
            // (see also http://www.biodas.org/documents/spec-1.6.html#response)
            return Response.status(Response.Status.BAD_REQUEST).header(QE_STATUS, INVALID_INPUT).build();
        }
        Stack<Atom> versions = new Stack<Atom>();
        versions.add(latestAtomByRowKey);
        while (versions.peek().getPrecedingVersion() != null) {
            Atom precedingVersion = (Atom) versions.peek().getPrecedingVersion();
            versions.add(precedingVersion);
        }
        ObjectMapper mapper = new ObjectMapper();
        String json = null;
        try {
            json = mapper.writeValueAsString(versions);
        } catch (IOException ex) {
            Logger.getLogger(GenericElementResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.ok(json)/*.header("Access-Control-Allow-Origin", "*").header("QE-Status", "200")*/.build();


    }

    /**
     * Retrieve resources and list their permissions.
     *
     * @return
     */
    @GET
    @Path(value = "/{sgid}/permissions")
    @ApiOperation(value = "Find a specific element by rowkey and list its permissions ", notes = "Add extra notes here", responseClass = " com.github.seqware.queryengine.model.Atom")
    @ApiErrors(value = {
        @ApiError(code = INVALID_ID, reason = "Invalid ID supplied"),
        @ApiError(code = INVALID_SET, reason = "set not found")})
    public final Response permissionsOfElementRequest(
            @ApiParam(value = "id of element to be fetched", required = true)
            @PathParam(value = "sgid") String sgid) {
        Atom latestAtomByRowKey = SWQEFactory.getQueryInterface().getLatestAtomByRowKey(sgid, getModelClass());
        if (latestAtomByRowKey == null) {
            // A genuinely bad request:
            // (see also http://www.biodas.org/documents/spec-1.6.html#response)
            return Response.status(Response.Status.BAD_REQUEST).header(QE_STATUS, INVALID_ID).build();
        }
        if (!(latestAtomByRowKey instanceof Molecule)) {
            // A genuinely bad request:
            // (see also http://www.biodas.org/documents/spec-1.6.html#response)
            return Response.status(Response.Status.BAD_REQUEST).header(QE_STATUS, INVALID_INPUT).build();
        }

        ObjectMapper mapper = new ObjectMapper();
        String json = null;
        try {
            ACL acl = ((Molecule) latestAtomByRowKey).getPermissions();
            json = mapper.writeValueAsString(acl);
        } catch (IOException ex) {
            Logger.getLogger(GenericElementResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.ok(json)/*.header("Access-Control-Allow-Origin", "*").header("QE-Status", "200")*/.build();
    }

    /**
     * Update permissions for a particular element
     *
     * @param sgid
     * @param user
     * @return
     */
    @PUT
    @Path("/{sgid}/permissions")
    @ApiOperation(value = "Update permissions for a particular element", notes = "This can only be done by an authenticated user.")
    @ApiErrors(value = {
        @ApiError(code = INVALID_ID, reason = "Invalid element supplied"),
        @ApiError(code = INVALID_SET, reason = "Element not found")})
    public Response updateElementPermissions(
            @ApiParam(value = "rowkey that needs to be updated", required = true)
            @PathParam("sgid") String sgid) {
        // make this an overrideable method in the real version
        //userData.addUser(user);
        return Response.ok().entity("").build();
    }

    /**
     * Associate a tag with a particular element
     *
     * @param sgid
     * @param user
     * @return
     */
    @PUT
    @Path("/{sgid}/tag")
    @ApiOperation(value = "Tag an existing element", notes = "This can only be done by an authenticated user.")
    @ApiErrors(value = {
        @ApiError(code = INVALID_ID, reason = "Invalid element supplied"),
        @ApiError(code = INVALID_SET, reason = "Element not found")})
    public Response tagElement(
            @ApiParam(value = "rowkey that needs to be tagged", required = true)
            @PathParam("sgid") String sgid,
            @ApiParam(value = "rowkey of tagset to pull tag from", required = true)
            @QueryParam(value = "tagset_id") String tagset_id,
            @ApiParam(value = "tag key", required = true)
            @QueryParam(value = "key") String key,
            @ApiParam(value = "tag predicate", required = false)
            @QueryParam(value = "predicate") String predicate,
            @ApiParam(value = "tag value", required = false)
            @QueryParam(value = "value") String value) {
        // make this an overrideable method in the real version
        //userData.addUser(user);
        return Response.ok().entity("").build();
    }

    /**
     * Update an existing element.
     *
     * @param sgid
     * @param user
     * @return
     */
    @PUT
    @Path("/{sgid}")
    @ApiOperation(value = "Update an existing element", notes = "This can only be done by an authenticated user.")
    @ApiErrors(value = {
        @ApiError(code = INVALID_ID, reason = "Invalid element supplied"),
        @ApiError(code = INVALID_SET, reason = "Element not found")})
    public Response updateElement(
            @ApiParam(value = "rowkey that need to be deleted", required = true) @PathParam("sgid") String sgid,
            @ApiParam(value = "Updated user object", required = true) Atom user) {
        // make this an overrideable method in the real version
        //userData.addUser(user);
        return Response.ok().entity("").build();
    }

    /**
     * Names the class, useful for comments and debugging output
     *
     * @return
     */
    public abstract String getClassName();

    /**
     * Iterate through the elements of this type
     *
     * @return
     */
    public abstract SeqWareIterable<T> getElements();

    /**
     * Class of the elements in the set, used for casting and type-check
     * operations
     *
     * @return
     */
    public abstract Class getModelClass();

    /**
     * Proto-buffer
     *
     * @return
     */
    public ProtobufTransferInterface gettIO() {
        return ProtobufSerialization.biMap.get(getModelClass());
    }
}
