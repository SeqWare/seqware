package net.sourceforge.seqware.common.err;

import net.sourceforge.seqware.common.err.SeqwareExitCode;
import net.sourceforge.seqware.common.err.SeqwareException;

/**
 * Created by IntelliJ IDEA.
 * User: Xiaoshu Wang (xiao@renci.org)
 * Date: 8/12/11
 * Time: 2:26 PM
 */
public class SwBadFileException extends SeqwareException {
//    public final static int exitCode = SeqwareExitCode.BadFile;

    @Override
    public int getExitCode() {
        return SeqwareExitCode.BadFile;
    }

    public SwBadFileException() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public SwBadFileException(String s) {
        super(s);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public SwBadFileException(String s, Throwable throwable) {
        super(s, throwable);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public SwBadFileException(Throwable throwable) {
        super(throwable);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
