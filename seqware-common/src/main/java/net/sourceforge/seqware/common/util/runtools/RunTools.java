package net.sourceforge.seqware.common.util.runtools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;

import net.sourceforge.seqware.common.util.iotools.BufferedReaderThread;

/**
 * <p>RunTools class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class RunTools {

    /**
     * <p>runCommand.</p>
     *
     * @param env a {@link java.util.Map} object.
     * @param command a {@link java.lang.String} object.
     * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     */
    public static ReturnValue runCommand(Map<String, String> env, String command) {
        return (RunTools.runCommand(env, splitCommandPreserveQuote(command)));
    }

    /**
     * <p>startCommand.</p>
     *
     * @param env a {@link java.util.Map} object.
     * @param command a {@link java.lang.String} object.
     * @return a {@link java.lang.Process} object.
     * @throws java.io.IOException if any.
     */
    public static Process startCommand(Map<String, String> env, String command) throws IOException {
        return (RunTools.startCommand(env, splitCommandPreserveQuote(command)));
    }

    /**
     * <p>runCommand.</p>
     *
     * @param command a {@link java.lang.String} object.
     * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     */
    public static ReturnValue runCommand(String command) {
        return (RunTools.runCommand(splitCommandPreserveQuote(command)));
    }

    /**
     * <p>startCommand.</p>
     *
     * @param command a {@link java.lang.String} object.
     * @return a {@link java.lang.Process} object.
     * @throws java.io.IOException if any.
     */
    public static Process startCommand(String command) throws IOException {
        return (RunTools.startCommand(splitCommandPreserveQuote(command)));
    }

    /**
     * <p>runCommand.</p>
     *
     * @param command an array of {@link java.lang.String} objects.
     * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     */
    public static ReturnValue runCommand(String[] command) {
        return (RunTools.runCommand(null, command));
    }

    /**
     * <p>startCommand.</p>
     *
     * @param command an array of {@link java.lang.String} objects.
     * @return a {@link java.lang.Process} object.
     * @throws java.io.IOException if any.
     */
    public static Process startCommand(String[] command) throws IOException {
        return (RunTools.startCommand(null, command));
    }

    /**
     * A simple command runner that captures the return value of the program. If
     * the command was OK it should (in most cases) return 0. This util assumes
     * this is the case.
     *
     * FIXME: Jordan, the Process object does not start the command within a
     * shell environment, so if you're depending on environmental variables
     * you'll run into problems, see
     * http://java.sun.com/javase/6/docs/api/java/lang/Process.html
     *
     * @param command an array of {@link java.lang.String} objects.
     * @param env a {@link java.util.Map} object.
     * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     */
    public static ReturnValue runCommand(Map<String, String> env, String[] command) {
        ReturnValue ret = new ReturnValue();

        Process p = null;
//        Log.debug("Env:"+env);
//        Log.debug("Command:");
//        for (String s: command)
//        {
//            Log.debug(s);
//        }
//        Log.debug("End Command");
        try {
            p = startCommand(env, command);
        } catch (Exception e) {
            // make sure return value is not success if we got a exception! 
            if (p != null) {
                ret.setProcessExitStatus(p.exitValue());
            } else {
                Log.error("The result of the process was null - env:" + env + " cmd:" + command, e);
                ret.setProcessExitStatus(ReturnValue.PROGRAMFAILED);
            }

            if (ret.getExitStatus() == ReturnValue.SUCCESS) {
                Log.error("An exception was thrown but the return code was 0 - success", e);
                ret.setExitStatus(ReturnValue.PROGRAMFAILED);
            }

            e.printStackTrace();
            ret.setStderr(e.getMessage());
            return ret;
        }

        ret = waitAndGetReturn(p);
        return ret;
    }

    /**
     * Wait on a process to finish, then parse information into return value
     *
     * @param p a {@link java.lang.Process} object.
     * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     */
    public static ReturnValue waitAndGetReturn(Process p) {
        ReturnValue ret = new ReturnValue();

        // Make sure we weren't passed a null process
        if (p == null) {
            // FIXME: Return ReturnValue indicating it was NULL, and there was an error
        }

        try {
            // Spawn reader threads to grab stdout and stderr
            BufferedReaderThread stdOutThread = new BufferedReaderThread(p.getInputStream());
            BufferedReaderThread stdErrThread = new BufferedReaderThread(p.getErrorStream());
            stdOutThread.start();
            stdErrThread.start();

            // Wait on thread and process
            stdOutThread.join();
            stdErrThread.join();
            p.waitFor();

            // When all is done, get return and output
            ret.setProcessExitStatus(p.exitValue());

            // save output
            ret.setStdout(stdOutThread.getOutput().toString());
            ret.setStderr(stdErrThread.getOutput().toString());
            ret.setExitStatus(ReturnValue.SUCCESS);

            // Check for errors
            if (p.exitValue() != 0) {
                Log.stdout(stdOutThread.getOutput().toString());
                Log.stderr(stdErrThread.getOutput().toString());
                Log.error("The exit value was "+p.exitValue());
                ret.setExitStatus(ReturnValue.PROGRAMFAILED);
            }
            if (stdOutThread.getError() != null) {
                ret.getStderr().concat("Reading stdout threw an exception:" + stdOutThread.getError());
                ret.setExitStatus(ReturnValue.PROGRAMFAILED);
            }
            if (stdErrThread.getError() != null) {
                ret.getStderr().concat("Reading stderr threw an exception:" + stdErrThread.getError());
                ret.setExitStatus(ReturnValue.PROGRAMFAILED);
            }

        } catch (Exception e) {
            //  make sure return value is not success if we got a exception! 
            if (p != null) {
                ret.setProcessExitStatus(p.exitValue());
            } else {
                Log.error("The result of the process was null", e);
                ret.setProcessExitStatus(ReturnValue.PROGRAMFAILED);
            }
            if (ret.getExitStatus() == ReturnValue.SUCCESS) {
                Log.error("An exception was thrown but the return code was 0 - success", e);
                ret.setExitStatus(ReturnValue.PROGRAMFAILED);
            }
            e.printStackTrace();
            ret.getStderr().concat(e.getMessage());
        }

        return ret;
    }

    /**
     * An alternate runner that launches a process and returns it so that the
     * caller can process it's output directly. A use case is when we need to
     * output stdout or stderr to a file in real-time, or if it will be too
     * large to buffer in RAM and return.
     *
     * @throws java.io.IOException if any.
     * @param env a {@link java.util.Map} object.
     * @param command an array of {@link java.lang.String} objects.
     * @return a {@link java.lang.Process} object.
     */
    // FIXME: This should instantiate the shell. Modules should not need to say
    // FIXME:     RunTools.runCommand( new String[] { "bash", "-c", cmd.toString() } );
    // FIXME: Instead they should just say:
    // FIXME:     RunTools.runCommand( cmd.toString() ) with this function adding the bash -c logic
    // FIXME: Doing this will break modules, so when we do this, also need to remove the bash -c from whatever calls us
    public static Process startCommand(Map<String, String> env, String[] command) throws IOException {

        ProcessBuilder pb = new ProcessBuilder(command);
        Map<String, String> envMap = pb.environment();
        if (env != null) {
            for (String key : env.keySet()) {
                envMap.put(key, env.get(key));
            }
        }

        return pb.start();
    }

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    public static void main(String[] args) {
        StringBuffer sb = new StringBuffer();
        for (String token : args) {
            sb.append(token + " ");
        }
        ReturnValue rv = RunTools.runCommand(sb.toString().trim());
        Log.info("Process status: " + rv.getProcessExitStatus());
        Log.info("My status: " + rv.getExitStatus());
        Log.info("Error status: " + rv.getStderr());
        Log.info("Output:\n" + rv.getStdout());

    }

    /**
     * A little helper that splits up a command into an array but preserves any
     * options that are quoted (either ' or ") as single entries in the array
     * rather than further breaking them up.
     *
     * This won't work well if you have something like this:
     *
     * bash -c 'echo "foo 'bar' "'
     *
     * It won't like the single quotes inside single quotes.
     *
     * This method preserves the outside quotes so they will be passed to the
     * underlying shell
     *
     * @param command
     * @return
     */
    private static String[] splitCommandPreserveQuote(String command) {
        ArrayList<String> result = new ArrayList<String>();
        String[] tokens = command.split("\\s+");
        boolean matching = false;
        String quoteString = null;
        StringBuffer match = new StringBuffer();
        for (String t : tokens) {
            if (matching && !t.endsWith(quoteString)) {
                match.append(" " + t);
            } else if (!matching && t.startsWith("\"")) {
                quoteString = "\"";
                matching = true;
                match.append(t.substring(1));
            } else if (!matching && t.startsWith("'")) {
                quoteString = "'";
                matching = true;
                match.append(t.substring(1));
            } else if (matching && t.endsWith(quoteString)) {
                match.append(" " + t.substring(0, t.length() - 1));
                matching = false;
                quoteString = null;
                result.add(match.toString());
                match = new StringBuffer();
            } else {
                result.add(t);
            }
        }
        return (result.toArray(new String[0]));
    }
}
