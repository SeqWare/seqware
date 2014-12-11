package net.sourceforge.seqware.webservice.resources.queries;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.sql.SQLException;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import net.sourceforge.seqware.common.factory.DBAccess;
import net.sourceforge.seqware.common.metadata.MetadataDB;
import net.sourceforge.seqware.pipeline.runner.PluginRunner;
import net.sourceforge.seqware.webservice.resources.BasicResource;
import net.sourceforge.seqware.webservice.resources.TomcatVersion;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.restlet.data.MediaType;
import org.restlet.resource.Get;

/**
 * This resource returns information about the web-service environment
 *
 * @author dyuen
 */
public class EnvironmentResource extends BasicResource {

    @Get
    public void getXml() {

        SortedMap<String, String> environment = new TreeMap<>();
        PluginRunner pluginRunner = new PluginRunner();
        environment.put("metadata", "webservice");
        environment.put("tomcat.version", TomcatVersion.get().getServerNumber());
        environment.put("tomcat.built", TomcatVersion.get().getServerBuilt());
        environment.put("version", pluginRunner.getClass().getPackage().getImplementationVersion());
        environment.put("java.version", System.getProperty("java.version"));
        for (Entry<Object, Object> property : System.getProperties().entrySet()) {
            environment.put("java.property." + property.getKey().toString(), property.getValue().toString());
        }

        MetadataDB mdb = null;
        try {
            String query = "show all";
            mdb = DBAccess.get();
            List<Object[]> executeQuery = mdb.executeQuery(query, new ArrayListHandler());
            for (Object[] row : executeQuery) {
                environment.put("database." + row[0].toString(), row[1].toString());
            }
            environment.put("jdbc.driver.name", mdb.getDbmd().getDriverName());
            environment.put("jdbc.username", mdb.getDbmd().getUserName());
            environment.put("jdbc.driver.version", mdb.getDbmd().getDriverVersion());
            environment.put("jdbc.url", mdb.getDbmd().getURL());
        } catch (RuntimeException | SQLException ex) {
            environment.put("database", "connection error");
        } finally {
            if (mdb != null) {
                DbUtils.closeQuietly(mdb.getDb(), mdb.getSql(), null);
            }
            DBAccess.close();
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String toJson = gson.toJson(environment);
        getResponse().setEntity(toJson, MediaType.APPLICATION_ALL_JSON);

    }
}
