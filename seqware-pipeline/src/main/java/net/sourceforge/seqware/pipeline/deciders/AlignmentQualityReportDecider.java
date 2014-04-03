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
package net.sourceforge.seqware.pipeline.deciders;

import java.util.List;
import net.sourceforge.seqware.common.hibernate.FindAllTheFiles;
import net.sourceforge.seqware.common.module.FileMetadata;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.common.util.Log;

/**
 * FIXME: this extends the plugin but really it should be a decider subclass. Doing this for testing now.
 * LEFT OFF WITH: left off with finding all files of a particular type
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class AlignmentQualityReportDecider extends Plugin {
  
  ReturnValue ret = new ReturnValue(ReturnValue.NOTIMPLEMENTED);

  /** {@inheritDoc} */
  @Override
  public ReturnValue init() {
    return(ret);
  }

  /** {@inheritDoc} */
  @Override
  public ReturnValue do_test() {
    return(ret);
  }

  /** {@inheritDoc} */
  @Override
  public ReturnValue do_run() {
    
    List<ReturnValue> returnValues = metadata.findFilesAssociatedWithAStudy("PCSI");
    for (ReturnValue rv : returnValues) {
      String workflowRunStatus = rv.getAttribute(FindAllTheFiles.WORKFLOW_RUN_STATUS);
      for (FileMetadata fm : rv.getFiles()) {
                if (fm.getMetaType().equals("application/bam")) {
                    Log.info(workflowRunStatus+" "+fm.getFilePath());
                }
            }
    }
    
    return(new ReturnValue(ReturnValue.SUCCESS));
  }

  /** {@inheritDoc} */
  @Override
  public ReturnValue clean_up() {
    return(ret);
  }
  
}
