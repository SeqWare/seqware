package net.sourceforge.seqware.common.err;

import net.sourceforge.seqware.common.err.SeqwareExitCode;
import net.sourceforge.seqware.common.err.SeqwareException;

/**
 * Created by IntelliJ IDEA.
 * User: Xiaoshu Wang (xiao@renci.org)
 * Date: 8/11/11
 * Time: 10:40 PM
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class SwParameterException extends SeqwareException {
//    public final static int exitCode = SeqwareExitCode.BadParameter;

    /** {@inheritDoc} */
    @Override
    public int getExitCode() {
        return SeqwareExitCode.BadParameter;
    }

    /**
     * <p>Constructor for SwParameterException.</p>
     */
    public SwParameterException() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    /**
     * <p>Constructor for SwParameterException.</p>
     *
     * @param s a {@link java.lang.String} object.
     */
    public SwParameterException(String s) {
        super(s);    //To change body of overridden methods use File | Settings | File Templates.
    }

    /**
     * <p>Constructor for SwParameterException.</p>
     *
     * @param s a {@link java.lang.String} object.
     * @param throwable a {@link java.lang.Throwable} object.
     */
    public SwParameterException(String s, Throwable throwable) {
        super(s, throwable);    //To change body of overridden methods use File | Settings | File Templates.
    }

    /**
     * <p>Constructor for SwParameterException.</p>
     *
     * @param throwable a {@link java.lang.Throwable} object.
     */
    public SwParameterException(Throwable throwable) {
        super(throwable);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
