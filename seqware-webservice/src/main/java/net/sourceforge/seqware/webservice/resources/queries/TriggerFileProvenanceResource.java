package net.sourceforge.seqware.webservice.resources.queries;

import net.sourceforge.seqware.common.factory.DBAccess;
import net.sourceforge.seqware.common.metadata.MetadataDB;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.webservice.resources.BasicRestlet;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.io.IOUtils;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

public class TriggerFileProvenanceResource extends BasicRestlet {

    public TriggerFileProvenanceResource(Context context) {
        super(context);
    }

    @Override
    public void handle(final Request request, Response response) {
        super.handle(request, response);
        authenticate(request.getChallengeResponse().getIdentifier());
        init(request);

        MetadataDB mdb = null;
        try {
            Log.info("Executing file provenance update");
            String query = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("file-provenance-report.sql"), StandardCharsets.UTF_8);
            mdb = DBAccess.get();
            mdb.executeUpdate(query);
            response.setStatus(Status.SUCCESS_NO_CONTENT);
        } catch (IOException | SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            if (mdb != null) {
                DbUtils.closeQuietly(mdb.getDb(), mdb.getSql(), null);
            }
            DBAccess.close();
        }
    }
}
