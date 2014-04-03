package net.sourceforge.seqware.common.err;

import net.sourceforge.seqware.common.module.ReturnValue;

/**
 * This exception wraps the old seqware exception code
 * User: Xiaoshu Wang (xiao@renci.org)
 * Date: 9/6/11
 * Time: 2:38 PM
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class OldSeqwareException extends SeqwareException{

    private int exitCode;
    private ReturnValue returnValue;

    /**
     * <p>Setter for the field <code>exitCode</code>.</p>
     *
     * @param exitCode a int.
     */
    public void setExitCode(int exitCode){
        this.exitCode = exitCode;
    }

    /**
     * <p>Getter for the field <code>returnValue</code>.</p>
     *
     * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     */
    public ReturnValue getReturnValue() {
        return returnValue;
    }

    /**
     * <p>Setter for the field <code>returnValue</code>.</p>
     *
     * @param returnValue a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     */
    public void setReturnValue(ReturnValue returnValue) {
        this.returnValue = returnValue;
    }

    /** {@inheritDoc} */
    @Override
    public int getExitCode() {
        return exitCode;
    }

    /**
     * <p>Constructor for OldSeqwareException.</p>
     */
    public OldSeqwareException() {
        super();
    }

    /**
     * <p>Constructor for OldSeqwareException.</p>
     *
     * @param s a {@link java.lang.String} object.
     */
    public OldSeqwareException(String s) {
        super(s);
    }

    /**
     * <p>Constructor for OldSeqwareException.</p>
     *
     * @param s a {@link java.lang.String} object.
     * @param throwable a {@link java.lang.Throwable} object.
     */
    public OldSeqwareException(String s, Throwable throwable) {
        super(s, throwable);
    }

    /**
     * <p>Constructor for OldSeqwareException.</p>
     *
     * @param throwable a {@link java.lang.Throwable} object.
     */
    public OldSeqwareException(Throwable throwable) {
        super(throwable);
    }
}
