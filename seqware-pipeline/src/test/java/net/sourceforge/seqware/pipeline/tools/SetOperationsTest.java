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
package net.sourceforge.seqware.pipeline.tools;

import java.util.TreeSet;
import junit.framework.Assert;
import static net.sourceforge.seqware.pipeline.tools.SetOperations.difference;
import static net.sourceforge.seqware.pipeline.tools.SetOperations.intersection;
import static net.sourceforge.seqware.pipeline.tools.SetOperations.isSubset;
import static net.sourceforge.seqware.pipeline.tools.SetOperations.isSuperset;
import static net.sourceforge.seqware.pipeline.tools.SetOperations.symDifference;
import static net.sourceforge.seqware.pipeline.tools.SetOperations.union;
import org.junit.Test;

/**
 *
 * @author dyuen
 */
public class SetOperationsTest {

    @Test
    public void ExternalTest() {
        TreeSet<Character> set1 = new TreeSet<Character>();
        TreeSet<Character> set2 = new TreeSet<Character>();

        set1.add('A');
        set1.add('B');
        set1.add('C');
        set1.add('D');

        set2.add('C');
        set2.add('D');
        set2.add('E');
        set2.add('F');

        System.out.println("set1: " + set1);
        System.out.println("set2: " + set2);

        System.out.println("Union: " + union(set1, set2));
        Assert.assertTrue("union size wrong", union(set1, set2).size() == 6);
        System.out.println("Intersection: " + intersection(set1, set2));
        Assert.assertTrue("intersection size wrong", intersection(set1, set2).size() == 2);

        System.out.println("Difference (set1 - set2): " + difference(set1, set2));
        Assert.assertTrue("Difference size wrong", difference(set1, set2).size() == 2);
        System.out.println("Symmetric Difference: " + symDifference(set1, set2));
        Assert.assertTrue("Symmetric size wrong", symDifference(set1, set2).size() == 4);


        TreeSet<Character> set3 = new TreeSet<Character>(set1);

        set3.remove('D');
        System.out.println("set3: " + set3);
        System.out.println("Is set1 a subset of set2? " + isSubset(set1, set3));
        Assert.assertTrue(isSubset(set1, set3) == false);
        System.out.println("Is set1 a superset of set2? " + isSuperset(set1, set3));
        Assert.assertTrue(isSuperset(set1, set3) == true);
        System.out.println("Is set3 a subset of set1? " + isSubset(set3, set1));
        Assert.assertTrue(isSubset(set3, set1) == true);
        System.out.println("Is set3 a superset of set1? " + isSuperset(set3, set1));
        Assert.assertTrue(isSuperset(set3, set1) == false);
        
        // same set is both subset and superset
        Assert.assertTrue(isSuperset(set1, set1) && isSubset(set1, set1));
    }
}
