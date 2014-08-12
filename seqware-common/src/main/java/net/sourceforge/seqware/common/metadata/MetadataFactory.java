package net.sourceforge.seqware.common.metadata;

import io.seqware.pipeline.SqwKeys;
import java.sql.SQLException;
import java.util.Map;

public final class MetadataFactory {
    public static final String NO_DATABASE_CONFIG = "Missing some of the following required settings:" + SqwKeys.SW_DB_SERVER + ","
            + SqwKeys.SW_DB + "," + SqwKeys.SW_DB_USER + "," + SqwKeys.SW_DB_PASS;

    public static Metadata get(Map<String, String> settings) {
        String method = settings.get(SqwKeys.SW_METADATA_METHOD.getSettingKey());

        if ("database".equals(method)) {
            return getDB(settings);
        } else if ("webservice".equals(method)) {
            return getWS(settings);
        } else if ("none".equals(method)) {
            return getNoOp();
        } else if ("inmemory".equals(method)) {
            return getInMemory();
        } else {
            throw new RuntimeException("Missing SW_METADATA_METHOD entry in seqware settings.");
        }
    }

    public static MetadataWS getWS(Map<String, String> settings) {
        String url = settings.get(SqwKeys.SW_REST_URL.getSettingKey());
        String user = settings.get(SqwKeys.SW_REST_USER.getSettingKey());
        String pass = settings.get(SqwKeys.SW_REST_PASS.getSettingKey());

        if (url == null || user == null || pass == null) {
            throw new RuntimeException("Missing some of the following required settings: " + SqwKeys.SW_REST_URL + ","
                    + SqwKeys.SW_REST_USER + "," + SqwKeys.SW_REST_PASS);
        }

        return new MetadataWS(url, user, pass);
    }

    public static MetadataDB getDB(Map<String, String> settings) {
        String server = settings.get(SqwKeys.SW_DB_SERVER.getSettingKey());
        String dbName = settings.get(SqwKeys.SW_DB.getSettingKey());
        String user = settings.get(SqwKeys.SW_DB_USER.getSettingKey());
        String pass = settings.get(SqwKeys.SW_DB_PASS.getSettingKey());

        if (server == null || dbName == null || user == null || pass == null) {
            throw new RuntimeException(NO_DATABASE_CONFIG);
        }

        String url = "jdbc:postgresql://" + server + "/" + dbName;
        try {
            return new MetadataDB(url, user, pass);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static MetadataNoConnection getNoOp() {
        return new MetadataNoConnection();
    }

    public static MetadataInMemory getInMemory() {
        return new MetadataInMemory();
    }
}
