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

import com.github.seqware.queryengine.dto.QueryEngine.TagSetPB;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.impl.protobufIO.TagSetIO;
import com.github.seqware.queryengine.model.TagSet;
import com.wordnik.swagger.jaxrs.JavaHelp;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * TagSet resource
 *
 * @author dyuen
 */
@Path("/tagset")
public class TagSetResource {// extends JavaHelp{

    /**
     * List available tagsets
     *
     * @param sgid rowkey of the tagset to be accessed
     * @return list of tags within this tagset
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response featuresRequest() {
        // Check whether the dsn contains the type of store, or not:
        //        if (!dsn.matches("^[a-zA-Z]+[0-9a-zA-Z_]*\\.[a-zA-Z]+[0-9a-zA-Z_]*\\.[a-zA-Z]+[0-9a-zA-Z_]*$"))
        //            return this.getUnsupportedOperationResponse();
        StringBuilder response = new StringBuilder();
        for (TagSet ts : SWQEFactory.getQueryInterface().getTagSets()) {
            response.append(ts.getSGID().getRowKey());
            response.append("\n");
        }

        return Response.ok(//"<?xml version=\"1.0\" standalone=\"no\"?>\n" +
                response.toString()).header("Access-Control-Allow-Origin", "*").header("X-DAS-Status", "200").build();
    }

    /**
     * Retrieve tags from a tagset
     *
     * @param sgid rowkey of the tagset to be accessed
     * @return list of tags within this tagset
     */
    @GET
    @Path("{sgid}/tags")
    @Produces(MediaType.TEXT_PLAIN)
    public Response featuresRequest(@PathParam("sgid") String sgid) {
        // Check whether the dsn contains the type of store, or not:
        //        if (!dsn.matches("^[a-zA-Z]+[0-9a-zA-Z_]*\\.[a-zA-Z]+[0-9a-zA-Z_]*\\.[a-zA-Z]+[0-9a-zA-Z_]*$"))
        //            return this.getUnsupportedOperationResponse();
        TagSet latestAtomByRowKey = SWQEFactory.getQueryInterface().getLatestAtomByRowKey(sgid, TagSet.class);
        if (latestAtomByRowKey == null) {
            // A genuinely bad request:
            // (see also http://www.biodas.org/documents/spec-1.6.html#response)
            return Response.status(Response.Status.BAD_REQUEST).header("QE-Status", "400").build();
        }

        TagSetIO tIO = new TagSetIO();
        TagSetPB m2pb = tIO.m2pb(latestAtomByRowKey);
        String toString = m2pb.toString();

        return Response.ok(//"<?xml version=\"1.0\" standalone=\"no\"?>\n" +
                toString.toString()).header("Access-Control-Allow-Origin", "*").header("QE-Status", "200").build();

    }
}
