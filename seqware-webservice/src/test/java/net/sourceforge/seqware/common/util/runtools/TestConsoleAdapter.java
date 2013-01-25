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
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import junit.framework.Assert;

/**
 * Allows us to test the interactive console. To use, instantiate the object and
 * call setLine(String) for each line that you want to read.
 *
 * @author mtaschuk
 */
public class TestConsoleAdapter extends ConsoleAdapter {
    
    private Map<String, String> line;
    private Iterator<String> iterator;

    /**
     * Get the value of line
     *
     * @return the value of line
     */
    public Map<String, String> getLine() {
        return line;
    }

    /**
     * Set the value of the line to return in the 'readLine' and 'readPassword'
     * methods.
     *
     * @param line new value of line
     */
    public void setLine(Map<String, String> line) {
        this.line = line;
    }
    
    private String get(String field) {
        for (String key : line.keySet()) {
            if (field.contains(key)) {
                return line.get(key);
            }
        }
        Assert.fail("TestConsoleAdapter does not have a value for "+field);
        return null;
    }
    
    @Override
    public void flush() {
    }
    
    @Override
    /**
     * Returns null.
     */
    public Console format(String fmt, Object... args) {
        return null;
    }
    
    @Override
    /**
     * Returns null.
     */
    public Console printf(String format, Object... args) {
        return null;
    }
    
    @Override
    public String readLine() {
        return "";
    }
    
    @Override
    public String readLine(String fmt, Object... args) {
        return get(fmt);
    }
    
    @Override
    public char[] readPassword() {
        return "".toCharArray();
    }
    
    @Override
    public char[] readPassword(String fmt, Object... args) {
        return get(fmt).toCharArray();
    }
    
    @Override
    /**
     * Wraps System.in in an InputStreamReader.
     */
    public Reader reader() {
        return new InputStreamReader(System.in);
    }
    
    @Override
    /**
     * Wraps System.out in a PrintWriter.
     */
    public PrintWriter writer() {
        return new PrintWriter(System.out);
    }
    
    public static TestConsoleAdapter initializeTestInstance() {
        instance = new TestConsoleAdapter();
        return (TestConsoleAdapter) instance;
    }
}
