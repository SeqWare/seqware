package io.seqware.cli;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Main {

  private static void invalid(String cmd) {
    System.out.println(String.format("seqware: '%s' is not a seqware command. See 'seqware --help'.", cmd));
    System.exit(1);
  }

  private static void invalid(String cmd, String sub) {
    System.out.println(String.format("seqware: '%s %s' is not a seqware command. See 'seqware %s --help'.", cmd, sub, cmd));
    System.exit(1);
  }  
  

  private static boolean isHelp(String cmd) {
    return "-h".equals(cmd) || "--help".equals(cmd);
  }

  private static boolean isDev() {
    return Boolean.parseBoolean(System.getenv("SEQWARE_DEV"));
  }

  private static void help() {
    System.out.println("usage: seqware [<flags>]");
    System.out.println("       seqware <command> --help");
    System.out.println("       seqware <command> [<args> | --help]");
    System.out.println("");
    System.out.println("commands:");
    System.out.println("   annotate      Add arbitrary key/value pairs to seqware objects");
    if (isDev())
    System.out.println("   bundle        Interact with a workflow bundle");
    System.out.println("   create        Create new seqware objects (e.g., study)");
    System.out.println("   dump          Create data dump of a study");
    System.out.println("   provision     Provide input files to seqware");
    System.out.println("   workflow      Interact with workflows");
    System.out.println("   workflow-run  Interact with workflow runs");
    System.out.println("");
    System.out.println("flags:");
    System.out.println("   -h --help     Print help info");
    System.out.println("   -v --version  Print Seqware's version"); // handled in seqware script

  }

  public static void main(String[] args) {
    if (args.length >= 1) {
      String cmd = args[0];
      if ("annotate".equals(cmd)) {
        annotate(args);
      } else if ("bundle".equals(cmd)) {
        bundle(args);
      } else if ("create".equals(cmd)) {
        create(args);
      } else if ("dump".equals(cmd)) {
        dump(args);
      } else if ("provision".equals(cmd)) {
        provision(args);
      } else if ("workflow".equals(cmd)) {
        workflow(args);
      } else if ("workflow-run".equals(cmd)) {
        workflowRun(args);
      } else if (isHelp(cmd)) {
        help();
      } else {
        invalid(cmd);
      }
    } else {
      help();
    }
  }

  private static void annotate(String[] args) {
    // TODO Auto-generated method stub
    
  }

  private static void bundle(String[] args) {
    // TODO Auto-generated method stub
    
  }

  private static void create(String[] args) {
    // TODO Auto-generated method stub
    
  }

  private static void dump(String[] args) {
    // TODO Auto-generated method stub
    
  }

  private static void provision(String[] args) {
    // TODO Auto-generated method stub
    
  }

  private static void workflow(String[] args) {
    // TODO Auto-generated method stub
    
  }

  private static void workflowRun(String[] args) {
    // TODO Auto-generated method stub
    
  }



}
