package net.sourceforge.seqware.common.util;

public class Str {

    /**
     * Returns a String suitable to be used in multiple contexts, e.g., as part of a filename. The returned string will match [a-zA-Z0-9_-]+
     * 
     * Any characters outside this range will be replaced with an underscore, and multiple underscores will be collapsed down to a single
     * underscore.
     * 
     * @param name
     *            a string to make safe
     * @return
     */
    public static String safe(String name) {
        name = name.replaceAll("[^a-zA-Z0-9_-]+", "_");
        name = name.replaceAll("_+", "_");
        return name;
    }

    /**
     * Whether or not the given string matches [a-zA-Z0-9_-]+
     * 
     * @param name
     *            the string to check
     * @return true if it is safe, false otherwise
     */
    public static boolean isSafe(String name) {
        return name.matches("[a-zA-Z0-9_-]+");
    }

    /**
     * Throws an exception if the string does not match [a-zA-Z0-9_-]+, otherwise a no-op.
     * 
     * @param name
     *            the string to check
     * @throws UnsafeName
     *             if the name is not safe
     */
    public static void ensureSafe(String name) throws UnsafeName {
        if (!isSafe(name)) {
            throw new UnsafeName("String is not safe: " + name);
        }
    }

    @SuppressWarnings("serial")
    public static class UnsafeName extends RuntimeException {
        public UnsafeName(String orig) {
            super("String is not safe: " + orig);
        }
    }
}
