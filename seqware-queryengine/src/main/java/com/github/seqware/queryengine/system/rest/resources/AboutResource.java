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

import com.github.seqware.queryengine.Constants;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.google.gson.Gson;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.jaxrs.JavaHelp;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * Describes the version, back-end, and provides other useful information about
 * the current back-end.
 *
 * @author dyuen
 */
public class AboutResource extends JavaHelp {

    public class BagOfVersionInfo {

        private String backend = SWQEFactory.getBackEnd().getClass().toString();
        private String modelManager = SWQEFactory.getModelManager().getClass().toString();
        private String serialization = SWQEFactory.getSerialization().getClass().toString();
        private String storage = SWQEFactory.getStorage().getClass().toString();
        private String version = getClass().getPackage().getImplementationVersion();
        
        private Map<String, String> constants = Constants.getSETTINGS_MAP();

        BagOfVersionInfo() {
            // no-args constructor
        }
    }

    /**
     * List of debug information
     *
     * @return list of debug information
     */
    @GET
    @Path(value = "/debug")
    @ApiOperation(value = "List information about the attached back-end", notes = "Add extra notes here")
    public Response backendRequest() {
        return Response.ok(new Gson().toJson(new BagOfVersionInfo()).toString()).header("Access-Control-Allow-Origin", "*").header("X-DAS-Status", "200").build();
    }

    /**
     * List of versions of the RESTful API this server understands and
     * information about their URIs and what methods they support.
     *
     * @return list of versions
     */
    @GET
    @Path(value = "/versions")
    @ApiOperation(value = "List compatible versions", notes = "Add extra notes here")
    public Response versionRequest() {
        return Response.ok(new Gson().toJson(getClass().getPackage().getImplementationVersion()).toString()).header("Access-Control-Allow-Origin", "*").header("X-DAS-Status", "200").build();
    }
}
