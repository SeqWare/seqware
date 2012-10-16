/*
 * Copyright (C) 2011 SeqWare
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
package net.sourceforge.seqware.pipeline.plugins;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Map;

import joptsimple.OptionSpec;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.module.FileMetadata;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import net.sourceforge.seqware.pipeline.plugins.filelinker.FileLinkerParser;

import org.openide.util.lookup.ServiceProvider;

/**
 * 
 * @author mtaschuk
 */
@ServiceProvider(service = PluginInterface.class)
public class FileLinker extends Plugin {

   ReturnValue ret = new ReturnValue();
   private OptionSpec<String> separator;

   public FileLinker() {
      super();
      parser.acceptsAll(
            Arrays.asList("file-list-file"),
            "A file containing the necessary information, with each line in the format parent_sw_accession, file path and mime-type. parent_sw_accession is either an IUS or a Lane.")
            .withRequiredArg();
      parser.acceptsAll(Arrays.asList("workflow-accession"), "The sw_accession of the Import files workflow").withRequiredArg();
      this.separator = parser.accepts("csv-separator").withOptionalArg().ofType(String.class).defaultsTo("\t")
            .describedAs("Separator used in csv file.");
      ret.setExitStatus(ReturnValue.SUCCESS);
   }

   @Override
   public ReturnValue init() {

      return ret;
   }

   @Override
   public ReturnValue do_test() {
      return ret;
   }

   @Override
   public ReturnValue do_run() {
      String currentDir = new File(".").getAbsolutePath();

      if (options.has("file-list-file") && options.has("workflow-accession")) {
         // parse the file
         String filename = (String) options.valueOf("file-list-file");

         Map<Integer, FileMetadata> swaToFileMap = null;
         try {
            swaToFileMap = FileLinkerParser.parse(filename, separator.value(options).charAt(0));
         } catch (FileNotFoundException e) {
            ret.setExitStatus(ReturnValue.INVALIDFILE);
            ret.setDescription(e.getMessage());
            return ret;
         }

         // make the workflow run
         int workflowAccession = new Integer(options.valueOf("workflow-accession").toString());
         int workflowRunId = metadata.add_workflow_run(workflowAccession);
         if (workflowRunId == 0) {
            Log.error("Failure in updating the workflow run " + workflowRunId + ". The workflow_run accession may be incorrect.");
            ret.setExitStatus(ReturnValue.SQLQUERYFAILED);
            return ret;
         }

         if (!saveFiles(workflowRunId, swaToFileMap)) { return ret; }

         // update the workflow run to reflect success
         ReturnValue rv = metadata.update_workflow_run(workflowRunId, null, null, Metadata.SUCCESS, null, currentDir, null, null,
               null, 0, 0, null, null);
         if (rv.getExitStatus() != ReturnValue.SUCCESS) {
            Log.error("Failure in updating the workflow run " + workflowRunId + ". The workflow_run accession may be incorrect.");
            ret.setExitStatus(ReturnValue.FAILURE);
            return ret;
         }

      } else {
         println("Combination of parameters not recognized!");
         println(this.get_syntax());
         ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
      }
      return ret;
   }

   private boolean saveFiles(int workflowRunId, Map<Integer, FileMetadata> swaToFileMap) {
      boolean result = true;
      for (Map.Entry<Integer, FileMetadata> entry : swaToFileMap.entrySet()) {
         if (!metadata.isDuplicateFile(entry.getValue().getFilePath())) {
            ReturnValue rv;
            if ((rv = metadata.saveFileForIus(workflowRunId, entry.getKey(), entry.getValue())).getExitStatus() != ReturnValue.SUCCESS) {
               Log.error("Failed to add file [" + entry.getValue().getFilePath() + "] with meta data type ["
                     + entry.getValue().getMetaType()
                     + "] to database. Attempting to add file to entity with SeqWare Accession [" + entry.getKey()
                     + "]. Most likely an IUS or Lane.");
               ret.setExitStatus(rv.getExitStatus());
               result = false;
               break;
            } else {
               Log.info("Successfully added file [" + entry.getValue().getFilePath() + "] with meta data type ["
                     + entry.getValue().getMetaType()
                     + "] to database. SeqWare Accession [" + entry.getKey()
                     + "].");
            }
         } else {
            Log.error("Ignored file [" + entry.getValue().getFilePath() + "]. Already exists in database.");
         }
      }
      return result;
   }

   @Override
   public ReturnValue clean_up() {
      return ret;
   }

   @Override
   public String get_description() {
      return "Takes a list of files and enters them into the database, linking "
            + "them with the appropriate IUSes and creating workflow runs for "
            + "the 'FileImport' workflow. For more information, see https://sourceforge.net/apps/mediawiki/seqware/index.php?title=FileLinker";
   }

}
