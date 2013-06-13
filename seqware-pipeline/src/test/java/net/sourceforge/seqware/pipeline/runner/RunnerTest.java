package net.sourceforge.seqware.pipeline.runner;

//import org.aspectj.weaver.ast.Test;
import java.io.File;
import java.security.Permission;
import java.util.Arrays;
import java.util.Collection;
import net.sourceforge.seqware.common.util.Log;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Testing the runner class.
 * <p/>
 * Created by IntelliJ IDEA. User: xiao Date: 7/21/11 Time: 3:22 PM To change
 * this template use File | Settings | File Templates.
 *
 * @author boconnor
 * @version $Id: $Id
 * @since 0.13.3
 */
@RunWith(Parameterized.class)
public class RunnerTest {

    private String testArgs;
    private int expected;
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
    public static void setUpClassProperty() {
        rscDir = new File(RunnerTest.class.getResource(".").getPath());
    }

    /**
     * <p>setup.</p>
     */
    @Before
    public void setup() {
        System.setSecurityManager(new NoExitSecurityManager());
    }

    /**
     * <p>teardown.</p>
     */
    @After
    public void teardown() {
        System.setSecurityManager(null);
    }

    /**
     * <p>arguments.</p>
     *
     * @return an array of {@link java.lang.Object} objects.
     */
    @Parameters
    public static Collection<Object[]> arguments() {
        Log.info("rscDir = " + rscDir);
        String stdoutFile = new File(rscDir, "runnertest.out").getAbsolutePath();
        String stderrFile = new File(rscDir, "runnertest.err").getAbsolutePath();
        String basic = "--no-metadata --module net.sourceforge.seqware.pipeline.module.TestModule";
        Log.info("stdoutFile = " + stdoutFile);
        Log.info("stderrFile = " + stderrFile);
        Object[][] data = new Object[][] {{basic, 0},
            {basic + " --output " + stdoutFile, 0},
            {basic + " --output " + stdoutFile + " --stderr " + stderrFile, 0}};
        return Arrays.asList(data);
    }

    /**
     * <p>testRunner.</p>
     *
     * @param args a {@link java.lang.String} object.
     * @param expected a int.
     */
    public RunnerTest(String args, int expected) {
        this.expected = expected;
        this.testArgs = args;
    }

    @Test
    public void executeParameterizedTest() {
        try {
            Runner.main(testArgs.split("\\s"));
        } catch (ExitException e) {
            Assert.assertEquals(e.status, expected);
        }

    }
}
