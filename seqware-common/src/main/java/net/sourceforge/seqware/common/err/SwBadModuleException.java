package net.sourceforge.seqware.common.err;

import net.sourceforge.seqware.common.err.SeqwareExitCode;
import net.sourceforge.seqware.common.err.SeqwareException;

/**
 * User: Xiaoshu Wang (xiao@renci.org)
 * Date: 9/1/11
 * Time: 2:56 PM
 */
public class SwBadModuleException extends SeqwareException{

    @Override
    public int getExitCode() {
        return SeqwareExitCode.BadModule;
    }

    public SwBadModuleException() {
        super();
    }

    public SwBadModuleException(String s) {
        super(s);
    }

    public SwBadModuleException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public SwBadModuleException(Throwable throwable) {
        super(throwable);
    }
}
