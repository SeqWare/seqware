package net.sourceforge.seqware.pipeline.module;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;

/**
 * This is an empty module mainly for testing purpose.
 * Created by IntelliJ IDEA.
 * User: xiao
 * Date: 7/21/11
 * Time: 3:53 PM
 * To change this template use File | Settings | File Templates.
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class TestModule extends Module {

    /** {@inheritDoc} */
    @Override
    public String getAlgorithm() {
        return "TestModule";
    }

    /** {@inheritDoc} */
    @Override
    public ReturnValue do_run() {
        Log.info("stdout: TestModule.do_run");
        Log.error("stderr: TestModule.do_run");
        return new ReturnValue(ReturnValue.SUCCESS);
    }

    /** {@inheritDoc} */
    @Override
    public ReturnValue do_test() {
        Log.info("stdout: TestModule.do_test");
        Log.error("stderr: TestModule.do_test");
        return new ReturnValue(ReturnValue.SUCCESS);
    }

    /** {@inheritDoc} */
    @Override
    public ReturnValue do_verify_input() {
        Log.info("stdout: TestModule.do_verify_input");
        Log.error("stderr: TestModule.do_verify_input");
        return new ReturnValue(ReturnValue.SUCCESS);
    }

    /** {@inheritDoc} */
    @Override
    public ReturnValue do_verify_parameters() {
        Log.info("stdout: TestModule.do_verify_parameters");
        Log.error("stderr: TestModule.do_verify_parameters");
        return new ReturnValue(ReturnValue.SUCCESS);
    }

    /** {@inheritDoc} */
    @Override
    public ReturnValue do_verify_output() {
        Log.info("stdout: TestModule.do_verify_output");
        Log.error("stderr: TestModule.do_verify_output");
        return new ReturnValue(ReturnValue.SUCCESS);
    }

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    public static void main(String[] args) {
        File file  = new File("/home/xiao/work/test/sysout.log");
        PrintStream printStream = null;
        PrintStream curOut = System.out;
        try {
            printStream = new PrintStream(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Log.info("1");

        System.setOut(printStream);


        Log.info("2");

        System.setOut(curOut);

        Log.info("3");
    }
}
