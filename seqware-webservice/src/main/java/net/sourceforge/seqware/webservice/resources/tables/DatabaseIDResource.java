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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.sourceforge.seqware.common.model.Attribute;
import net.sourceforge.seqware.webservice.resources.BasicResource;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Put;

/**
 * <p>DatabaseIDResource class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class DatabaseIDResource extends BasicResource {

    private int id;

    /**
     * <p>Getter for the field <code>attribute</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getAttribute() {
        return attribute;
    }

    /**
     * <p>Getter for the field <code>id</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public int getId() {
        return id;
    }
    protected String attribute;

    /**
     * <p>Constructor for DatabaseIDResource.</p>
     *
     * @param attributeName a {@link java.lang.String} object.
     */
    public DatabaseIDResource(String attributeName) {
        this.attribute = attributeName;
    }

    /** {@inheritDoc} */
    @Override
    public void doInit() {
        super.doInit();
        this.id = parseClientInt((String) getRequestAttributes().get(attribute));
        attribute += " " + id;
    }

//    @Get
//    @Override
//    public Representation get() {
//        String output = attribute;
//        StringRepresentation repOutput = new StringRepresentation(output);
//        repOutput.setMediaType(MediaType.TEXT_PLAIN);
//        return repOutput;
//    }

    /** {@inheritDoc} */
    @Put
    @Override
    public Representation put(Representation rep) {
        StringRepresentation repOutput = new StringRepresentation("Updating " + attribute);
        repOutput.setMediaType(MediaType.TEXT_PLAIN);

        return repOutput;
    }

    /** {@inheritDoc} */
    @Delete
    @Override
    public Representation delete() {
        StringRepresentation repOutput = new StringRepresentation("Deleting " + attribute);
        repOutput.setMediaType(MediaType.TEXT_PLAIN);

        return repOutput;
    }

    /**
     * Merge attributes from a new set into an existing one while removing duplicates.
     * Unfortunately, due to how duplicates work,
     * @param newAttributeSet
     * @return
     */
    protected <S, T extends Attribute> void mergeAttributes(Set<T> existingAttributeSet, Set<T> newAttributeSet, S parent) {
        // extract keys
        Map<String, T> keyMap = new HashMap<String, T>();
        for (T exist : existingAttributeSet) {
            keyMap.put(exist.getTag(), exist);
        }
        // add new attributes, but remove existing ones if there is a duplicate
        for (T newAttr : newAttributeSet) {
            if (keyMap.containsKey(newAttr.getTag())) {
                T oldDuplicate = keyMap.get(newAttr.getTag());
                keyMap.remove(newAttr.getTag());
                existingAttributeSet.remove(oldDuplicate);
            }
            keyMap.put(newAttr.getTag(), newAttr);
            // populate the parent end of the relationship
            existingAttributeSet.add(newAttr);
            // populate the child end of the relationship
            newAttr.setAttributeParent(parent);
        }
    }  
}
