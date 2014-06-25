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

import java.util.HashMap;
import org.xml.sax.SAXException;

/**
 * This class provides methods for dealing with Pegasus workflow log directories for example watching a directory to detect when a workflow
 * has failed or finished.
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public class WorkflowTools {

    protected int statusCounts = 3;
    protected int totalSteps = 0;
    protected int currStep = 0;
    protected int percentage = 0;

    class LogDefaultHandler extends org.xml.sax.helpers.DefaultHandler {

        private HashMap<String, HashMap<String, String>> jobsInfo = new HashMap<>();
        private String currentJobReading = null;
        private String currentOutputReading = null;
        private boolean readyToReadData = false;

        public HashMap<String, HashMap<String, String>> getData() {
            // System.out.println("trying to get Data!");
            return (jobsInfo);
        }

        @Override
        public void startElement(String uri, String localName, String qName, org.xml.sax.Attributes attributes) throws SAXException {

            // System.out.println("    - Start Element :" + qName);

            if ("mainjob".equals(qName)) {
                currentJobReading = "mainjob";
            } else if ("statcall".equals(qName)) {
                if (attributes.getValue("id") != null && "stdout".equals(attributes.getValue("id"))) {
                    currentJobReading = "mainjob";
                    currentOutputReading = "stdout";
                    // System.out.println("  + found STDOUT");
                } else if (attributes.getValue("id") != null && "stderr".equals(attributes.getValue("id"))) {
                    currentJobReading = "mainjob";
                    currentOutputReading = "stderr";
                    // System.out.println("  + found STDERR");
                }
            } else if ("data".equals(qName)) {
                if (currentOutputReading != null && currentJobReading != null && currentJobReading.equals("mainjob")
                        && (currentOutputReading.equals("stdout") || currentOutputReading.equals("stderr"))) {
                    // System.out.println("  + found the data section so readyToRead");
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
             * switch (Job.valueOf(qName)) { case setup: currentJobReading = "setup"; break; case prejob: currentJobReading = "prejob";
             * break; case mainjob: currentJobReading = "mainjob"; break; case postjob: currentJobReading = "postjob"; break; case cleanup:
             * currentJobReading = "cleanup"; break; case statcall: if (attributes.getValue("id") != null &&
             * "stdout".equals(attributes.getValue("id"))) { currentOutputReading = "stdout"; System.out.println("I found STDOUT!"); } else
             * if (attributes.getValue("id") != null && "stderr".equals(attributes.getValue("id"))) { currentOutputReading = "stderr";
             * System.out.println("I found STDERR!"); } break; case data: if (currentOutputReading != null && currentJobReading != null) {
             * readyToReadData = true; } break; default: break; }
             */
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if ("data".equals(qName) || "failure".equals(qName) || "statcall".equals(qName) || "arg".equals(qName)
                    || "mainjob".equals(qName)) {
                // System.out.println("  + found the end of the data section so NOT readyToRead");
                readyToReadData = false;
                currentJobReading = null;
            }
        }

        @Override
        public void characters(char[] chars, int start, int length) throws SAXException {
            if (readyToReadData) {
                // System.out.println("  Reading data section!");
                // readyToReadData = false;
                // System.out.println("Adding for "+currentJobReading+" method "+currentOutputReading);
                HashMap<String, String> outputMap = jobsInfo.get(currentJobReading);
                if (outputMap == null) {
                    outputMap = new HashMap<>();
                }
                String dataSection = new String(chars, start, length);
                // for (char currChar : chars) {
                // System.out.print(currChar);
                // }
                // System.out.print(dataSection);
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
     * <p>
     * Getter for the field <code>statusCounts</code>.
     * </p>
     * 
     * @return a int.
     */
    public int getStatusCounts() {
        return statusCounts;
    }

    /**
     * <p>
     * Setter for the field <code>statusCounts</code>.
     * </p>
     * 
     * @param statusCounts
     *            a int.
     */
    public void setStatusCounts(int statusCounts) {
        this.statusCounts = statusCounts;
    }
}
