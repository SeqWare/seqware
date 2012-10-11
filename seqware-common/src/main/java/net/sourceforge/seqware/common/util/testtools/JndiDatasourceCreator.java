package net.sourceforge.seqware.common.util.testtools;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;
import org.postgresql.ds.PGPoolingDataSource;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

public class JndiDatasourceCreator {
  // private static final String url =
  // "jdbc:postgres:localhost/seqware_meta_db_test";
  private static final String username = "seqware";
  private static final String password = "seqware";
  private static final String jndiName = "SeqWareMetaDB";

  public static void create() throws Exception {
    try {
      if (isJNDIExist("java:comp/env/jdbc/" + jndiName))
        return;
      final SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
      PGPoolingDataSource ds = new PGPoolingDataSource();
      ds.setServerName("localhost");
      ds.setPortNumber(5432);
      ds.setDatabaseName("test_seqware_meta_db");
      ds.setUser(username);
      ds.setPassword(password);
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
