/*
 * Copyright (C) 2013 SeqWare
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
package net.sourceforge.seqware.common.util.runtools;

import java.io.Console;
import java.io.PrintWriter;
import java.io.Reader;
import net.sourceforge.seqware.common.util.Log;

/**
 *
 * @author mtaschuk
 */
public class ConsoleAdapter {

    protected static ConsoleAdapter instance = null;

    protected ConsoleAdapter() {
    }

    public static ConsoleAdapter getInstance() {
        if (instance == null) {
            instance = new ConsoleAdapter();
        }
        return instance;
    }

    public void flush() {
        if (System.console() != null) {
            System.console().flush();
        }
    }

    public Console format(String fmt, Object... args) {
        if (System.console() != null) {
            return System.console().format(fmt, args);
        } else {
            return null;
        }
    }

    public Console printf(String format, Object... args) {
        if (System.console() != null) {
            return System.console().printf(format, args);
        } else {
            return null;
        }
    }

    public Reader reader() {
        if (System.console() != null) {
            return System.console().reader();
        } else {
            return null;
        }
    }

    public String readLine() {
        if (System.console() != null) {
            return System.console().readLine();
        } else {
            return null;
        }
    }

    public String readLine(String fmt, Object... args) {
        if (System.console() != null) {
            return System.console().readLine(fmt, args);
        } else {
            return null;
        }
    }

    public char[] readPassword() {
        if (System.console() != null) {
            return System.console().readPassword();
        } else {
            return null;
        }
    }

    public char[] readPassword(String fmt, Object... args) {
        if (System.console() != null) {
            return System.console().readPassword(fmt, args);
        } else {
            return null;
        }
    }

    public PrintWriter writer() {
        if (System.console() != null) {
            return System.console().writer();
        } else {
            return null;
        }
    }

    public String promptString(String string, String deflt) {
        String title = null;

        String prompt = string + (deflt == null ? ":" : "[" + deflt + "] : ");
        int counter = 0;
        while (title == null && counter++ < 10) {
            System.out.println();
            title = ConsoleAdapter.getInstance().readLine(prompt);
            if (title.trim().isEmpty()) {
                title = deflt;
            }
        }
        return title;
    }

    public Integer promptInteger(String string, Integer deflt) {
        Integer title = null;
        String prompt = string + (deflt == null ? ":" : "[" + deflt + "] : ");
        int counter = 0;
        while (title == null && counter++ < 10) {
            System.out.println();
            String line = ConsoleAdapter.getInstance().readLine(prompt);
            if (line.trim().isEmpty()) {
                title = deflt;
            } else {
                try {
                    title = Integer.parseInt(line);
                } catch (NumberFormatException e) {
                    Log.stdout(string + " must be an integer.");
                }
            }
        }
        return title;
    }

    public Boolean promptBoolean(String string, Boolean deflt) {
        Boolean title = null;
        String prompt = string + (deflt == null ? ":" : "[" + deflt + "] : ");
        int counter = 0;
        while (title == null && counter++ < 10) {
            System.out.println();
            String line = ConsoleAdapter.getInstance().readLine(prompt);
            if (line.trim().isEmpty()) {
                title = deflt;
            } else {
                try {
                    title = Boolean.parseBoolean(line);
                } catch (NumberFormatException e) {
                    Log.stdout(string + " must be true or false.");
                }

            }
        }
        return title;
    }
}
