/*
 * Copyright (C) 2013 SeqWare
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
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.model.Organism;
import org.openide.util.lookup.ServiceProvider;

/**
 * Checks that the database you're pointing to (if there is one) is consistent
 * with the web service you're pointing to
 *
 * @author dyuen
 * @author Raunaq Suri
 */
@ServiceProvider(service = SanityCheckPluginInterface.class)
public class DB_WS_ConsistencyCheck implements SanityCheckPluginInterface {

    @Override
    public boolean isTutorialTest() {
        return true;
    }

    @Override
    public boolean isMasterTest() {
        return false;
    }

    @Override
    public boolean isDBTest() {
        return true;
    }

    @Override
    public boolean check(QueryRunner qRunner, Metadata metadataWS) throws SQLException {
        if (qRunner == null) {
            System.err.println("Warning: No or invalid SeqWare metadb settings");
            return true;
        }
        // create bizarro study
        UUID randomUUID = UUID.randomUUID();
        int rowsAffected = qRunner.updateQuery("insert into organism(code) VALUES ('" + randomUUID.toString() + "')");
        if (rowsAffected != 1) {
            System.err.println("Could not write to database using given database parameters");
            return false;
        }
        // try to retrieve
        List<Organism> organisms = metadataWS.getOrganisms();
        boolean found = false;
        for (Organism o : organisms) {
            if (o.getCode().equals(randomUUID.toString())) {
                found = true;
            }
        }
        if (!found) {
            System.err.println("REST web service does not match database options in your .seqware/settings");
            return false;
        }
        return true;
    }

    @Override
    public String getDescription() {
        return ".seqware database settings are present and are inconsistent with the provided web service settings";
    }

    @Override
    public int getPriority() {
        return 10;
    }
}
