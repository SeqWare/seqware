/*
 * Copyright (C) 2013 SeqWare
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
package org.apache.oozie.action.hadoop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.util.DiskChecker;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.oozie.action.ActionExecutor;
import org.apache.oozie.action.ActionExecutorException;
import org.apache.oozie.action.hadoop.JavaActionExecutor;
import org.apache.oozie.action.hadoop.LauncherMain;
import org.apache.oozie.action.hadoop.LauncherMapper;
import org.apache.oozie.action.hadoop.MapReduceMain;
import org.apache.oozie.action.hadoop.ShellMain;
import org.apache.oozie.client.WorkflowAction;
import org.apache.oozie.util.IOUtils;
import org.apache.oozie.util.XLog;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;

/**
 *
 * @author boconnor
 */
public class OozieSGEActionNode extends JavaActionExecutor {
  
  //static protected ArrayList<SGEJob> sgeJobs = new ArrayList<SGEJob>();
  
   /**
     * Config property name to set the child environment
     */
    public String OOZIE_LAUNCHER_CHILD_ENV = "mapred.child.env";

    public OozieSGEActionNode() {
        super("sge");
    }

    @Override
    protected List<Class> getLauncherClasses() {
        List<Class> classes = super.getLauncherClasses();
        // Base class of ShellMain dedicated for 'shell' action
        classes.add(LauncherMain.class);
        // Some utility methods used in ShelltMain
        classes.add(MapReduceMain.class);
        // Specific to Shell action
        classes.add(ShellMain.class);
        // ShellMain's inner class
        classes.add(ShellMain.OutputWriteThread.class);
        return classes;
    }

    @Override
    protected String getLauncherMain(Configuration launcherConf, Element actionXml) {
        return launcherConf.get(LauncherMapper.CONF_OOZIE_ACTION_MAIN_CLASS, ShellMain.class.getName());
    }

    @SuppressWarnings("unchecked")
    @Override
    Configuration setupActionConf(Configuration actionConf, Context context, Element actionXml, Path appPath)
            throws ActionExecutorException {
        super.setupActionConf(actionConf, context, actionXml, appPath);
        Namespace ns = actionXml.getNamespace();

        String exec = actionXml.getChild("exec", ns).getTextTrim();
        String execName = new Path(exec).getName();
        actionConf.set(ShellMain.CONF_OOZIE_SHELL_EXEC, execName);

        // Setting Shell command's arguments
        setListInConf("argument", actionXml, actionConf, ShellMain.CONF_OOZIE_SHELL_ARGS, false);
        // Setting Shell command's environment variable key=value
        setListInConf("env-var", actionXml, actionConf, ShellMain.CONF_OOZIE_SHELL_ENVS, true);

        // Setting capture output flag
        actionConf.setBoolean(ShellMain.CONF_OOZIE_SHELL_CAPTURE_OUTPUT,
                actionXml.getChild("capture-output", ns) != null);

        return actionConf;
    }

    /**
     * This method read a list of tag from an XML element and set the
     * Configuration accordingly
     *
     * @param tag
     * @param actionXml
     * @param actionConf
     * @param key
     * @param checkKeyValue
     * @throws ActionExecutorException
     */
    protected void setListInConf(String tag, Element actionXml, Configuration actionConf, String key,
            boolean checkKeyValue) throws ActionExecutorException {
        String[] strTagValue = null;
        Namespace ns = actionXml.getNamespace();
        List<Element> eTags = actionXml.getChildren(tag, ns);
        if (eTags != null && eTags.size() > 0) {
            strTagValue = new String[eTags.size()];
            for (int i = 0; i < eTags.size(); i++) {
                strTagValue[i] = eTags.get(i).getTextTrim();
                if (checkKeyValue) {
                    checkPair(strTagValue[i]);
                }
            }
        }
        MapReduceMain.setStrings(actionConf, key, strTagValue);
    }

    /**
     * Check if the key=value pair is appropriately formatted
     * @param pair
     * @throws ActionExecutorException
     */
    private void checkPair(String pair) throws ActionExecutorException {
        String[] varValue = pair.split("=");
        if (varValue == null || varValue.length <= 1) {
            throw new ActionExecutorException(ActionExecutorException.ErrorType.FAILED, "JA010",
                    "Wrong ENV format [{0}] in <env-var> , key=value format expected ", pair);
        }
    }

    @Override
    protected Configuration setupLauncherConf(Configuration conf, Element actionXml, Path appPath, Context context)
            throws ActionExecutorException {
        super.setupLauncherConf(conf, actionXml, appPath, context);
        conf.setBoolean("mapreduce.job.complete.cancel.delegation.tokens", true);
        addDefaultChildEnv(conf);
        return conf;
    }

