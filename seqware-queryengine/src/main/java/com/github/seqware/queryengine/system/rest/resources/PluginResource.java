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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.model.Atom;
import com.github.seqware.queryengine.model.Plugin;
import com.github.seqware.queryengine.system.rest.exception.InvalidIDException;
import com.github.seqware.queryengine.util.SeqWareIterable;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiError;
import com.wordnik.swagger.annotations.ApiErrors;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Plugin resource.
 *
 * @author dyuen
 */
@Path("/plugin")
@Api(value = "/plugin", description = "Operations about plugins", listingPath="/resources/plugin")
@Produces({"application/json"})
public class PluginResource extends GenericSetResource<Plugin> {

    @Override
    public final String getClassName() {
        return "Plugin";
    }

    @Override
    public final Class getModelClass() {
        return Plugin.class;
    }
    
    @Override
    public final SeqWareIterable getElements() {
        return SWQEFactory.getQueryInterface().getPlugins();
    }
    
    /**
     * Upload a jar file to create a new plugin
     * @return 
     */
    @POST
    @ApiOperation(value = "Create new plugin from jarfile", notes = "This can only be done by an authenticated user.")
    @ApiErrors(value = {
        @ApiError(code = RESOURCE_EXISTS, reason = "Resource already exists")})
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public Response uploadOBO(
            @ApiParam(value = "rowkey that needs to be updated", required = false) 
            @QueryParam("sgid") String sgid
            ) {
        // make this an overrideable method in the real version
        //userData.addUser(user);
        return Response.ok().entity("").build();
    }
    
    /**
     * Retrieve resources.
     *
     * @param sgid rowkey of the plugin to be run
     * @return listing of resources
     */
    @POST
    @Path(value = "/{sgid}/run")
    @ApiOperation(value = "Run a specific plugin by rowkey with JSON parameters", notes = "Add extra notes here", responseClass = "com.github.seqware.queryengine.model.Atom")
    @ApiErrors(value = {
        @ApiError(code = INVALID_ID, reason = "Invalid ID supplied"),
        @ApiError(code = INVALID_SET, reason = "set not found")})
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public final Response runPlugin(
            @ApiParam(value = "id of plugin to run", required = true)
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

        return Response.ok("ok".toString())/*.header("Access-Control-Allow-Origin", "*").header("QE-Status", "200")*/.build();
    }
    
}