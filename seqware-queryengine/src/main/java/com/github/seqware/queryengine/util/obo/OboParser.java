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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.biojava.bio.BioError;
import org.biojava.bio.seq.io.ParseException;
import org.biojava.ontology.AlreadyExistsException;
import org.biojava.ontology.OntoTools;
import org.biojava.ontology.Ontology;
import org.biojava.ontology.OntologyException;
import org.biojava.ontology.OntologyFactory;
import org.biojava.ontology.Synonym;
import org.biojava.ontology.Term;
import org.biojava.ontology.obo.OboFileParser;
import org.biojava.utils.ChangeVetoException;

/**
 * Copied from BioJava 1.8.1 Legacy org.biojava.ontology.io.OboParser
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class OboParser {
    private OboFileHandlerHack handler;

    /**
     * Parse a OBO file and return its content as a BioJava Ontology object
     *
     * @param oboFile the file to be parsed
     * @param ontoName a {@link java.lang.String} object.
     * @param ontoDescription a {@link java.lang.String} object.
     * @return the ontology represented as a BioJava ontology file
     * @throws org.biojava.bio.seq.io.ParseException if any.
     * @throws java.io.IOException if any.
     */
    public Ontology parseOBO(
            BufferedReader oboFile,
            String ontoName,
            String ontoDescription)
            throws ParseException, IOException {

        try {
            OntologyFactory factory = OntoTools.getDefaultFactory();
            Ontology ontology = factory.createOntology(ontoName, ontoDescription);

            OboFileParser parser = new OboFileParser();

            this.handler = new OboFileHandlerHack(ontology);

            parser.addOboFileEventListener(handler);
            parser.parseOBO(oboFile);

            return ontology;


        } catch (AlreadyExistsException ex) {
            throw new ParseException(ex, "Duplication in ontology");
        } catch (OntologyException ex) {
            throw new ParseException(ex);
        } catch (ChangeVetoException ex) {
            throw new BioError("Error accessing newly created ontology", ex);
        }

    }
    
    /**
     * Hacked map of terms to synonyms
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<Term, List<Synonym>> getSynonymMap(){
        return this.handler.getMap();
    }
}
