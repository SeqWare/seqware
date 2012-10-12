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
package com.github.seqware.queryengine.util.obo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.biojava.ontology.Ontology;
import org.biojava.ontology.Synonym;
import org.biojava.ontology.Term;
import org.biojava.ontology.obo.OboFileHandler;

/**
 * The BioJava 1.8.1 package does not seem to handle Synonyms correctly, need to
 * keep track of them separately
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class OboFileHandlerHack extends OboFileHandler {

    private Map<Term, List<Synonym>> map = new HashMap<Term, List<Synonym>>();
    private Field privateTermField = null;
    private Field isTermField = null;

    /**
     * <p>Constructor for OboFileHandlerHack.</p>
     *
     * @param ontology a {@link org.biojava.ontology.Ontology} object.
     */
    public OboFileHandlerHack(Ontology ontology) {
        super(ontology);
        try {
            this.privateTermField = OboFileHandler.class.getDeclaredField("currentTerm");
            privateTermField.setAccessible(true);
            this.isTermField = OboFileHandler.class.getDeclaredField("isTerm");
            isTermField.setAccessible(true);
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(OboFileHandlerHack.class.getName()).fatal(null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(OboFileHandlerHack.class.getName()).fatal(null, ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void newSynonym(Synonym synonym) {
        try {
            Term currentTerm = (Term) privateTermField.get(this);
            boolean isTerm = (Boolean) isTermField.get(this);

            if (!map.containsKey(currentTerm)) {
                map.put(currentTerm, new ArrayList<Synonym>());
            }
            if (isTerm) {
                map.get(currentTerm).add(synonym);
            }
            currentTerm.addSynonym(synonym);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(OboFileHandlerHack.class.getName()).fatal(null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(OboFileHandlerHack.class.getName()).fatal(null, ex);
        }
    }

    /**
     * <p>Getter for the field <code>map</code>.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<Term, List<Synonym>> getMap() {
        return map;
    }
    
    
}
