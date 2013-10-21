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
package io.seqware.webservice.client;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import io.seqware.webservice.generated.client.SeqWareWebserviceClient;
import io.seqware.webservice.generated.model.Organism;
import java.util.List;
import org.junit.Assert;
import net.sourceforge.seqware.pipeline.plugins.ExtendedTestDatabaseCreator;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for the admin web service
 * @author dyuen
 */
public class ConnectivityIT {
    
    @BeforeClass
    public static void resetDatabase() {
        ExtendedTestDatabaseCreator.resetDatabaseWithUsers();
    }
    
    @Test
    public void testOrganismConnectivity(){

      // some testing for workflow_runs
      SeqWareWebserviceClient client = new SeqWareWebserviceClient("organism");
      ClientResponse response = client.findRange_XML(ClientResponse.class, "1", "5");
      GenericType<List<Organism>> genericType = new GenericType<List<Organism>>() {
      };
      List<Organism> data = response.getEntity(genericType);
      Assert.assertTrue("no organisms found", data.size() > 0);

   }
}
