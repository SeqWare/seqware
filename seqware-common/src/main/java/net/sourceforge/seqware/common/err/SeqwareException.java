package net.sourceforge.seqware.common.err;

/**
 * Created by IntelliJ IDEA.
 * User: Xiaoshu Wang (xiao@renci.org)
 * Date: 8/11/11
 * Time: 10:27 PM
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class SeqwareException extends RuntimeException {

    /**
     * <p>getExitCode.</p>
     *
     * @return a int.
     */
    public int getExitCode() {
        return SeqwareExitCode.Unclassified;
    }

    /**
     * <p>Constructor for SeqwareException.</p>
     */
    public SeqwareException() {
        super();
    }

    /**
     * <p>Constructor for SeqwareException.</p>
     *
     * @param s a {@link java.lang.String} object.
     */
    public SeqwareException(String s) {
        super(s);
    }

    /**
     * <p>Constructor for SeqwareException.</p>
     *
     * @param s a {@link java.lang.String} object.
     * @param throwable a {@link java.lang.Throwable} object.
     */
    public SeqwareException(String s, Throwable throwable) {
        super(s, throwable);
    }

    /**
     * <p>Constructor for SeqwareException.</p>
     *
     * @param throwable a {@link java.lang.Throwable} object.
     */
    public SeqwareException(Throwable throwable) {
        super(throwable);
    }
}
