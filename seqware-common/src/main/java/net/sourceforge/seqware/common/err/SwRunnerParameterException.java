package net.sourceforge.seqware.common.err;

import net.sourceforge.seqware.common.err.SeqwareExitCode;
import net.sourceforge.seqware.common.err.SeqwareException;

/**
 * User: Xiaoshu Wang (xiao@renci.org)
 * Date: 9/6/11
 * Time: 3:20 PM
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class SwRunnerParameterException extends SeqwareException{

    /** {@inheritDoc} */
    @Override
    public int getExitCode() {
        return SeqwareExitCode.BadRunnerParams;
    }

    /**
     * <p>Constructor for SwRunnerParameterException.</p>
     */
    public SwRunnerParameterException() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    /**
     * <p>Constructor for SwRunnerParameterException.</p>
     *
     * @param s a {@link java.lang.String} object.
     */
    public SwRunnerParameterException(String s) {
        super(s);    //To change body of overridden methods use File | Settings | File Templates.
    }

    /**
     * <p>Constructor for SwRunnerParameterException.</p>
     *
     * @param s a {@link java.lang.String} object.
     * @param throwable a {@link java.lang.Throwable} object.
     */
    public SwRunnerParameterException(String s, Throwable throwable) {
        super(s, throwable);    //To change body of overridden methods use File | Settings | File Templates.
    }

    /**
     * <p>Constructor for SwRunnerParameterException.</p>
     *
     * @param throwable a {@link java.lang.Throwable} object.
     */
    public SwRunnerParameterException(Throwable throwable) {
        super(throwable);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
