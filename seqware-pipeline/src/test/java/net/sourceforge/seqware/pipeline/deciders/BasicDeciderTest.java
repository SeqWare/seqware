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
package net.sourceforge.seqware.pipeline.deciders;

import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author dyuen
 */
public class BasicDeciderTest {

    @Test
    public void testPositiveRerunConditions() {
        Assert.assertTrue("should have rerun", BasicDecider.isDoRerun(BasicDecider.FILE_STATUS.DISJOINT_SETS, BasicDecider.PREVIOUS_RUN_STATUS.COMPLETED));
        Assert.assertTrue("should have rerun", BasicDecider.isDoRerun(BasicDecider.FILE_STATUS.DISJOINT_SETS, BasicDecider.PREVIOUS_RUN_STATUS.OTHER));
        Assert.assertTrue("should have rerun", BasicDecider.isDoRerun(BasicDecider.FILE_STATUS.DISJOINT_SETS, BasicDecider.PREVIOUS_RUN_STATUS.FAILED));
        Assert.assertTrue("should have rerun", BasicDecider.isDoRerun(BasicDecider.FILE_STATUS.PAST_SUBSET_OR_INTERSECTION, BasicDecider.PREVIOUS_RUN_STATUS.FAILED));
        Assert.assertTrue("should have rerun", BasicDecider.isDoRerun(BasicDecider.FILE_STATUS.PAST_SUBSET_OR_INTERSECTION, BasicDecider.PREVIOUS_RUN_STATUS.COMPLETED));
        Assert.assertTrue("should have rerun", BasicDecider.isDoRerun(BasicDecider.FILE_STATUS.SAME_FILES, BasicDecider.PREVIOUS_RUN_STATUS.FAILED));
        Assert.assertTrue("should have rerun", BasicDecider.isDoRerun(BasicDecider.FILE_STATUS.PAST_SUPERSET, BasicDecider.PREVIOUS_RUN_STATUS.FAILED));
    }

    @Test
    public void testNegativeRerunConditions() {
        Assert.assertTrue("should not have rerun", !BasicDecider.isDoRerun(BasicDecider.FILE_STATUS.PAST_SUBSET_OR_INTERSECTION, BasicDecider.PREVIOUS_RUN_STATUS.OTHER));
        Assert.assertTrue("should not have rerun", !BasicDecider.isDoRerun(BasicDecider.FILE_STATUS.SAME_FILES, BasicDecider.PREVIOUS_RUN_STATUS.OTHER));
        Assert.assertTrue("should not have rerun", !BasicDecider.isDoRerun(BasicDecider.FILE_STATUS.SAME_FILES, BasicDecider.PREVIOUS_RUN_STATUS.COMPLETED));
        Assert.assertTrue("should not have rerun", !BasicDecider.isDoRerun(BasicDecider.FILE_STATUS.PAST_SUPERSET, BasicDecider.PREVIOUS_RUN_STATUS.OTHER));
        Assert.assertTrue("should not have rerun", !BasicDecider.isDoRerun(BasicDecider.FILE_STATUS.PAST_SUPERSET, BasicDecider.PREVIOUS_RUN_STATUS.COMPLETED));
    }
}
