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
import com.github.seqware.queryengine.model.Group;
import com.github.seqware.queryengine.util.SeqWareIterable;
import com.wordnik.swagger.annotations.Api;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * Group resource.
 *
 * @author dyuen
 */
@Path("/group")
@Api(value = "/group", description = "Operations about groups", listingPath="/resources/group")
@Produces({"application/json"})
public class GroupResource extends GenericMutableSetResource<Group> {

    @Override
    public final String getClassName() {
        return "Group";
    }

    @Override
    public final Class getModelClass() {
        return Group.class;
    }
    
    @Override
    public final SeqWareIterable getElements() {
        return SWQEFactory.getQueryInterface().getGroups();
    }
}