package net.sourceforge.seqware.common.util;

/**
 * Created by IntelliJ IDEA.
 * User: Xiaoshu Wang (xiao@renci.org)
 * Date: 8/12/11
 * Time: 9:44 AM
 */
public class ExitException extends SecurityException{
        public final int status;

        public ExitException(int st) {
            super("No exit");
            status = st;
        }
}
