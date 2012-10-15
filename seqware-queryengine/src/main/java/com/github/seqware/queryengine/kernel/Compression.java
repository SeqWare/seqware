package com.github.seqware.queryengine.kernel;

import org.apache.commons.lang.StringUtils;

/**
 * Static methods for compressing/decompressing user data.
 *
 * @author jbaran
 * @version $Id: $Id
 */
public class Compression {

    /** Constant <code>MAX_BASE=94</code> */
    public static final int MAX_BASE = 94;

    /**
     * Returns the compressed surrogate for a particular SO accession. The compressed
     * surrogate does not reflect the whole hierarchy the term appears in!
     *
     * @param sequenceOntologyAccession SO accession that should be compressed.
     * @return Compressed SO accession.
     */
    public static String getSequenceOntologyAccessionSurrogate(String sequenceOntologyAccession) {
        if (!sequenceOntologyAccession.startsWith("SO:"))
            throw new IllegalArgumentException("Argument is not an SO accession.");

        return "SO:" + toBaseN(Integer.parseInt(sequenceOntologyAccession.replaceFirst("^SO:", "")), MAX_BASE);
    }

    /**
     * <p>getSequenceOntologyAccession.</p>
     *
     * @param surrogate a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public static String getSequenceOntologyAccession(String surrogate) {
        if (!surrogate.startsWith("SO:"))
            throw new IllegalArgumentException("Argument is not an SO accession.");

        String accession = Integer.toString(fromBaseN(surrogate.replaceFirst("^SO:", ""), MAX_BASE));

        return "SO:" + StringUtils.repeat(" ", 7 - accession.length()) + accession;
    }

    // The following two methods have been adapted from:
    //    http://en.wikipedia.org/wiki/Hexavigesimal

    /**
     * Converts an integer into a string representation of certain base.
     *
     * @param number Integer whose string representation is desired.
     * @param base Base to use for encoding the integer, max. MAX_BASE.
     * @return String representation of the provided integer in the given base.
     */
    public static String toBaseN(int number, int base){
        number = Math.abs(number);
        String converted = "";
        // Repeatedly divide the number by 26 and convert the
        // remainder into the appropriate letter.
        do
        {
            int remainder = number % base;
            converted = (char)(remainder + '!') + converted;
            number = (number - remainder) / base;
        } while (number > 0);

        return converted;
    }

    /**
     * Returns the integer extracted from the given string in a certain base.
     *
     * @param number String representation of the integer.
     * @param base Base of the integer in the string, max. MAX_BASE.
     * @return Integer value of the string value.
     */
    public static int fromBaseN(String number, int base) {
        int s = 0;
        if (number != null && number.length() > 0) {
            s = (number.charAt(0) - '!');
            for (int i = 1; i < number.length(); i++) {
                s *= base;
                s += (number.charAt(i) - '!');
            }
        }
        return s;
    }

}
