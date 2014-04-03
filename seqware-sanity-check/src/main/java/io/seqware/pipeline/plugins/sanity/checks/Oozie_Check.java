/*
 * Copyright (C) 2014 SeqWare
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
package io.seqware.pipeline.plugins.sanity.checks;

import io.seqware.pipeline.plugins.sanity.QueryRunner;
import io.seqware.pipeline.plugins.sanity.SanityCheckPluginInterface;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Raunaq Suri
 */
@ServiceProvider(service = SanityCheckPluginInterface.class)

public class Oozie_Check implements SanityCheckPluginInterface {

    @Override
    public boolean isTutorialTest() {
        return false;
    }

    @Override
    public boolean isMasterTest() {
        return true;
    }

    @Override
    public boolean isDBTest() {
        return false;
    }

    @Override
    public boolean check(QueryRunner qRunner, Metadata metadataWS) throws SQLException {
        HashMap<String, String> settings = (HashMap<String, String>) ConfigTools.getSettings();
        if (settings.isEmpty()) {
            return false;
        } else if (!settings.containsKey("OOZIE_URL")) {
            return false;
        }
        try {
            //Attempts to connect to the oozie webservice
            URL url = new URL(settings.get("OOZIE_URL"));
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            if (http.getResponseCode() == 200) {
                return true;
            } else {
                System.err.println("Response code received from url is not 200. Please check configuration");
                return false;
            }

        } catch (IOException ex) {

            System.err.println("Error connecting to oozie url: " + ex.getMessage());
            return false;
        }
    }

    @Override
    public String getDescription() {
        return "Oozie is not running";
    }

    @Override
    public int getPriority() {
        return 10;
    }
}
