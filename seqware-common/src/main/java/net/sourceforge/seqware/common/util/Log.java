/*
 * Copyright (C) 2012 SeqWare
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.seqware.common.util;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * <p>Log class.</p>
 *
 * @author yongliang
 * @version $Id: $Id
 */
public class Log {
    
    private static boolean verbose;
    /**
     * See {@link org.apache.log4j.Logger#debug(Object)}.
     *
     * @param message
     *            the message to log.
     */
    public static void trace(final Object message) {
        Logger logger = Logger.getLogger(getCallerClassName());
        if(verbose)
            setVerboseLogger(logger);
        logger.trace(message);
    }

    /**
     * See {@link org.apache.log4j.Logger#debug(Object,Throwable)}.
     *
     * @param message
     *            the message to log.
     * @param t
     *            the error stack trace.
     */
    public static void trace(final Object message, final Throwable t) {
        Logger logger = Logger.getLogger(getCallerClassName());
        if(verbose)
            setVerboseLogger(logger);
        logger.trace(message,t);
    }

    /**
     * See {@link org.apache.log4j.Logger#debug(Object)}.
     *
     * @param message
     *            the message to log.
     */
    public static void debug(final Object message) {
        Logger logger = Logger.getLogger(getCallerClassName());
        if(verbose)
            setVerboseLogger(logger);
        logger.debug(message);
    }

    /**
     * See {@link org.apache.log4j.Logger#debug(Object,Throwable)}.
     *
     * @param message
     *            the message to log.
     * @param t
     *            the error stack trace.
     */
    public static void debug(final Object message, final Throwable t) {
        Logger logger = Logger.getLogger(getCallerClassName());
        if(verbose)
            setVerboseLogger(logger);
        logger.debug(message,t);
    }

    /**
     * See {@link org.apache.log4j.Logger#info(Object)}.
     *
     * @param message
     *            the message to log.
     */
    public static void info(final Object message) {
        Logger logger = Logger.getLogger(getCallerClassName());
        if(verbose)
            setVerboseLogger(logger);
        logger.info(message);
   }

    /**
     * See {@link org.apache.log4j.Logger#info(Object,Throwable)}.
     *
     * @param message
     *            the message to log.
     * @param t
     *            the error stack trace.
     */
    public static void info(final Object message, final Throwable t) {
        Logger logger = Logger.getLogger(getCallerClassName());
        if(verbose)
            setVerboseLogger(logger);
        logger.info(message, t);
    }

    /**
     * See {@link org.apache.log4j.Logger#warn(Object)}.
     *
     * @param message
     *            the message to log.
     */
    public static void warn(final Object message) {
        Logger logger = Logger.getLogger(getCallerClassName());
        if(verbose)
            setVerboseLogger(logger);
        logger.warn(message);
    }

    /**
     * See {@link org.apache.log4j.Logger#warn(Object,Throwable)}.
     *
     * @param message
     *            the message to log.
     * @param t
     *            the error stack trace.
     */
    public static void warn(final Object message, final Throwable t) {
        Logger logger = Logger.getLogger(getCallerClassName());
        if(verbose)
            setVerboseLogger(logger);
        logger.warn(message, t);
    }

    /**
     * See {@link org.apache.log4j.Logger#error(Object)}.
     *
     * @param message
     *            the message to log.
     */
    public static void error(final Object message) {
        Logger logger = Logger.getLogger(getCallerClassName());
        if(verbose)
            setVerboseLogger(logger);
        logger.error(message);
    }

    /**
     * See {@link org.apache.log4j.Logger#error(Object,Throwable)}.
     *
     * @param message
     *            the message to log.
     * @param t
     *            the error stack trace.
     */
    public static void error(final Object message, final Throwable t) {
        Logger logger = Logger.getLogger(getCallerClassName());
        if(verbose)
            setVerboseLogger(logger);
        logger.error(message, t);
    }

    /**
     * See {@link org.apache.log4j.Logger#fatal(Object)}.
     *
     * @param message
     *            the message to log.
     */
    public static void fatal(final Object message) {
        Logger logger = Logger.getLogger(getCallerClassName());
        if(verbose)
            setVerboseLogger(logger);
        logger.fatal(message);
    }

    /**
     * See {@link org.apache.log4j.Logger#fatal(Object,Throwable)}.
     *
     * @param message
     *            the message to log.
     * @param t
     *            the error stack trace.
     */
    public static void fatal(final Object message, final Throwable t) {
        Logger logger = Logger.getLogger(getCallerClassName());
        if(verbose)
            setVerboseLogger(logger);
        logger.fatal(message, t);
    }
    
    /**
     * <p>stdout.</p>
     *
     * @param message a {@link java.lang.String} object.
     */
    public static void stdout(final String message) {
	    System.out.println(message);
    }
    
    /**
     * <p>stderr.</p>
     *
     * @param message a {@link java.lang.String} object.
     */
    public static void stderr(final String message) {
	    System.err.println(message);
    } 
    
    /**
     * override the log4j.properties
     *
     * @param b a boolean.
     */
    public static void setVerbose(boolean b) {
        Log.verbose = b;
    }

    // Private means that this class is a static singleton.
    private Log() {
    }

       /**
     * Info about the logger caller
     */
    private static class CallInfo {

        public String className;
        public String methodName;

        public CallInfo() {
        }

        public CallInfo(String className, String methodName) {
            this.className = className;
            this.methodName = methodName;
        }
    }

    /**
     * @return the className of the class actually logging the message
     */
    private static String getCallerClassName() {
        final int level = 5;
        return getCallerClassName(level);
    }

    /**
     * @return the className of the class actually logging the message
     */
    private static String getCallerClassName(final int level) {
        CallInfo ci = getCallerInformations(level);
        return ci.className;
    }

    /**
     * Examine stack trace to get caller
     * 
     * @param level
     *            method stack depth
     * @return who called the logger
     */
    private static CallInfo getCallerInformations(int level) {
        StackTraceElement[] callStack = Thread.currentThread().getStackTrace();
        StackTraceElement caller = callStack[level];
        return new CallInfo(caller.getClassName(), caller.getMethodName());
    }

    private static void setVerboseLogger(Logger logger) {
        logger.setLevel(Level.DEBUG);
        logger.removeAllAppenders();
        logger.addAppender(new ConsoleAppender(new PatternLayout("%p [%d{yyyy/MM/dd HH:mm:ss}] | %m%n")));
    }
}
