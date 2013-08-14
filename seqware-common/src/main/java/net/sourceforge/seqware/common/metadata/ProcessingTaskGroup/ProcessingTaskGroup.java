package net.sourceforge.seqware.common.metadata.ProcessingTaskGroup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.metadata.MetadataDB;
import net.sourceforge.seqware.common.util.filetools.lock.LockingFileTools;
import net.sourceforge.seqware.common.util.processtools.ProcessTools;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.sourceforge.seqware.common.model.ProcessingStatus;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;

/**
 * Standalone application to insert a new processing task group into the database. This can have parents and children associated with it.
 *
 * @author jordan
 * @version $Id: $Id
 */
public class ProcessingTaskGroup {
  /**
   * <p>getSyntax.</p>
   *
   * @param parser a {@link joptsimple.OptionParser} object.
   * @param errorMessage a {@link java.lang.String} object.
   */
  public static void getSyntax(OptionParser parser, String errorMessage) {
    if ( errorMessage != null && errorMessage.length() > 0 ) {
      Log.stderr("ERROR: " + errorMessage);
      Log.stderr("");
    }
    Log.stderr("Syntax: java net.sourceforge.seqware.pipeline.metadata.ProcessingTaskGroup [--help] [Parameters]");
    Log.stderr("");
    Log.stderr("Parameters are limited to the following:");
    try { parser.printHelpOn(System.err); }
    catch (IOException e) {
      e.printStackTrace(System.err);
    }
    System.exit(-1);
  }

  
    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    public static void main(String[] args) {
      OptionParser parser = new OptionParser();
      OptionSet options = null;
      
      try {
        // MetaDB config
        parser.accepts("metadata-config-database", "Required: The JDBC path for connection").withRequiredArg().ofType(String.class).describedAs("Example: jdbc:postgresql://127.0.0.1/seqware_meta_db");
        parser.accepts("metadata-config-username", "Required: Database username for connection").withRequiredArg().ofType(String.class).describedAs("Database Username");
        parser.accepts("metadata-config-password", "Required: Database password for connection").withRequiredArg().ofType(String.class).describedAs("Database Password");
        parser.accepts("metadata-tries-number", "Optional: After a failure, how many times we should try metadata write back operations, such as obtaining a lock, writing to DB, etc.").withRequiredArg().ofType(Integer.class).defaultsTo(60).describedAs("Number of tries (Default: 60)");
        parser.accepts("metadata-tries-delay", "Optional: After a failure, how long we should wait before trying again (in accordance with metadata-tires-number)").withRequiredArg().ofType(Integer.class).defaultsTo(5).describedAs("Number of seconds between tries (Default: 5)");
        
        // Parent's of the TaskGroup
        parser.accepts("metadata-parentID", "Optional: Specifies one of the parentID for the task group. This option can be specified zero or more times.").withRequiredArg().ofType(Integer.class).describedAs("The processingID of the parent for this event, for constructing the dependency tree in the metadb");
        parser.accepts("metadata-parentID-file", "Optional: The same as --metadata-parentID, but is a path to a file, to parse for parent processing ID's.").withRequiredArg().ofType(String.class).describedAs("Path to a line-delimeted file containing one or more parent processing IDs");
        
        // Children of the Task Group
        parser.accepts("metadata-childID", "Optional: Specifies one of the childID for the task group. This option can be specified zero or more times.").withRequiredArg().ofType(Integer.class).describedAs("The processingID of the child for this event, for constructing the dependency tree in the metadb");
        parser.accepts("metadata-childID-file", "Optional: The same as --metadata-childID, but is a path to a file, to parse for child processing ID's.").withRequiredArg().ofType(String.class).describedAs("Path to a line-delimeted file containing one or more child processing IDs");

        // Output for our new ID
        parser.accepts("metadata-processingID-file", "Optional: Specifies the path to a file, which we will write our processingID, for future processing events to parse.").withRequiredArg().ofType(String.class).describedAs("Path for where we should create a new file with our processing ID");
        parser.accepts("metadata-algorithm", "Required for new task groups: The algorithm name for this task group.").withRequiredArg().ofType(String.class).describedAs("Algorithm name");
        parser.accepts("metadata-description", "Optional: The description of this task group.").withRequiredArg().ofType(String.class).describedAs("Description");

        //If they pass in a processingID, then instead of adding a new one, just update this one 
        parser.accepts("processingID", "Optional: If specified, we won't create a new TaskGroup, but rather turn the specified ID into a task group and associate it with specified parents and children").withRequiredArg().ofType(Integer.class).describedAs("ProcessingID of the soon-to-be Task Group");

        options = parser.parse(args);
        
      } catch (OptionException e) {
        getSyntax(parser, e.getMessage());
      }

      // Check if help was requested
      if (options.has("help") || options.has("h") || options.has("?")) {
        getSyntax(parser, "");
      }

      
      
      // Parse MetadataDB options. If have what we need, then try to connect
      MetadataDB meta = null;
      if (options.has("metadata-config-database") && options.has("metadata-config-username") && options.has("metadata-config-password") ) {
        // Try to connect and exit if there was a problem.
        meta = new MetadataDB();
        ReturnValue ret = meta.init( (String) options.valueOf("metadata-config-database"), (String) options.valueOf("metadata-config-username"), (String) options.valueOf("metadata-config-password") );
        if ( ret.getExitStatus() != ReturnValue.SUCCESS) {
          Log.stderr( "ERROR connecting to metadata DB" );
          Log.stderr( ret.getStderr() );
          System.exit( ret.getExitStatus() );
        }
      }
      else {
        Log.stderr("You must specify metadata config: --metadata-config-database, --metadata-config-username and --metadata-config-password ");
      }

      // Find all parent processingIDs
      ArrayList<Integer> parentIDs = new ArrayList<Integer>();
      ArrayList<Integer> childIDs = new ArrayList<Integer>();
      ArrayList<File> processingIDFiles = new ArrayList<File>();

      if ( meta != null ) {
        for (Integer parent : (List<Integer>)options.valuesOf("metadata-parentID")) {
          parentIDs.add(parent);
        }
        for (String file : (List<String>)options.valuesOf("metadata-parentID-file")) {
          try {
            BufferedReader r;
            String line = null;
            r = new BufferedReader(new FileReader(file));
            
            while( (line = r.readLine()) != null ) {
              try {
                parentIDs.add( Integer.parseInt(line) );
              }
              catch (NumberFormatException ex) {
                Log.stderr("Non number found when parsing parentID file '" + line + "'");
                System.exit(ReturnValue.METADATAINVALIDIDCHAIN);
              }
            }
          } catch (Exception e) {
            Log.stderr("Could not open parentID file for metadata: " + e.getMessage());
            System.exit(ReturnValue.METADATAINVALIDIDCHAIN);
          }
        }
        
        // Find all child processingIDs
          for (Integer child : (List<Integer>)options.valuesOf("metadata-childID")) {
            childIDs.add(child);
          }
          for (String file : (List<String>)options.valuesOf("metadata-childID-file")) {
            try {
              BufferedReader r;
              String line = null;
              r = new BufferedReader(new FileReader(file));
              
              while( (line = r.readLine()) != null ) {
                try {
                  childIDs.add( Integer.parseInt(line) );
                }
                catch (NumberFormatException ex) {
                  Log.stderr("Non number found when parsing childID file '" + line + "'");
                  System.exit(ReturnValue.METADATAINVALIDIDCHAIN);
                }
              }
            } catch (Exception e) {
              Log.stderr("Could not open childID file for metadata: " + e.getMessage());
              System.exit(ReturnValue.METADATAINVALIDIDCHAIN);
            }

        // Create new file to write ID to if specified
        
        for (String outputFile : (List<String>)options.valuesOf("metadata-processingID-file")) {
          File file1 = new File(outputFile);
          
          try {
            if ( (file1.exists() || file1.createNewFile()) && file1.canWrite() ) {
              processingIDFiles.add(file1);
            } 
            else {
              Log.stderr("Could not create processingID File for metadata");
              System.exit(ReturnValue.METADATAINVALIDIDCHAIN);
            }
          } catch (IOException e) {
            Log.stderr("Could not create processingID File for metadata: " + e.getMessage());
            System.exit(ReturnValue.METADATAINVALIDIDCHAIN);
          }
        }
      }
      
      // Create a processing event associating metadata with parents and writing to child files
      int processingID = 0;
      String algorithm = null;
      String description = null;

      int[] parents = new int[ parentIDs.size() ];
      for (int i = 0 ; i < parentIDs.size() ; i++ ) {
        parents[i] = parentIDs.get(i);
      }
      int[] children = new int[ childIDs.size() ];
      for (int i = 0 ; i < childIDs.size() ; i++ ) {
        children[i] = childIDs.get(i);
      }

      // Add task group, or convert the processingID into one
      if ( ! options.has("processingID") ) {
        if ( options.has("metadata-algorithm") ) {
         algorithm = (String) options.valueOf("metadata-algorithm");
        }
        else {
          Log.stderr("When adding a task group, you must specify an algorithm name");
          System.exit(ReturnValue.INVALIDPARAMETERS);
        }
      
        if ( options.has("metadata-description") ) {
          description = (String) options.valueOf("metadata-description");
        }
      
        ReturnValue metaret = meta.add_task_group( parents, children, algorithm, description );
      
        if ( metaret.getExitStatus() == ReturnValue.SUCCESS ) {
          processingID = metaret.getReturnValue();
          Log.stderr( "MetaDB ProcessingID for this run is: " + processingID );
        }
        else {
          Log.stderr("MetaDB failed with exit: " + metaret.getExitStatus() );
          if ( metaret.getStdout() != null ) {
            Log.info( "STDOUT: " + metaret.getStdout() );  
          }
          if ( metaret.getStderr() != null ) {
            Log.stderr( "STDERR: " + metaret.getStderr() );  
          }
          
          System.exit( ReturnValue.SQLQUERYFAILED );
        }
      }
      // Otherwise convert the specified one
      else {
        processingID = (Integer) options.valueOf("processingID");
        if ( options.has("metadata-algorithm") ) {
          algorithm = (String) options.valueOf("metadata-algorithm");
        }
        if ( options.has("metadata-description") ) {
          description = (String) options.valueOf("metadata-description");
        }
         
        ReturnValue metaret = meta.processing_event_to_task_group( processingID, parents, children, algorithm, description );
        if ( metaret.getExitStatus() != ReturnValue.SUCCESS ) {
          Log.stderr("MetaDB failed with exit: " + metaret.getExitStatus() );
          if ( metaret.getStdout() != null ) {
            Log.info( "STDOUT: " + metaret.getStdout() );  
          }
          if ( metaret.getStderr() != null ) {
            Log.stderr( "STDERR: " + metaret.getStderr() );  
          }
          
          System.exit( ReturnValue.SQLQUERYFAILED );
        }
        
        // Try to write to each processingIDFile until success or timeout
        for (File file : processingIDFiles ) {
          int maxTries = (Integer) options.valueOf("metadata-tries-number");
          for ( int i = 0 ; i < maxTries ; i++ ) {
            // Break on success
            if ( LockingFileTools.lockAndAppend(file, processingID + System.getProperty("line.separator") ) ) {
              break;
            }
            // Sleep if going to try again
            else if ( i < maxTries ) {
              ProcessTools.sleep( (Integer) options.valueOf("metadata-tries-delay") );
            }
            // Return error if failed on last try
            else {
              ReturnValue retval = new ReturnValue();
              retval.printAndAppendtoStderr("Could not write to processingID File for metadata");
              retval.setExitStatus(ReturnValue.METADATAINVALIDIDCHAIN);
              meta.update_processing_event(processingID, retval);
              meta.update_processing_status(processingID, ProcessingStatus.failed);
              System.exit(retval.getExitStatus());
            }
          }
        }
      }

      // If none of the methods exited, it was a success
      if ( meta != null && processingID != 0 ) {
        meta.update_processing_status( processingID, ProcessingStatus.success);
      }
      System.exit(ReturnValue.SUCCESS);
    }
  }

}
