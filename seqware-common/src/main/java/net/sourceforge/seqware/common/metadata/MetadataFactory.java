package net.sourceforge.seqware.common.metadata;

import java.util.Map;

public final class MetadataFactory {

  public static Metadata get(Map<String, String> settings) {
    String method = settings.get("SW_METADATA_METHOD");

    if ("database".equals(method)) {
      return getDB(settings);
    } else if ("webservice".equals(method)) {
      return getWS(settings);
    } else if ("none".equals(method)) {
      return getNoOp();
    } else {
      throw new RuntimeException("Missing SW_METADATA_METHOD entry in seqware settings.");
    }
  }

  private static Metadata getWS(Map<String, String> settings) {
    String url = settings.get("SW_REST_URL");
    String user = settings.get("SW_REST_USER");
    String pass = settings.get("SW_REST_PASS");
    
    if (url == null || user == null || pass == null) {
      throw new RuntimeException("Missing some of the following required settings: SW_REST_URL, SW_REST_USER, SW_REST_PASS");
    }
    
    return new MetadataWS(url, user, pass);
  }

  private static Metadata getDB(Map<String, String> settings) {
    String server = settings.get("SW_DB_SERVER");
    String dbName = settings.get("SW_DB");
    String user = settings.get("SW_DB_USER");
    String pass = settings.get("SW_DB_PASS");

    if (server == null || dbName == null || user == null || pass == null) {
      throw new RuntimeException("Missing some of the following required settings: SW_DB_SERVER, SW_DB, SW_DB_USER, SW_DB_PASS");
    }
    
    String url = "jdbc:postgresql://" + settings.get("SW_DB_SERVER") + "/" + settings.get("SW_DB");
    return new MetadataDB(url, user, pass);
  }

  private static Metadata getNoOp() {
    return new MetadataNoConnection();
  }
}
