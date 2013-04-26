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
import com.github.seqware.queryengine.model.PluginRun;
import com.github.seqware.queryengine.util.SeqWareIterable;
import com.wordnik.swagger.annotations.Api;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * PluginRun resource.
 *
 * @author dyuen
 */
@Path("/pluginrun")
@Api(value = "/pluginrun", description = "Operations about pluginrun", listingPath="/resources/pluginrun")
@Produces({"application/json"})
public class PluginRunResource extends GenericElementResource<PluginRun> {

    @Override
    public final String getClassName() {
        return "PluginRun";
    }

    @Override
    public final Class getModelClass() {
        return PluginRun.class;
    }
    
    @Override
    public final SeqWareIterable getElements() {
        return SWQEFactory.getQueryInterface().getPluginRuns();
    }
}