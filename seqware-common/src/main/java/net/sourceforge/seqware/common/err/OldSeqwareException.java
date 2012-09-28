package net.sourceforge.seqware.common.err;

import net.sourceforge.seqware.common.module.ReturnValue;

/**
 * This exception wraps the old seqware exception code
 * User: Xiaoshu Wang (xiao@renci.org)
 * Date: 9/6/11
 * Time: 2:38 PM
 */
public class OldSeqwareException extends SeqwareException{

    private int exitCode;
    private ReturnValue returnValue;

    public void setExitCode(int exitCode){
        this.exitCode = exitCode;
    }

    public ReturnValue getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(ReturnValue returnValue) {
        this.returnValue = returnValue;
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }

    public OldSeqwareException() {
        super();
    }

    public OldSeqwareException(String s) {
        super(s);
    }

    public OldSeqwareException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public OldSeqwareException(Throwable throwable) {
        super(throwable);
    }
}
