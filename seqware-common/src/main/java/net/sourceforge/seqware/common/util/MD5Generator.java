package net.sourceforge.seqware.common.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;

/**
 * Utility class for calculating MD5 hash on a file's contents.
 * 
 * @author lmose
 */
public class MD5Generator {

    /**
     * Returns a 32 character representation of the MD5 hash of the specified
     * file's content.  Leading zeros are preserved.
     */
    public String md5sum(String filename) throws Exception {
        String result = "ERROR";

        //TODO: Attempt to run native md5sum implementation first.
        InputStream is = null;
        InputStream dis = null;
        InputStream bis = null;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            is = new FileInputStream(filename);
            dis =  new DigestInputStream(is, md);
            bis = new BufferedInputStream(dis);
            
            // Read the file and calculate the md5 hash 
            byte[] buffer = new byte[8192];
            while (bis.read(buffer) > -1);
            byte[] md5sum = md.digest();
            
            // Convert the hash to a string.
            BigInteger bigInt = new BigInteger(1, md5sum);
            result = String.format("%032x", bigInt);
        } finally {
            if (bis != null) bis.close();
            if (dis != null) dis.close();
            if (is != null) is.close();
        }

        return result;
    }
}
