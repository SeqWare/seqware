package net.sourceforge.seqware.pipeline.runner;

//import org.aspectj.weaver.ast.Test;
import org.testng.annotations.*;

import java.io.File;
import java.security.Permission;
import net.sourceforge.seqware.common.util.Log;

import static org.testng.Assert.assertEquals;

/**
 * Testing the runner class.
 * <p/>
 * Created by IntelliJ IDEA.
 * User: xiao
 * Date: 7/21/11
 * Time: 3:22 PM
 * To change this template use File | Settings | File Templates.
 *
 * @author boconnor
 * @version $Id: $Id
 * @since 0.13.3
 */
public class RunnerTest {

    private static File rscDir;

    class NoExitSecurityManager extends SecurityManager {
        @Override
        public void checkPermission(Permission permission) {
            //allow anything
        }

        @Override
        public void checkPermission(Permission permission, Object o) {
            //allow everything
        }

        @Override
        public void checkExit(int status) {
            super.checkExit(status);
            throw new ExitException(status);
        }
    }

    class ExitException extends SecurityException {
        public final int status;

        public ExitException(int st) {
            super("No exit");
            status = st;
        }
    }

    /**
     * <p>setUpClassProperty.</p>
     */
    @BeforeClass
    public void setUpClassProperty() {
        rscDir = new File(RunnerTest.class.getResource(".").getPath());
    }

    /**
     * <p>setup.</p>
     */
    @BeforeMethod
    public void setup() {
        System.setSecurityManager(new NoExitSecurityManager());
    }

    /**
     * <p>teardown.</p>
     */
    @AfterMethod
    public void teardown() {
        System.setSecurityManager(null);
    }

    /**
     * <p>arguments.</p>
     *
     * @return an array of {@link java.lang.Object} objects.
     */
    @DataProvider(name = "args")
    public Object[][] arguments() {
        Log.info("rscDir = " + rscDir);
        String stdoutFile = new File(rscDir, "runnertest.out").getAbsolutePath();
        String stderrFile = new File(rscDir, "runnertest.err").getAbsolutePath();
        String basic = "--no-metadata --module net.sourceforge.seqware.pipeline.module.TestModule";
        Log.info("stdoutFile = " + stdoutFile);
        Log.info("stderrFile = " + stderrFile);
        return new Object[][]{
//                {"--no-metadata", -1},
                {basic, 0},
                {basic + " --output " + stdoutFile, 0},
                {basic + " --output " + stdoutFile + " --stderr " + stderrFile, 0}
        };
    }

    /**
     * <p>testRunner.</p>
     *
     * @param args a {@link java.lang.String} object.
     * @param expected a int.
     */
    @Test(dataProvider = "args")
    public void testRunner(String args, int expected) {
        try {

            Runner.main(args.split("\\s"));
        } catch (ExitException e) {
            assertEquals(e.status, expected);
        }
    }

}


