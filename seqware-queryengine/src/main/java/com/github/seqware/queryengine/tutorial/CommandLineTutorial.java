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
package com.github.seqware.queryengine.tutorial;

import org.apache.commons.cli.*;

/**
 *
 * @author dyuen
 */
public class CommandLineTutorial {

    public static void main(String[] args) throws ParseException {
        // create Options object
        Options options = new Options();
        Option option1 = OptionBuilder.withArgName("worker").withDescription("(required) the work module and thus the type of file we are working with").isRequired().create('w');
        options.addOption(option1);
        Option option2 = OptionBuilder.withArgName("threads").withDescription("(optional: default 1) the number of threads to use in our import").hasArgs(1).create('t');
        options.addOption(option2);
        Option option3 = OptionBuilder.withArgName("compressed").withDescription("(optional) whether we are working with compressed input").create('c');
        options.addOption(option3);
        Option option4 = OptionBuilder.withArgName("reference").withDescription("(required) the reference name to attach our FeatureSet to").isRequired().hasArgs(1).create('r');
        options.addOption(option4);
        Option option5 = OptionBuilder.withArgName("inputFile").withDescription("(required) comma separated input files").hasArgs().withValueSeparator(',').isRequired().create('w');
        options.addOption(option5);
        Option option6 = OptionBuilder.withArgName("outputFile").withDescription("(optional) output file with our resulting key values").hasArgs(1).create('o');
        options.addOption(option6);
        Option option7 = OptionBuilder.withArgName("tagSpec").withDescription("(optional) tag specification IDs, new tags will be linked in in the first set that they appear (or in an ad hoc set if they do not)").hasArgs().create('s');
        options.addOption(option7);
        
        try {
            CommandLineParser parser = new PosixParser();
            CommandLine cmd = parser.parse(options, args);
            System.out.println(cmd.toString());
            System.out.println(options.toString());
            cmd.getArgs();
        } catch (MissingOptionException e) {
            // automatically generate the help statement
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("FeatureImporter", options);
        } catch (ParseException e) {
            // automatically generate the help statement
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("FeatureImporter", options);
        }

    }
}
