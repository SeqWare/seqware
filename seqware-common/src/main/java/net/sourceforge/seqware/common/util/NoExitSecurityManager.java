package net.sourceforge.seqware.common.util;


import java.security.Permission;

/**
 * Created by IntelliJ IDEA.
 * User: Xiaoshu Wang (xiao@renci.org)
 * Date: 8/12/11
 * Time: 9:42 AM
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class NoExitSecurityManager extends SecurityManager{
        /** {@inheritDoc} */
        @Override
        public void checkPermission(Permission permission) {
            //allow anything
        }

        /** {@inheritDoc} */
        @Override
        public void checkPermission(Permission permission, Object o) {
            //allow everything
        }

        /** {@inheritDoc} */
        @Override
        public void checkExit(int status) {
            super.checkExit(status);
            throw new ExitException(status);
        }
}
