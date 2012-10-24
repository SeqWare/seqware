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
package net.sourceforge.seqware.webservice.resources.tables;

import java.io.IOException;
import net.sourceforge.seqware.common.business.RegistrationService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.webservice.resources.BasicResource;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

/**
 * <p>DatabaseResource class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class DatabaseResource extends BasicResource {

    protected String table;
    

    /**
     * <p>Constructor for DatabaseResource.</p>
     *
     * @param table a {@link java.lang.String} object.
     */
    public DatabaseResource(String table) {
        this.table = table;
    }



//    @Get
//    @Override
//    public Representation get() {
//        String output = "Get All " + table;
//        StringRepresentation repOutput = new StringRepresentation(output);
//        repOutput.setMediaType(MediaType.TEXT_PLAIN);
//        return repOutput;
//    }

//    @Post
//    @Override
//    public Representation post(Representation entity) {
//        try {
//            String output = "Post " + table + " " + entity.getText();
//            StringRepresentation repOutput = new StringRepresentation(output);
//            repOutput.setMediaType(MediaType.TEXT_PLAIN);
//            return repOutput;
//        } catch (IOException e) {
//            getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
//            return null;
//        }
//    }


}
