package net.sourceforge.seqware.common.err;

/**
 * Created by IntelliJ IDEA.
 * User: Xiaoshu Wang (xiao@renci.org)
 * Date: 8/11/11
 * Time: 10:27 PM
 */
public class SeqwareException extends RuntimeException {

    public int getExitCode() {
        return SeqwareExitCode.Unclassified;
    }

    public SeqwareException() {
        super();
    }

    public SeqwareException(String s) {
        super(s);
    }

    public SeqwareException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public SeqwareException(Throwable throwable) {
        super(throwable);
    }
}
