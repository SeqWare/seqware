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
package org.apache.commons.cli;

import java.util.ListIterator;

/**
 * Workaround for bug with Apache CLI https://issues.apache.org/jira/browse/CLI-185
 * This can be removed when CLI 1.3 is released
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class HackedPosixParser extends PosixParser {
    
    /** {@inheritDoc} */
    @Override
        public void processArgs(Option opt, ListIterator iter)
        throws ParseException
    {
        // loop until an option is found
        while (iter.hasNext())
        {
            String str = (String) iter.next();

            // found an Option
            if (super.getOptions().hasOption(str))
            {
                iter.previous();

                break;
            }

            // found a value
            else
            {

                try
                {
                    opt.addValueForProcessing( (str) );
                }
                catch (RuntimeException exp)
                {
                    iter.previous();

                    break;
                }
            }
        }

        if ((opt.getValues() == null) && !opt.hasOptionalArg())
        {
            throw new MissingArgumentException("no argument for:"
                                               + opt.getKey());
        }
    }
}
