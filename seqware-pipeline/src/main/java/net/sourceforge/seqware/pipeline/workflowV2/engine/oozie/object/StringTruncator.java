/*
 * Copyright (C) 2015 SeqWare
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
package net.sourceforge.seqware.pipeline.workflowV2.engine.oozie.object;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/**
 * This class enforces names for the Oozie workflow engine that are less than or equal to 50 characters but are still unique.
 *
 * This class keeps track of names so it should be instantiated once per workflow.
 *
 * @author dyuen
 */
public class StringTruncator {

    public static final int ROOT_CHAR_COUNT = 45;
    public static final int DUPLICATION_LIMIT = 9999;

    private final Map<String, Integer> rootUsed = new HashMap<>();

    /**
     * Translates a long name to a shortname that is compatible with Oozie, yet still unique.
     * 
     * @param longName
     * @return
     */
    public String translateName(String longName) {
        if (longName.length() < ROOT_CHAR_COUNT) {
            return longName;
        }
        String root = StringUtils.left(longName, ROOT_CHAR_COUNT);
        int count;
        if (rootUsed.containsKey(root)) {
            // already used, need to count up
            count = rootUsed.get(root) + 1;
        } else {
            count = 1;
        }
        rootUsed.put(root, count);
        if (count > DUPLICATION_LIMIT) {
            throw new RuntimeException("string truncation only supports up to " + DUPLICATION_LIMIT + " duplicates");
        }
        return root + "-" + count;
    }

}
