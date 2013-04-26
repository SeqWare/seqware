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
package net.sourceforge.seqware.pipeline.tutorial;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Assert;

/**
 *
 * @author seqware
 */
public class TutorialSuite {
    protected static Pattern processingPattern = Pattern.compile("MetaDB ProcessingAccession for this run is: ([\\d]+)");
    protected static Pattern swidPattern = Pattern.compile("SWID: ([\\d]+)");

    public static String getAndCheckProcessingAccession(String s) throws NumberFormatException {
        Matcher match = processingPattern.matcher(s);
        Assert.assertTrue("ProcessingAccession not found in output.", match.find());
        String swid = match.group(1);
        Assert.assertFalse("The ProcessingAccession was empty", swid.trim().isEmpty());
        Integer.parseInt(swid.trim());
        return swid;
    }

    public static String getAndCheckSwid(String s) throws NumberFormatException {
        Matcher match = swidPattern.matcher(s);
        Assert.assertTrue("SWID not found in output.", match.find());
        String swid = match.group(1);
        Assert.assertFalse("The SWID was empty", swid.trim().isEmpty());
        Integer.parseInt(swid.trim());
        return swid;
    }
    
}
