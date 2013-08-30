package net.sourceforge.seqware.webservice.resources.queries;

import static net.sourceforge.seqware.webservice.resources.BasicResource.parseClientInt;

import java.io.IOException;
import java.io.Writer;

import net.sourceforge.seqware.webservice.resources.BasicRestlet;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.representation.WriterRepresentation;

import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;
import net.sourceforge.seqware.webservice.resources.BasicResource;

public class StudyIdFilesTSVResource2 extends BasicRestlet {

  public StudyIdFilesTSVResource2(Context context) {
    super(context);
  }

  @Override
  public void handle(Request request, Response response) {
    authenticate(request.getChallengeResponse().getIdentifier());
    init(request);
    if (request.getAttributes().containsKey("studyId")) {
      final int studyAccession = parseClientInt((String) request.getAttributes().get("studyId"));
      response.setEntity(new WriterRepresentation(MediaType.TEXT_TSV) {
        @Override
        public void write(Writer writer) throws IOException {
          writeStudyReport(studyAccession, writer);
        }
      });
    } else {
      response.setEntity(new WriterRepresentation(MediaType.TEXT_TSV) {
        @Override
        public void write(Writer writer) throws IOException {
          writeAllStudiesReport(writer);
        }
      });
    }
  }

  public static void writeStudyReport(int studyAccession, Writer writer) {
    Var require = RT.var("clojure.core", "require");
    require.invoke(Symbol.intern("io.seqware.report"));
    Var v = RT.var("io.seqware.report", "write-study-report!");
    v.invoke(studyAccession, writer);
  }

  public static void writeAllStudiesReport(Writer writer) {
    Var require = RT.var("clojure.core", "require");
    require.invoke(Symbol.intern("io.seqware.report"));
    Var v = RT.var("io.seqware.report", "write-all-studies-report!");
    v.invoke(writer);
  }

}
