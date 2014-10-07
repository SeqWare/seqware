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
package net.sourceforge.seqware.common.model.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 * <p>
 * XmlizeXML class.
 * </p>
 * 
 * @author mtaschuk
 * @version $Id: $Id
 */
public class XmlizeXML extends XmlAdapter<String, String> {

    /**
     * {@inheritDoc}
     * 
     * @return
     * @throws java.lang.Exception
     */
    @Override
    public String unmarshal(String vt) throws Exception {
        // String out = vt.replace("&amp;", "&");
        String out = StringEscapeUtils.unescapeXml(vt);
        out = StringEscapeUtils.escapeEcmaScript(out);

        return out;
    }

    /**
     * {@inheritDoc}
     * 
     * @return
     * @throws java.lang.Exception
     */
    @Override
    public String marshal(String bt) throws Exception {
        String out = StringEscapeUtils.escapeXml(bt);
        out = StringEscapeUtils.escapeEcmaScript(out);
        return out;
    }
}
