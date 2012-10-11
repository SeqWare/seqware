package net.sourceforge.seqware.common.err;

import net.sourceforge.seqware.common.err.SeqwareExitCode;
import net.sourceforge.seqware.common.err.SeqwareException;

/**
 * User: Xiaoshu Wang (xiao@renci.org)
 * Date: 9/6/11
 * Time: 3:20 PM
 */
public class SwRunnerParameterException extends SeqwareException{

    @Override
    public int getExitCode() {
        return SeqwareExitCode.BadRunnerParams;
    }

    public SwRunnerParameterException() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public SwRunnerParameterException(String s) {
        super(s);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public SwRunnerParameterException(String s, Throwable throwable) {
        super(s, throwable);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public SwRunnerParameterException(Throwable throwable) {
        super(throwable);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
