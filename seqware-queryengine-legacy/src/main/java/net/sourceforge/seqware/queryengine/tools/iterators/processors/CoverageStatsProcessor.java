/**
 * 
 */
package net.sourceforge.seqware.queryengine.tools.iterators.processors;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import net.sourceforge.seqware.queryengine.backend.model.Coverage;
import net.sourceforge.seqware.queryengine.backend.model.Variant;
import net.sourceforge.seqware.queryengine.backend.store.impl.BerkeleyDBStore;

/**
 * @author boconnor
 *net.sourceforge.seqware.queryengine.tools.iterators.processors.CoverageStatsProcessor
 */
public class CoverageStatsProcessor extends CoverageProcessor implements ProcessorInterface {


  private long totalBases = 0;
  private long totalPositions = 0;
  private long largestCov = 0;
  
  public Object process (Object obj) {

    Coverage c = (Coverage) obj;
    
    //System.out.println("Coverage Object: "+c.getContig()+":"+c.getStartPosition()+"-"+c.getStopPosition());
    //System.out.println("My Coverage: "+this.getContig()+":"+this.getStart()+"-"+this.getStop());
    
    // figure out the range contained by this coverage object
    int newStart = this.getStart();
    int newStop = this.getStop();
    if (newStart < c.getStartPosition()) { newStart = c.getStartPosition(); }
    if (newStop > c.getStopPosition()) { newStop = c.getStopPosition(); }
    
    for(int i=newStart; i<=newStop; i++) {
      Integer intCov = c.getCoverage(i);
      if (intCov != null) {
        Long cov = new Long(intCov);
        if (cov > largestCov) { largestCov = cov; }
        totalBases += cov;
        totalPositions++;
        HashMap covPositions = (HashMap)this.getStats().get("cov_positions");
        if (covPositions == null) { covPositions = new HashMap(); this.getStats().put("cov_positions", covPositions); }
        HashMap covBases = (HashMap)this.getStats().get("cov_bases");
        if (covBases == null) { covBases = new HashMap(); this.getStats().put("cov_bases", covBases); }
        Long posCount = (Long)covPositions.get(cov);
        Long baseCount = (Long)covBases.get(cov);
        if (posCount != null) { posCount++; }
        else { posCount = new Long(1); }
        covPositions.put(cov, posCount);
        if (baseCount != null) { baseCount += cov; }
        else { baseCount = new Long(1); }
        covBases.put(cov, baseCount);
      }
    }
    return(null);
  }
  
  public String report(Object obj) {
    
    StringBuffer sb = new StringBuffer();
    
    // print out some general stats
    double mean = totalBases / totalPositions;
    double median = 0.0;
    boolean even = false;
    if (totalPositions % 2 == 0) { even = true; }
    long halfPos = (long)totalPositions / 2;
    if (!even) {halfPos++;}
    
    sb.append("# coverage statistics\n");
    sb.append("# total bases sequenced\t"+totalBases+"\n");
    sb.append("# total positions sequenced\t"+totalPositions+"\n");
    sb.append("# highest coverage\t"+largestCov+"\n");
    sb.append("# mean coverage\t"+mean+"\n");
    
    // calculate coverage
    Vector v = new Vector(((HashMap)this.getStats().get("cov_positions")).keySet());
    Collections.sort(v);
    
    // calc median
    long previous = 0;
    for(Enumeration e = v.elements(); e.hasMoreElements();) {
      Long cov = (Long)e.nextElement();
      //System.out.println("COV: "+cov);
      Long count = (Long)((HashMap)this.getStats().get("cov_positions")).get(cov);
      if (halfPos > previous && halfPos <= (previous+count)) {
        median = cov;
        //System.out.println("FOUND MEDIAN: "+cov);
        break;
      }
      previous += count;
    }
    sb.append("# median coverage\t"+median+"\n");
    
    sb.append("# coverage\tpositions_at_coverage\tbases_at_coverage\tpositions_at_that_coverage_or_greater\tbases_at_that_coverage_or_greater\n");
    // print out total bases for each coverage level
    for(long i=1; i<=largestCov; i++) {
      Long posCov = (Long)((HashMap)this.getStats().get("cov_positions")).get(i);
      if (posCov == null) { posCov = new Long(0);}
      long posTotal = 0;
      Long baseCov = (Long)((HashMap)this.getStats().get("cov_bases")).get(i);
      if (baseCov == null) { baseCov = new Long(0); }
      long baseTotal = 0;
      for (long j = i; j<=largestCov; j++) {
        if ((Long)((HashMap)this.getStats().get("cov_positions")).get(j) != null) {
          posTotal += (Long)((HashMap)this.getStats().get("cov_positions")).get(j);
        }
      }
      for (long j = i; j<=largestCov; j++) {
        if ((Long)((HashMap)this.getStats().get("cov_bases")).get(j) != null) {
          baseTotal += (Long)((HashMap)this.getStats().get("cov_bases")).get(j);
        }
      }
      sb.append(i+"\t"+posCov+"\t"+baseCov+"\t"+posTotal+"\t"+baseTotal+"\n");
    }
    
    return(sb.toString());
  }

}
