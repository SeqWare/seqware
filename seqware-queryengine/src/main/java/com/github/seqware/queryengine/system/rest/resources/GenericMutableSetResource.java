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

import com.github.seqware.queryengine.model.Atom;
import com.wordnik.swagger.annotations.ApiError;
import com.wordnik.swagger.annotations.ApiErrors;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 *
 * @author dyuen
 */
public abstract class GenericMutableSetResource<T extends Atom> extends GenericSetResource<T> {
    
    /**
     * Create a totally new element inside the set given a specification without an associated
     * ID.
     *
     * @param element
     * @return
     */
    @POST
    @Path("/{sgid}")
    @ApiOperation(value = "Create an element in the set" , notes = "This can only be done by an authenticated user.")
    @ApiErrors(value = {
        @ApiError(code = INVALID_INPUT, reason = "Invalid input")})
    public final Response addElement(
            @ApiParam(value = "set to add an element to", required = true) 
            @PathParam("sgid") String sgid,
            @ApiParam(value = "element that needs to be added to the store", required = true) Atom element) {
        // make this an overrideable method in the real version
        //petData.addPet(pet);
        return Response.ok().entity("SUCCESS").build();
    }
}
