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
package net.sourceforge.seqware.pipeline.plugins;

import java.util.Collection;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import org.junit.Assert;
import org.junit.Test;
import org.openide.util.Lookup;

/**
 *
 * @author dyuen
 */
public class DocumentationTest {
  @Test 
  public void ensurePluginDescriptions(){
    Collection<? extends PluginInterface> plugs;
    plugs = Lookup.getDefault().lookupAll(PluginInterface.class);

    for (PluginInterface plug : plugs) {
          String description = plug.get_description();
          Assert.assertTrue("Plugin missing description: " + plug.getClass().getSimpleName(), description != null && !description.isEmpty());
    }
  }
}
