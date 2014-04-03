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
import net.sourceforge.seqware.common.metadata.Metadata;
import org.openide.util.lookup.ServiceProvider;

/**
 * Checks that the database you're pointing to (if there is one) is consistent
 * with the web service you're pointing to
 *
 * @author dyuen
 * @author Raunaq Suri
 */
@ServiceProvider(service = SanityCheckPluginInterface.class)
public class WS_Check implements SanityCheckPluginInterface {

    @Override
    public boolean isTutorialTest() {
        return false;
    }

    @Override
    public boolean isMasterTest() {
        return false;
    }

    @Override
    public boolean isDBTest() {
        return false;
    }

    @Override
    public boolean check(QueryRunner qRunner, Metadata metadataWS) throws SQLException {
        metadataWS.getOrganisms();
        return true;
    }

    @Override
    public String getDescription() {
        return ".seqware web service settings do not point to a working web service";
    }

    @Override
    public int getPriority() {
        return 5;
    }
}
