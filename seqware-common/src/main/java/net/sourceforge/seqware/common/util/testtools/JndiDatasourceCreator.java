package net.sourceforge.seqware.common.util.testtools;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;
import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.postgresql.ds.PGPoolingDataSource;
import org.apache.tomcat.dbcp.dbcp.BasicDataSourceFactory;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

/**
 * <p>JndiDatasourceCreator class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class JndiDatasourceCreator {
  // private static final String url =
  // "jdbc:postgres:localhost/seqware_meta_db_test";
  private static final String username = "seqware";
  private static final String password = "seqware";
  private static final String jndiName = "SeqWareMetaDB";

  /**
   * <p>create.</p>
   *
   * @throws java.lang.Exception if any.
   */
  public static void create() throws Exception {
    try {
      if (isJNDIExist("java:comp/env/jdbc/" + jndiName))
        return;
      final SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
      
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setRemoveAbandoned(true);
        ds.setRemoveAbandonedTimeout(30);
        ds.setLogAbandoned(true);
        ds.setUrl("jdbc:postgresql://localhost:5432/test_seqware_meta_db");
        
        Map<String, String> settings = new HashMap<String, String>();
        try {
            settings = ConfigTools.getSettings();
        } catch (Exception e) {
            Log.stderr("Error reading settings file: " + e.getMessage());
        }
        if (settings.containsKey(BasicTestDatabaseCreator.BASIC_TEST_DB_HOST_KEY)) {
            ds.setUsername(settings.get(BasicTestDatabaseCreator.BASIC_TEST_USERNAME_KEY));
            ds.setPassword(settings.get(BasicTestDatabaseCreator.BASIC_TEST_PASSWORD_KEY));
            ds.setUrl("jdbc:postgresql://"+settings.get(BasicTestDatabaseCreator.BASIC_TEST_DB_HOST_KEY)+":5432/" + settings.get(BasicTestDatabaseCreator.BASIC_TEST_DB_NAME_KEY));
        }

//      PGPoolingDataSource ds = new PGPoolingDataSource();
//      ds.setServerName("localhost");
//      ds.setPortNumber(5432);
//      ds.setDatabaseName("test_seqware_meta_db");
//      ds.setUser(username);
//      ds.setPassword(password);
      
      builder.bind("java:comp/env/jdbc/" + jndiName, ds);
      builder.activate();
    } catch (NamingException ex) {
      ex.printStackTrace();
    }
  }

  private static boolean isJNDIExist(String name) throws NamingException {
    try {
      InitialContext context = new InitialContext();
      return context.lookup(name) != null;
    } catch (NoInitialContextException e) {
      return false;
    }
  }
}
