package net.sourceforge.seqware.common.err;

import net.sourceforge.seqware.common.err.SeqwareExitCode;
import net.sourceforge.seqware.common.err.SeqwareException;

/**
 * Created by IntelliJ IDEA.
 * User: Xiaoshu Wang (xiao@renci.org)
 * Date: 8/12/11
 * Time: 2:26 PM
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class SwBadFileException extends SeqwareException {
//    public final static int exitCode = SeqwareExitCode.BadFile;

    /** {@inheritDoc} */
    @Override
    public int getExitCode() {
        return SeqwareExitCode.BadFile;
    }

    /**
     * <p>Constructor for SwBadFileException.</p>
     */
    public SwBadFileException() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    /**
     * <p>Constructor for SwBadFileException.</p>
     *
     * @param s a {@link java.lang.String} object.
     */
    public SwBadFileException(String s) {
        super(s);    //To change body of overridden methods use File | Settings | File Templates.
    }

    /**
     * <p>Constructor for SwBadFileException.</p>
     *
     * @param s a {@link java.lang.String} object.
     * @param throwable a {@link java.lang.Throwable} object.
     */
    public SwBadFileException(String s, Throwable throwable) {
        super(s, throwable);    //To change body of overridden methods use File | Settings | File Templates.
    }

    /**
     * <p>Constructor for SwBadFileException.</p>
     *
     * @param throwable a {@link java.lang.Throwable} object.
     */
    public SwBadFileException(Throwable throwable) {
        super(throwable);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
