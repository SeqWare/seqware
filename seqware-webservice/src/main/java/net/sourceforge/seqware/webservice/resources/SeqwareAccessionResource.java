/*
 * Copyright (C) 2011 SeqWare
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
package net.sourceforge.seqware.webservice.resources;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

/**
 * <p>SeqwareAccessionResource class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class SeqwareAccessionResource extends BasicResource {

    /** {@inheritDoc} */
    @Get
    @Override
    public Representation get() {
        String output = "All SWAs";
        StringRepresentation repOutput = new StringRepresentation(output);
        repOutput.setMediaType(MediaType.TEXT_PLAIN);
        return repOutput;
    }

}
