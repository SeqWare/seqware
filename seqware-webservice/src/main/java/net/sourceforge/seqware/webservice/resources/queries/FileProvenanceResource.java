package net.sourceforge.seqware.webservice.resources.queries;

import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;
import net.sourceforge.seqware.webservice.resources.BasicRestlet;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.WriterRepresentation;
import org.restlet.resource.ResourceException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

public class FileProvenanceResource extends BasicRestlet {
    public FileProvenanceResource(Context context) {
        super(context);
    }

    @Override
    public void handle(final Request request, final Response response) {
        super.handle(request, response);
        authenticate(request.getChallengeResponse().getIdentifier());
        init(request);

        final WriterRepresentation writerRepresentation = new WriterRepresentation(MediaType.TEXT_TSV) {
            @Override public void write(Writer writer) throws IOException {
                Var require = RT.var("clojure.core", "require");
                require.invoke(Symbol.intern("io.seqware.report"));
                Var v = RT.var("io.seqware.report", "write-file-provenance-report!");
                    v.invoke(queryMap(request), writer);
            }
        };

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            writerRepresentation.write(stream);
        } catch (IOException e) {
            if (e.getMessage().equals("Invalid parameter")) {
                response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Bad client request, check your parameters");
            } else {
                throw new RuntimeException(e);
            }
        }

        try {
            response.setEntity(stream.toString(StandardCharsets.UTF_8.name()), MediaType.TEXT_TSV);
        } catch (UnsupportedEncodingException e) {
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Bad Encoding");
        }
    }

}
