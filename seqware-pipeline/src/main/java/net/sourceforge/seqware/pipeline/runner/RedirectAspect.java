package net.sourceforge.seqware.pipeline.runner;

import net.sourceforge.seqware.common.err.SwBadFileException;
import net.sourceforge.seqware.pipeline.module.RedirectAware;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import net.sourceforge.seqware.common.util.Log;

/**
 * We need to clarify that there are two issues here.
 * (1) We need the capability to redirect stdout/stderr to a specified file name.
 * This need is solved here, i.e., in REDIRECT aspect
 * (2) We need the module capable of getting the stdout/stderr files.
 * This need will be solved by implementing RedirectAware interface
 * <p/>
 * Created by IntelliJ IDEA.
 * User: Xiaoshu Wang (xiao@renci.org)
 * Date: 8/12/11
 * Time: 2:04 PM
 *
 * @author boconnor
 * @version $Id: $Id
 */
@Aspect
public class RedirectAspect {
    @Option(name = "--stderr", aliases = {"-e"}, usage = "Files for redirecting stderr")
    private File stderr;

    @Option(name = "--stdout", aliases = {"-o"}, usage = "Files for redirecting stdout")
    private File stdout;

    /**
     * <p>doRun.</p>
     */
    @Pointcut("bean(module) && execution(* net.sourceforge.seqware.pipeline.module.ModuleInterface.do_run(..))")
    public void doRun() {}

    /**
     * <p>watchModuleRun2.</p>
     *
     * @param joinPoint a {@link org.aspectj.lang.ProceedingJoinPoint} object.
     */
    @Around("doRun()")
    public void watchModuleRun2(ProceedingJoinPoint joinPoint) {
        Log.info("RedirectAspect.watchModuleRun2");
        PrintStream old_out = System.out;
        PrintStream old_err = System.err;

        if (joinPoint.getTarget() instanceof RedirectAware){
            RedirectAware rd = (RedirectAware)joinPoint.getTarget();
            rd.setStderr(stderr);
            rd.setStdout(stdout);
        }
        if (stdout != null) {
            try {
                System.setOut(new PrintStream(new FileOutputStream(stdout)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new SwBadFileException(e);
            }
        }

        if (stderr != null) {
            try {
                System.setErr(new PrintStream(new FileOutputStream(stderr)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new SwBadFileException(e);
            }
        }

        try {
            joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            System.setOut(old_out);
            System.setErr(old_err);
        }
    }
}
