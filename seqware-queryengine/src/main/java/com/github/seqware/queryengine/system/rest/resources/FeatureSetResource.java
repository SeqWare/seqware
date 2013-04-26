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

import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.util.SeqWareIterable;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiError;
import com.wordnik.swagger.annotations.ApiErrors;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * FeatureSet resource.
 *
 * @author dyuen
 */
@Path("/featureset")
@Api(value = "/featureset", description = "Operations about featuresets", listingPath = "/resources/featureset")
@Produces({"application/json"})
public class FeatureSetResource extends GenericSetResource<FeatureSet> {

    @Override
    public final String getClassName() {
        return "FeatureSet";
    }

    @Override
    public final Class getModelClass() {
        return FeatureSet.class;
    }

    @Override
    public final SeqWareIterable getElements() {
        return SWQEFactory.getQueryInterface().getFeatureSets();
    }

    /**
     * Create new pluginrun event to create a query, monitor the query, and
     * return a new feature set when ready.
     *
     * @param sgid rowkey of featureset to operate on
     * @param query query in our query language
     * @param ttl time in hours for the results to live
     * @return
     */
    @POST
    @Path("/{sgid}/query")
    @ApiOperation(value = "Create new pluginrun event to monitor query", notes = "This can only be done by an authenticated user.")
    @ApiErrors(value = {
        @ApiError(code = INVALID_ID, reason = "Invalid element supplied"),
        @ApiError(code = INVALID_SET, reason = "Element not found")})
    public Response runQuery(
            @ApiParam(value = "rowkey that needs to be updated", required = true)
            @PathParam("sgid") String sgid,
            @ApiParam(value = "query", required = true)
            @QueryParam(value = "query") String query,
            @ApiParam(value = "ttl", required = false)
            @QueryParam(value = "ttl") int ttl) {
        // make this an overrideable method in the real version
        //userData.addUser(user);
        return Response.ok().entity("").build();
    }

    /**
     * Return the features that belong to the specified feature set in
     * VCF
     *
     * @param sgid rowkey of featureset to operate on
     * @return
     */
    @GET
    @Path("/{sgid}")
    @ApiOperation(value = "List features in a featureset in VCF", notes = "This can only be done by an authenticated user.")
    @ApiErrors(value = {
        @ApiError(code = INVALID_ID, reason = "Invalid element supplied"),
        @ApiError(code = INVALID_SET, reason = "Element not found")})
    @Produces(MediaType.TEXT_PLAIN)
    public Response getVCFFeatureListing(
            @ApiParam(value = "rowkey that needs to be updated", required = true)
            @PathParam("sgid") String sgid) {
        // make this an overrideable method in the real version
        //userData.addUser(user);
        return Response.ok().entity("").build();
    }
    
    /**
     * Return a specific feature in JSON
     *
     * @param sgid rowkey of featureset to operate on
     * @return
     */
    @GET
    @Path("/{sgid}/{fsgid}")
    @ApiOperation(value = "Get a specific feature in a featureset in JSON", notes = "This can only be done by an authenticated user.")
    @ApiErrors(value = {
        @ApiError(code = INVALID_ID, reason = "Invalid element supplied"),
        @ApiError(code = INVALID_SET, reason = "Element not found")})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJSONFeature(
            @ApiParam(value = "rowkey of featureset to find feature in", required = true)
            @PathParam("sgid") String sgid,
            @ApiParam(value = "rowkey of feature", required = true)
            @QueryParam(value = "sgid") String fsgid) {
        // make this an overrideable method in the real version
        //userData.addUser(user);
        return Response.ok().entity("").build();
    }

    /**
     * Upload a raw variant file to create a new featureset
     *
     * @param sgid rowkey of ontology to create
     * @return
     */
    @POST
    @ApiOperation(value = "Create a new featureset with a raw data file", notes = "This can only be done by an authenticated user.")
    @ApiErrors(value = {
        @ApiError(code = RESOURCE_EXISTS, reason = "Resource already exists")})
    @Consumes(MediaType.TEXT_PLAIN)
    public Response uploadRawVCFfile(
            @ApiParam(value = "tagset rowkey that needs to be updated", required = false)
            @PathParam("sgid") String sgid,
            @ApiParam(value = "format of input", required = true, allowableValues = "VCF,GFF3,GVF")
            @DefaultValue(value = "VCF")
            @QueryParam(value = "format") String format) {
        // make this an overrideable method in the real version
        //userData.addUser(user);
        return Response.ok().entity("").build();
    }
}
