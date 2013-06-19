package net.sourceforge.seqware.webservice.resources.queries;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Date;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.representation.WriterRepresentation;

import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;

import net.sourceforge.seqware.webservice.resources.BasicRestlet;

public class StudyIdFilesTSVResource2 extends BasicRestlet {

  public StudyIdFilesTSVResource2(Context context) {
    super(context);
  }

  @Override
  public void handle(Request request, Response response) {
    authenticate(request.getChallengeResponse().getIdentifier());
    init(request);
    final int studyAccession = Integer.parseInt((String) request.getAttributes().get("studyId"));
    response.setEntity(new WriterRepresentation(MediaType.TEXT_TSV) {
      @Override
      public void write(Writer writer) throws IOException {
        StudyIdFilesTSVResource2.write(studyAccession, writer);
      }
    });
  }

  public static void write(int studyAccession, Writer writer) {
    Var require = RT.var("clojure.core", "require");
    require.invoke(Symbol.intern("io.seqware.report"));
    Var v = RT.var("io.seqware.report", "write-study-report!");
    v.invoke(studyAccession, writer);
  }

  // testing
  public static void main(String[] args) {
    System.out.println("Start: "+new Date());
    Writer out = null;
    try {
      out = new FileWriter("test.out");
      write(63, out);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (out != null) {
        try {
          out.close();
        } catch (IOException e) {
        }
      }
      System.out.println("Stop: "+new Date());
    }
  }
}
