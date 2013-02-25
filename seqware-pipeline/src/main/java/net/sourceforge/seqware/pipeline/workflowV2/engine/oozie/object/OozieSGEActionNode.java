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
package net.sourceforge.seqware.pipeline.workflowV2.engine.oozie.object;

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
import org.apache.oozie.action.ActionExecutor;
import org.apache.oozie.action.ActionExecutorException;
import org.apache.oozie.client.WorkflowAction;
import org.apache.oozie.util.IOUtils;
import org.apache.oozie.util.XLog;
import org.jdom.JDOMException;

/**
 *
 * @author boconnor
 */
public class OozieSGEActionNode extends ActionExecutor {
  
  static protected ArrayList<SGEJob> sgeJobs = new ArrayList<SGEJob>();

  public OozieSGEActionNode() {
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
  }
}
