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

import com.wordnik.swagger.jaxrs.ConfigReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;

/**
 * See http://www.tumblr.com/tagged/api?before=1327364346 .
 *
 * @author dyuen
 */
public class QueryEngineServiceConfigReader extends ConfigReader {

    public QueryEngineServiceConfigReader(ServletConfig sc) {
        super(sc);
    }

    @Override
    public final String getBasePath() {
        // some fancy logic to determine base path
        try {
            Socket s = new Socket("www.google.com", 80);
            // TODO: this path has a hard-coded port and path, we should somehow detect this 
            String result = "http://" + s.getLocalAddress().getHostAddress() + ":8080/seqware-queryengine/api";
            s.close();
            return result;
        } catch (IOException ex) {
            Logger.getLogger(QueryEngineServiceConfigReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