    /**
     * This method sets the PATH to current working directory for the launched
     * map task from where shell command will run.
     *
     * @param conf
     */
    protected void addDefaultChildEnv(Configuration conf) {
        String envValues = "PATH=.:$PATH";
        updateProperty(conf, OOZIE_LAUNCHER_CHILD_ENV, envValues);
    }

    /**
     * Utility method to append the new value to any property.
     *
     * @param conf
     * @param propertyName
     * @param appendValue
     */
    private void updateProperty(Configuration conf, String propertyName, String appendValue) {
        if (conf != null) {
            String val = conf.get(propertyName, "");
            if (val.length() > 0) {
                val += ",";
            }
            val += appendValue;
            conf.set(propertyName, val);
            log.debug("action conf is updated with default value for property " + propertyName + ", old value :"
                    + conf.get(propertyName, "") + ", new value :" + val);
        }
    }
  
  
  /**
     * Config property name to set the child environment
     */
  /*
    public String OOZIE_LAUNCHER_CHILD_ENV = "mapred.child.env";

    public OozieSGEActionNode() {
        super("qsub");
    }


    @SuppressWarnings("unchecked")
    Configuration setupActionConf(Configuration actionConf, Context context, Element actionXml, Path appPath)
            throws ActionExecutorException {
        super(actionConf, context, actionXml, appPath);
        Namespace ns = actionXml.getNamespace();

        String exec = actionXml.getChild("exec", ns).getTextTrim();
        String execName = new Path(exec).getName();
        actionConf.set(ShellMain.CONF_OOZIE_SHELL_EXEC, execName);

        // Setting Shell command's arguments
        setListInConf("argument", actionXml, actionConf, ShellMain.CONF_OOZIE_SHELL_ARGS, false);
        // Setting Shell command's environment variable key=value
        setListInConf("env-var", actionXml, actionConf, ShellMain.CONF_OOZIE_SHELL_ENVS, true);

        // Setting capture output flag
        actionConf.setBoolean(ShellMain.CONF_OOZIE_SHELL_CAPTURE_OUTPUT,
                actionXml.getChild("capture-output", ns) != null);

        return actionConf;
    }*/

    /**
     * Utility method to append the new value to any property.
     *
     * @param conf
     * @param propertyName
     * @param appendValue
     */
    /*
    private void updateProperty(Configuration conf, String propertyName, String appendValue) {
        if (conf != null) {
            String val = conf.get(propertyName, "");
            if (val.length() > 0) {
                val += ",";
            }
            val += appendValue;
            conf.set(propertyName, val);
            log.debug("action conf is updated with default value for property " + propertyName + ", old value :"
                    + conf.get(propertyName, "") + ", new value :" + val);
        }
    } 
    */

  /* public OozieSGEActionNode() {
    super("sge");
  }

  @Override
  public void initActionType() {
    super.initActionType();
    XLog log = XLog.getLog(getClass());
    try {

      registerError(UnknownHostException.class.getName(), ActionExecutorException.ErrorType.TRANSIENT, "JA001");
      registerError(AccessControlException.class.getName(), ActionExecutorException.ErrorType.NON_TRANSIENT, "JA002");
      registerError(DiskChecker.DiskOutOfSpaceException.class.getName(),
              ActionExecutorException.ErrorType.NON_TRANSIENT, "JA003");
      registerError(org.apache.hadoop.hdfs.protocol.QuotaExceededException.class.getName(),
              ActionExecutorException.ErrorType.NON_TRANSIENT, "JA004");
      registerError(org.apache.hadoop.hdfs.server.namenode.SafeModeException.class.getName(),
              ActionExecutorException.ErrorType.NON_TRANSIENT, "JA005");
      registerError(ConnectException.class.getName(), ActionExecutorException.ErrorType.TRANSIENT, "  JA006");
      registerError(JDOMException.class.getName(), ActionExecutorException.ErrorType.ERROR, "JA007");
      registerError(FileNotFoundException.class.getName(), ActionExecutorException.ErrorType.ERROR, "JA008");
      registerError(IOException.class.getName(), ActionExecutorException.ErrorType.TRANSIENT, "JA009");
    } catch (java.lang.NoClassDefFoundError err) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      err.printStackTrace(new PrintStream(baos));
      log.warn(baos.toString());
    }
    
   
  }

  @Override
  public void start(Context cntxt, WorkflowAction action) throws ActionExecutorException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void end(Context cntxt, WorkflowAction action) throws ActionExecutorException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void check(Context cntxt, WorkflowAction action) throws ActionExecutorException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void kill(Context cntxt, WorkflowAction action) throws ActionExecutorException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean isCompleted(String string) {
    throw new UnsupportedOperationException("Not supported yet.");
  } */
  
}
