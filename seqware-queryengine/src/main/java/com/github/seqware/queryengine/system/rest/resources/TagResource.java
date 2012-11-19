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
import com.github.seqware.queryengine.model.Tag;
import com.github.seqware.queryengine.model.User;
import com.github.seqware.queryengine.util.SeqWareIterable;
import com.wordnik.swagger.annotations.Api;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * Tag resource.
 *
 * @author dyuen
 */
@Path("/tag")
@Api(value = "/tag", description = "Operations about tags", listingPath="/resources/tag")
@Produces({"application/json"})
public class TagResource extends GenericElementResource<User> {

    @Override
    public final String getClassName() {
        return "Tag";
    }

    @Override
    public final Class getModelClass() {
        return Tag.class;
    }
    
    @Override
    public final SeqWareIterable getElements() {
        return SWQEFactory.getQueryInterface().getTags();
    }
}