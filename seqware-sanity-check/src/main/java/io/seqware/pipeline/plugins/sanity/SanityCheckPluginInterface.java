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
package io.seqware.pipeline.plugins.sanity;

import java.sql.SQLException;
import net.sourceforge.seqware.common.metadata.Metadata;

/**
 * Specifies an interface for checks that can be done for a SeqWare install
 *
 * @author dyuen
 * @author Raunaq Suri
 */
public interface SanityCheckPluginInterface {

    public boolean isMasterTest();

    public boolean isTutorialTest();

    public boolean isDBTest();

    /**
     *
     * @param qRunner a runner constructed using your database options, may not
     * be present for non-admins
     * @param metadataWS a metadata interface constructed using the web service
     * interface
     * @return whether the check succeeded
     * @throws SQLException
     */
    public boolean check(QueryRunner qRunner, Metadata metadataWS) throws SQLException;

    /**
     * Describes the check and indicates what may be wrong
     *
     * @return
     */
    public String getDescription();

    /**
     * Indicate the priority of the check, higher is later
     *
     * @return
     */
    public int getPriority();
}
