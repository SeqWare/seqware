package net.sourceforge.seqware.webservice.resources.tables;

import java.util.Formatter;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.seqware.common.business.ValidationReportService;
import net.sourceforge.seqware.common.business.impl.ValidationReportServiceImpl.ReportEntry;
import net.sourceforge.seqware.common.factory.BeanFactory;

import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class FileLinkReportResource extends DatabaseResource {

  private List<Integer> swas = Lists.newArrayList();

  public FileLinkReportResource() {
    super("fileLinkReportResource");
  }

  @Override
  public void doInit() {
    String swasString = (String) getRequestAttributes().get("swas");
    if (swasString != null) {
      String[] swaArray = swasString.split(",");
      for (String swa : swaArray) {
        swas.add(Integer.valueOf(swa));
      }
    }
  }

  @Get("text")
  public Representation validate() {
    List<ReportEntry> report = Lists.newArrayList();
    ValidationReportService fileValidationService = BeanFactory.getFileValidationServiceBean();
    if (swas.isEmpty()) {
      report = fileValidationService.fileLinkReport();
    } else {
      report = fileValidationService.fileLinkReport(swas);
    }
    return new StringRepresentation(formatReport(report).toString());
  }
  
  /**
   * Generates a formatting string to give each table column just enough space.
   * @param report The table to be displayed. 
   * @return A formatting string. For example: %1$11s %2$19s %3$17s %4$11s %n
   */
  private String getReportFormat(List<Map<String, String>> report) {
    Map<String, Integer> header = Maps.newLinkedHashMap();
    for(Map<String, String> item : report) {
      for(Map.Entry<String, String> entry : item.entrySet()) {
        if(header.containsKey(entry.getKey())) {
          if(entry.getValue().length() > header.get(entry.getKey())) {
            header.put(entry.getKey(), entry.getValue().length());
          }
        } else {
          header.put(entry.getKey(), entry.getValue().length());
        }
      }
    }
    StringBuilder sb = new StringBuilder();
    int index = 1;
    for(Map.Entry<String, Integer> entry : header.entrySet()) {
      sb.append("%").append(index++).append("$").append(entry.getValue()).append("s ");
    }
    sb.append("%n");
    return sb.toString();
  }

  private StringBuilder formatReport(List<ReportEntry> report) {
    List<Map<String, String>> reportHash = populateReportHash(report);
    String format = getReportFormat(reportHash);
    
    StringBuilder sb = new StringBuilder();
    Formatter formatter = new Formatter(sb);
    for(Map<String, String> item : reportHash) {
      formatter.format(format, item.values().toArray());
    }
    return sb;
  }
  
  private List<Map<String, String>> populateReportHash(List<ReportEntry> report) {
    List<Map<String, String>> result = Lists.newArrayList();
    
    result.add(getHeader());
    for (ReportEntry entry : report) {
      Map<String, String> item = Maps.newLinkedHashMap();
      result.add(item);
      int numLinks = entry.getSequencerRunSwas().size() + entry.getLaneSwas().size() + entry.getIusSwas().size()
          + entry.getSampleSwas().size() + entry.getExperimentSwas().size() + entry.getStudySwas().size();
      item.put("total_links", Integer.toString(numLinks));
      item.put("#SRL", Integer.toString(entry.getSequencerRunSwas().size()));
      item.put("SequencerRunSWAs", listify(entry.getSequencerRunSwas()));
      item.put("#LL", Integer.toString(entry.getLaneSwas().size()));
      item.put("LaneSWAs", listify(entry.getLaneSwas()));
      item.put("#IL", Integer.toString(entry.getIusSwas().size()));
      item.put("IusSWAs", listify(entry.getIusSwas()));
      item.put("#SaL", Integer.toString(entry.getSampleSwas().size()));
      item.put("SampleSWAs", listify(entry.getSampleSwas()));
      item.put("#EL", Integer.toString(entry.getExperimentSwas().size()));
      item.put("ExperimentSWAs", listify(entry.getExperimentSwas()));
      item.put("#StL", Integer.toString(entry.getStudySwas().size()));
      item.put("StudySWAs", listify(entry.getStudySwas()));
      item.put("file_SWA", entry.getFileSwa());
      item.put("file_name", entry.getFilename());
    }
    return result;
  }
  
  private Map<String, String> getHeader() {
    Map<String, String> header = Maps.newLinkedHashMap();
    header.put("total_links", "total_links");
    header.put("#SRL","#SRL");
    header.put("SequencerRunSWAs","SequencerRunSWAs");
    header.put("#LL","#LL");
    header.put("LaneSWAs","LaneSWAs");
    header.put("#IL","#IL");
    header.put("IusSWAs","IusSWAs");
    header.put("#SaL","#SaL");
    header.put("SampleSWAs","SampleSWAs");
    header.put("#EL","#EL");
    header.put("ExperimentSWAs","ExperimentSWAs");
    header.put("#StL","#StL");
    header.put("StudySWAs","StudySWAs");
    header.put("file_SWA","file_SWA");
    header.put("file_name","file_name");
    return header;
  }
  

  private String listify(Set<String> collection) {
    StringBuilder sb = new StringBuilder();
    for (String item : collection) {
      sb.append(item);
      sb.append(',');
    }
    if (sb.length() > 0) {
      sb.setLength(sb.length() - 1);
    }
    return sb.toString();
  }
}
