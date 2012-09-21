package net.sourceforge.seqware.common.util;


import java.security.Permission;

/**
 * Created by IntelliJ IDEA.
 * User: Xiaoshu Wang (xiao@renci.org)
 * Date: 8/12/11
 * Time: 9:42 AM
 */
public class NoExitSecurityManager extends SecurityManager{
        @Override
        public void checkPermission(Permission permission) {
            //allow anything
        }

        @Override
        public void checkPermission(Permission permission, Object o) {
            //allow everything
        }

        @Override
        public void checkExit(int status) {
            super.checkExit(status);
            throw new ExitException(status);
        }
}
