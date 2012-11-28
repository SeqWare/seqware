/*
 * Copyright (C) 2012 SeqWare
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.seqware.common.util.workflowtools;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.runtools.RunTools;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.xml.sax.SAXException;

/**
 * This class provides methods for dealing with Pegasus workflow log directories
 * for example watching a directory to detect when a workflow has failed or
 * finished.
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class WorkflowTools {

  protected int statusCounts = 3;
  protected int totalSteps = 0;
  protected int currStep = 0;
  protected int percentage = 0;

  /**
   * This method examines a Pegasus log directory using the status command
   * provided and will encode the state of the workflow in the ReturnValue
   * object, running as ReturnValue.PROCESSING, failed as ReturnValue.FAILURE,
   * and success as ReturnValue.SUCCESS.
   *
   * @param statusCmd a {@link java.lang.String} object.
   * @return ReturnValue
   */
  public ReturnValue getWorkflowState(String statusCmd) {

    ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS);

    ArrayList<String> theCommand = new ArrayList<String>();
    theCommand = new ArrayList<String>();
    theCommand.add("bash");
    theCommand.add("-lc");
    theCommand.add(statusCmd);
    ret = RunTools.runCommand(theCommand.toArray(new String[0]));

    // check to make sure the command was OK
    if (ret.getExitStatus() != ReturnValue.SUCCESS) {
      Log.error("the status command failed: " + statusCmd);
      ret.setExitStatus(ReturnValue.UNKNOWN);
      return (ret);
    }

    // status string
    String statusString = parsePegasusStatus(ret.getStdout());

    // print the summary
    if (statusString != null) {
      Log.stdout(statusString);
    }

    // check the status in the STDOUT and set the ReturnValue correctly
    if (ret.getStdout().contains("FAILED")) {
      ret.setExitStatus(ReturnValue.FAILURE);
    } else if (ret.getStdout().contains("COMPLETED") && ret.getStdout().contains("100%")) {
      ret.setExitStatus(ReturnValue.SUCCESS);
    } else if (ret.getStdout().contains("RUNNING")) {
      ret.setExitStatus(ReturnValue.PROCESSING);
    } else {
      ret.setExitStatus(ReturnValue.UNKNOWN);
    }

    return (ret);

  }

  /**
   * This method allows you to monitor a running Pegasus workflow using a
   * statusCmd (typically something like "pegasus-status -l dir" and, if
   * something goes wrong, it will return false. This method won't return until
   * a workflow either finishes normally or terminates in error.
   *
   * @param statusCmd a {@link java.lang.String} object.
   * @param statusDir a {@link java.lang.String} object.
   * @return boolean indicating success or failure, use other methods to get
   * details of failure
   */
  public ReturnValue watchWorkflow(String statusCmd, String statusDir) {
    return (watchWorkflow(statusCmd, statusDir, -1));
  }

  /**
   * This method allows you to monitor a running Pegasus workflow using a
   * statusCmd (typically something like "pegasus-status -l dir" and, if
   * something goes wrong, it will return a ReturnValue with exit status of
   * ReturnValue.FAILURE, ReturnValue.PROCESSING if still running, and
   * ReturnValue.SUCCESS if finished OK. This method will watch the workflow for
   * at least the numbers of cycles specified (each last for 5 seconds) before
   * returning the ReturnValue.
   *
   * @param statusCmd a {@link java.lang.String} object.
   * @param statusDir a {@link java.lang.String} object.
   * @return boolean indicating success or failure, use other methods to get
   * details of failure
   * @param cycles a int.
   */
  public ReturnValue watchWorkflow(String statusCmd, String statusDir, int cycles) {

    ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS);

    boolean cont = true;
    int currCycle = 0;
    try {
      Thread.sleep(20000);
    } catch (InterruptedException ex) {
      Log.error("Threw interrupt exception during watchWorkflow: " + ex.getMessage());
    }
    int failedCounts = statusCounts; // should wait about 1 minutes to call something finished/errored
    int finishedCounts = statusCounts;
    Log.stdout("MONITORING PEGASUS STATUS:");

    // loop until the workflow finishes or fails
    while (cont) {

      ReturnValue statusReturn = getWorkflowState(statusCmd);

      // if failed
      if (statusReturn.getExitStatus() == ReturnValue.FAILURE) {
        failedCounts--;
        if (failedCounts < 1) {
          ret.setExitStatus(ReturnValue.FAILURE);
          ret.setProcessExitStatus(ReturnValue.FAILURE);
          // parse out the failed jobs info and print here and also add to return
          ReturnValue[] failedJobsInfo = getFailedJobsInfo(statusDir);
          Log.stdout("NUMBER OF FAILED JOB LOGS: " + failedJobsInfo.length);
          Log.stdout("PRINTING FAILED JOB INFORMATION:\n");
          if (failedJobsInfo == null || failedJobsInfo.length == 0) {
            Log.stderr("+Cannot locate and/or parse any job log files! This should not happen!");
            ret.setStderr("Cannot locate and/or parse any job log files! This should not happen! If this does happen it likely means the cluster has killed your job before any job output was written. This can be the case if your cluster has hard CPU, memory, or runtime limits and your job exceeded those. Try increasing your declared memory requirements in the profile section of the DAX workflow document. Or this failure means the log directory ("+statusDir+") or working directory are invalid.");
          } else {
            for (ReturnValue failedJob : failedJobsInfo) {
              Log.stdout("\n\nSTDOUT\n");
              Log.stdout(failedJob.getStdout());
              ret.setStdout(filterNull(ret.getStdout()) + filterNull(failedJob.getStdout()));
              Log.stderr("\n\nSTDERR\n");
              Log.stderr(failedJob.getStderr());
              ret.setStderr(filterNull(ret.getStderr()) + filterNull(failedJob.getStderr()));
            }
          }
          Log.error("Workflow failed");
          cont = false;
        }
      } else if (statusReturn.getExitStatus() == ReturnValue.SUCCESS) {
        finishedCounts--;
        if (finishedCounts < 1) {
          Log.stdout("WORKFLOW COMPLETED SUCCESSFULLY!");
          cont = false;
          ret.setExitStatus(ReturnValue.SUCCESS);
          ret.setProcessExitStatus(ReturnValue.SUCCESS);
          this.percentage = 100;
          this.currStep = this.totalSteps;
        }
      } else if (statusReturn.getExitStatus() == ReturnValue.UNKNOWN) {
        finishedCounts--;
        if (finishedCounts < 1) {
          Log.stdout("WORKFLOW STATE UNKNOWN!");
          cont = false;
          ret.setExitStatus(ReturnValue.UNKNOWN);
          ret.setProcessExitStatus(ReturnValue.UNKNOWN);
        }
      } else if (cycles > 0 && currCycle > cycles && currCycle > statusCounts) {
        cont = false;
        ret.setExitStatus(ReturnValue.PROCESSING);
        ret.setProcessExitStatus(ReturnValue.PROCESSING);
      }
      // keep track of the number of cycles
      currCycle++;
      try {
        Thread.sleep(5000);
      } catch (InterruptedException ex) {
        Log.error("Threw interrupt exception during watchWorkflow: " + ex.getMessage());
      }
    }

    // save the current step
    ret.getAttributes().put("currStep", new Integer(this.currStep).toString());
    ret.getAttributes().put("totalSteps", new Integer(this.totalSteps).toString());
    ret.getAttributes().put("percentage", new Integer(this.percentage).toString());

    return (ret);
  }

  /**
   * This method will examine a Pegasus DAX log directory for a given workflow
   * and identify the log files for jobs that failed. It will then report these
   * back as an array of RetunValue objects
   *
   * @param statusDir a {@link java.lang.String} object.
   * @return an array of {@link net.sourceforge.seqware.common.module.ReturnValue} objects.
   */
  public ReturnValue[] getFailedJobsInfo(String statusDir) {
    ArrayList<ReturnValue> returns = new ArrayList<ReturnValue>();

    File[] failedJobLogFiles = getFailedJobLogs(statusDir);

    Log.info("  + There are " + failedJobLogFiles.length + " failed job log files");

    for (File failedJobLogFile : failedJobLogFiles) {

      HashMap<String, HashMap<String, String>> errAndOutStr = parseLog(failedJobLogFile);
      // print out summary
      /*
       * for(String key : errAndOutStr.keySet()) { System.out.println("ERROR
       * REPORT SECTION:"+key); for(String key2 :
       * errAndOutStr.get(key).keySet()) { System.out.println(" + KEY:
       * "+key2+"\tVALUE: "+errAndOutStr.get(key).get(key2)); } }
       */

      if (errAndOutStr != null) {
        // info on the main job
        HashMap<String, String> mainJobErrors = errAndOutStr.get("mainjob");
        if (mainJobErrors != null) {
          ReturnValue newReturn = new ReturnValue();
          String stdOutStr = "=============================================\n"
                  + "FAILED JOB LOG FILE: " + failedJobLogFile.getAbsolutePath() + "\n"
                  + "COMMAND: " + mainJobErrors.get("command") + "\n"
                  + "=============================================\n"
                  + indentString(mainJobErrors.get("stdout"))
                  + "\n=============================================\n\n";
          newReturn.setStdout(stdOutStr);
          String stdErrStr = "=============================================\n"
                  + "FAILED JOB LOG FILE: " + failedJobLogFile.getAbsolutePath() + "\n"
                  + "FAILURE INFO: " + mainJobErrors.get("failure") + "\n"
                  + "=============================================\n"
                  + indentString(mainJobErrors.get("stderr"))
                  + "\n=============================================\n\n";
          newReturn.setStderr(stdErrStr);
          returns.add(newReturn);
        } else {
          Log.error("Unable to find mainjob information from " + failedJobLogFile.getAbsolutePath());
        }
      } else {
        Log.info("Unable to parse information from " + failedJobLogFile.getAbsolutePath() + " did not find STDERR/STDOUT sections or file was empty!");
      }
    }

    return (returns.toArray(new ReturnValue[0]));
  }

  private String filterNull(String input) {
    if (input == null) {
      return ("");
    }
    return (input);
  }

  private String indentString(String input) {
    if (input == null) {
      return ("");
    }
    StringBuilder sb = new StringBuilder();
    String[] t = input.split("\\n");
    for (String line : t) {
      sb.append("\t");
      sb.append(line);
      sb.append("\n");
    }
    return (sb.toString());
  }

  private File[] getFailedJobLogs(String statusDir) {

    File dir = new File(statusDir);
    ArrayList<File> logFiles = new ArrayList<File>();

    if (dir.isDirectory()) {

      //System.out.println("Looking in directory for *.dagman.out: "+dir.getAbsolutePath());

      FileFilter fileFilter = new WildcardFileFilter("*.dagman.out");
      File[] files = dir.listFiles(fileFilter);
      if (files.length != 1) {
        Log.error("There are too many files matching *.dagman.out in statusDir: " + statusDir + " expected just one but found " + files.length);
      } else {
        File dagLog = files[0];
        Log.info("EXAMINING ERROR LOGS:");
        Log.info(" + found dagman file: " + dagLog.getAbsolutePath());
        String[] logNames = findLogNamesFromDagLog(dagLog);
        for (String logFileName : logNames) {
          Log.info(" + found error log file prefix: " + logFileName);
          FileFilter logFileFilter = new WildcardFileFilter(logFileName + ".out*");
          File[] currLogFiles = dir.listFiles(logFileFilter);
          File newLogFile = null;
          for (File currLogFile : currLogFiles) {
            Log.info(" + found error log file instance: " + currLogFile.getAbsolutePath());
            if (newLogFile == null && currLogFile != null) {
              newLogFile = currLogFile;
            } else if (newLogFile.compareTo(currLogFile) < 0) {
              newLogFile = currLogFile;
            }
          }
          if (newLogFile != null && newLogFile.exists()) {
            //System.out.println("Adding log file");
            logFiles.add(newLogFile);
          } else if (newLogFile != null) {
            Log.error("Log file: " + newLogFile.getAbsolutePath() + " does not exist!");
          } else {
            Log.error("Log file was not found!");
          }
        }
        return (logFiles.toArray(new File[0]));
      }
    }

    Log.error("Was unable to find any error logs in statusDir: " + statusDir);
    return (logFiles.toArray(new File[0]));
  }

  private String[] findLogNamesFromDagLog(File dagLog) {

    String[] logList = null;
    ArrayList<String> logListArray = new ArrayList<String>();

    try {

      BufferedReader reader = new BufferedReader(new FileReader(dagLog));
      String line = reader.readLine();

      // setup matcher
      Pattern p = Pattern.compile("Node Name: (\\S+)");

      while (line != null) {
        if (line.contains("ERROR: the following job(s) failed")) {
          logListArray = new ArrayList<String>();
        }
        Matcher m = p.matcher(line);
        if (m.find()) {
          logListArray.add(m.group(1));
        }
        line = reader.readLine();
      }

      reader.close();

    } catch (FileNotFoundException ex) {
      Logger.getLogger(WorkflowTools.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Logger.getLogger(WorkflowTools.class.getName()).log(Level.SEVERE, null, ex);
    }

    return (logListArray.toArray(new String[0]));
  }

  private HashMap<String, HashMap<String, String>> parseLog(File failedJobLogFile) {

    int tries = 20;
    
    while(tries >= 0) {
      
      tries--;
      
      if (failedJobLogFile != null && failedJobLogFile.exists() && failedJobLogFile.canRead()) {
        try {
          if(failedJobLogFile.length()<=0) {
        	  Logger.getLogger(WorkflowTools.class.getName()).log(Level.SEVERE, "empty file: " + failedJobLogFile.getAbsolutePath());
        	  return new HashMap<String, HashMap<String,String>>();
          }
          Log.info("  + Parsing file: " + failedJobLogFile.getAbsolutePath());

          SAXParserFactory factory = SAXParserFactory.newInstance();
          SAXParser saxParser = factory.newSAXParser();

          // inner class to handle parsing
          LogDefaultHandler handler = new LogDefaultHandler();

          saxParser.parse(failedJobLogFile.getAbsoluteFile(), handler);

          return (handler.getData());

        } catch (IOException ex) {
          ex.printStackTrace();
          Logger.getLogger(WorkflowTools.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
          ex.printStackTrace();
          Logger.getLogger(WorkflowTools.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
          ex.printStackTrace();
          Logger.getLogger(WorkflowTools.class.getName()).log(Level.SEVERE, null, ex);
        }

      }
    
      // sleep
      try {
        Thread.sleep(6000);
      } catch (InterruptedException ex) {
        Log.error("Threw interrupt exception during parseLog: " + ex.getMessage());
      }
      
    }

    return (null);
    
  }

  class LogDefaultHandler extends org.xml.sax.helpers.DefaultHandler {

    private HashMap<String, HashMap<String, String>> jobsInfo = new HashMap<String, HashMap<String, String>>();
    private String currentJobReading = null;
    private String currentOutputReading = null;
    private boolean readyToReadData = false;

    public HashMap<String, HashMap<String, String>> getData() {
      //System.out.println("trying to get Data!");
      return (jobsInfo);
    }

    @Override
    public void startElement(String uri, String localName, String qName, org.xml.sax.Attributes attributes) throws SAXException {

      //System.out.println("    - Start Element :" + qName);

      if ("mainjob".equals(qName)) {
        currentJobReading = "mainjob";
      } else if ("statcall".equals(qName)) {
        if (attributes.getValue("id") != null && "stdout".equals(attributes.getValue("id"))) {
          currentJobReading = "mainjob";
          currentOutputReading = "stdout";
          //System.out.println("  + found STDOUT");
        } else if (attributes.getValue("id") != null && "stderr".equals(attributes.getValue("id"))) {
          currentJobReading = "mainjob";
          currentOutputReading = "stderr";
          //System.out.println("  + found STDERR");
        }
      } else if ("data".equals(qName)) {
        if (currentOutputReading != null && currentJobReading != null && currentJobReading.equals("mainjob") && (currentOutputReading.equals("stdout") || currentOutputReading.equals("stderr"))) {
          //System.out.println("  + found the data section so readyToRead");
          readyToReadData = true;
        }
      } else if ("failure".equals(qName)) {
        currentJobReading = "mainjob";
        currentOutputReading = "failure";
        readyToReadData = true;
      } else if ("arg".equals(qName)) {
        currentJobReading = "mainjob";
        currentOutputReading = "command";
        readyToReadData = true;
      }

      /*
       * switch (Job.valueOf(qName)) { case setup: currentJobReading = "setup";
       * break; case prejob: currentJobReading = "prejob"; break; case mainjob:
       * currentJobReading = "mainjob"; break; case postjob: currentJobReading =
       * "postjob"; break; case cleanup: currentJobReading = "cleanup"; break;
       * case statcall: if (attributes.getValue("id") != null &&
       * "stdout".equals(attributes.getValue("id"))) { currentOutputReading =
       * "stdout"; System.out.println("I found STDOUT!"); } else if
       * (attributes.getValue("id") != null &&
       * "stderr".equals(attributes.getValue("id"))) { currentOutputReading =
       * "stderr"; System.out.println("I found STDERR!"); } break; case data: if
       * (currentOutputReading != null && currentJobReading != null) {
       * readyToReadData = true; } break; default: break; }
       */
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
      if ("data".equals(qName) || "failure".equals(qName) || "statcall".equals(qName) || "arg".equals(qName) || "mainjob".equals(qName)) {
        //System.out.println("  + found the end of the data section so NOT readyToRead");
        readyToReadData = false;
        currentJobReading = null;
      }
    }

    @Override
    public void characters(char[] chars, int start, int length) throws SAXException {
      if (readyToReadData) {
        //System.out.println("  Reading data section!");
        //readyToReadData = false;
        //System.out.println("Adding for "+currentJobReading+" method "+currentOutputReading);
        HashMap<String, String> outputMap = jobsInfo.get(currentJobReading);
        if (outputMap == null) {
          outputMap = new HashMap<String, String>();
        }
        String dataSection = new String(chars, start, length);
        //for (char currChar : chars) {
        //  System.out.print(currChar);
        //}
        //System.out.print(dataSection);
        String previousText = outputMap.get(currentOutputReading);
        if (previousText != null) {
          outputMap.put(currentOutputReading, previousText + " " + dataSection);
        } else {
          outputMap.put(currentOutputReading, dataSection);
        }
        jobsInfo.put(currentJobReading, outputMap);
      }
    }
  }

  /**
   * <p>parsePegasusStatus.</p>
   *
   * @param pegasusStatus a {@link java.lang.String} object.
   * @return a {@link java.lang.String} object.
   */
  public String parsePegasusStatus(String pegasusStatus) {
    String ret = null;
    Pattern p = Pattern.compile(".*\\s(\\d+)/(\\d+)\\s.*");
    String[] lines = pegasusStatus.split("\n");
    for (String line : lines) {
      if (line.contains("RUNNING")) {
        //System.out.println("CURRENT LINE: "+line);
        String[] tokens = line.split("\\|");
        //System.out.println("CURRENT TOKEN: "+tokens[1]);
        Matcher m = p.matcher(tokens[1]);
        if (m.matches()) {
          String finished = m.group(1);
          String total = m.group(2);
          if (Integer.parseInt(finished) != this.currStep) {
            this.currStep = Integer.parseInt(finished);
            this.totalSteps = Integer.parseInt(total);
            this.percentage = (int) ((int) 100 * (Double.parseDouble(finished) / Double.parseDouble(total)));
            //System.out.println("HERE! RUNNING: step "+finished+" of "+total+" ("+percentage+"%)");
            return ("RUNNING: step " + finished + " of " + total + " (" + percentage + "%)");
          }
        }
      }
    }
    //Log.error("unable to parse the status: "+pegasusStatus);
    return (ret);
  }

  /**
   * <p>Getter for the field <code>statusCounts</code>.</p>
   *
   * @return a int.
   */
  public int getStatusCounts() {
    return statusCounts;
  }

  /**
   * <p>Setter for the field <code>statusCounts</code>.</p>
   *
   * @param statusCounts a int.
   */
  public void setStatusCounts(int statusCounts) {
    this.statusCounts = statusCounts;
  }
}
