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
package com.github.seqware.queryengine.system.rest;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.wordnik.swagger.annotations.Api;
import java.io.IOException;
import java.net.URI;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import com.wordnik.swagger.jaxrs.JavaApiListing;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;



/**
 * Mimic PingaService from
 * https://github.com/joejimbo/genobyte/blob/master/genobyte-pinga/src/main/java/org/obiba/pinga/PingaService.java
 * to demo web services.
 *
 * @author dyuen
 */
@Path("/resources")
@Api("/resources")
@Produces({"application/json"})
public class QueryEngineService extends JavaApiListing {
    
    public QueryEngineService(){
        //super("com.github.seqware.queryengine.system.rest.resources");
    }

    private static URI getBaseURI() {
        return UriBuilder.fromUri("http://localhost/").port(8080).build();
    }
    
    public static final URI BASE_URI = getBaseURI();

    public static HttpServer startService(String[] args) throws IOException {
        System.out.println("Starting grizzly...");
        ResourceConfig rc = new PackagesResourceConfig("com.github.seqware.queryengine.system.rest");
        return GrizzlyServerFactory.createHttpServer(BASE_URI,  rc);
    }

    public static void main(String[] args) throws IOException {
        HttpServer httpServer = QueryEngineService.startService(args);
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nTry out %stagset\nHit enter to stop it...",
                BASE_URI, BASE_URI));
        System.in.read();
        httpServer.stop();

    }
}
