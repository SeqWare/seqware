package net.sourceforge.seqware.common.err;

import net.sourceforge.seqware.common.err.SeqwareExitCode;
import net.sourceforge.seqware.common.err.SeqwareException;

/**
 * User: Xiaoshu Wang (xiao@renci.org)
 * Date: 9/1/11
 * Time: 2:56 PM
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class SwBadModuleException extends SeqwareException{

    /** {@inheritDoc} */
    @Override
    public int getExitCode() {
        return SeqwareExitCode.BadModule;
    }

    /**
     * <p>Constructor for SwBadModuleException.</p>
     */
    public SwBadModuleException() {
        super();
    }

    /**
     * <p>Constructor for SwBadModuleException.</p>
     *
     * @param s a {@link java.lang.String} object.
     */
    public SwBadModuleException(String s) {
        super(s);
    }

    /**
     * <p>Constructor for SwBadModuleException.</p>
     *
     * @param s a {@link java.lang.String} object.
     * @param throwable a {@link java.lang.Throwable} object.
     */
    public SwBadModuleException(String s, Throwable throwable) {
        super(s, throwable);
    }

    /**
     * <p>Constructor for SwBadModuleException.</p>
     *
     * @param throwable a {@link java.lang.Throwable} object.
     */
    public SwBadModuleException(Throwable throwable) {
        super(throwable);
    }
}
