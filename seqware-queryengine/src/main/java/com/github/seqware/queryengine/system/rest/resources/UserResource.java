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
import com.github.seqware.queryengine.model.User;
import com.github.seqware.queryengine.util.SeqWareIterable;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * User resource.
 *
 * @author dyuen
 */
@Path("/user")
@Api(value = "/user", description = "Operations about users"/*, listingPath="/resources.json/referenceset"*/)
@Produces({"application/json"})
public class UserResource extends GenericElementResource<User> {

    @Override
    public final String getClassName() {
        return "User";
    }

    @Override
    public final Class getModelClass() {
        return User.class;
    }
    
    @Override
    public final SeqWareIterable getElements() {
        return SWQEFactory.getQueryInterface().getUsers();
    }

    
    /**
     * Authenticate a User and retrieve an authentication token
     * @param username
     * @param password
     * @return 
     */
    @GET
    @Path(value = "/authenticate")
    @ApiOperation(value = "Authenticate a User and retrieve an authentication token", notes = "Add extra notes here", responseClass=" com.github.seqware.queryengine.model.User")
    public final Response getAuthentication(
            @ApiParam(value = "The user's name", required = true)
            @QueryParam(value = "username") String username,
            @ApiParam(value = "The user's password", required = true)
            @QueryParam(value = "password") String password) {
        return Response.ok("inception".toString()).header("Access-Control-Allow-Origin", "*").header("QE-Status", "200").build();
    }
    
}