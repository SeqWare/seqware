package net.sourceforge.solexatools.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileFinder {
    
    private Pattern p = null;
    private Matcher m = null;
    
    private long totalLength = 0;
    private long filesNumber = 0;
    private long directoriesNumber = 0;
    
    private final int FILES = 0;
    private final int DIRECTORIES = 1;
    private final int ALL = 2;
    
    public FileFinder() {
    }
    
    public FileFinder(Pattern p) {
    	this.p = p;
    }

    public List findAll(String startPath) throws Exception {
        return find(startPath, "", ALL);
    }

    public List findAll(String startPath, String mask)
            throws Exception {
        return find(startPath, mask, ALL);
    }

    public List findFiles(String startPath)
            throws Exception {
        return find(startPath, "", FILES);
    }

    public List findFiles(String startPath, String mask)
            throws Exception {
        return find(startPath, mask, FILES);
    }

    public List findDirectories(String startPath)
            throws Exception {
        return find(startPath, "", DIRECTORIES);
    }

    public List findDirectories(String startPath, String mask)
            throws Exception {
        return find(startPath, mask, DIRECTORIES);
    }
    
    public long getDirectorySize() {
        return totalLength;
    }
    
    public long getFilesNumber() {
        return filesNumber;
    }
    
    public long getDirectoriesNumber() {
        return directoriesNumber;
    }
    
    private boolean accept(String name) {
        if(p == null) {
            return true;
        }
        m = p.matcher(name);
        if(m.matches()) {
            return true;
        }
        else {
            return false;
        }
    }
    
    private List find(String startPath, String mask, int objectType)
            throws Exception 
    {
        if(startPath == null || mask == null) {
            throw new Exception("Error: dont set params");
        }
        File topDirectory = new File(startPath);
        if(!topDirectory.exists()) {
            throw new Exception("Error: Directory dont exists");
        }
        
        if(!mask.equals("")) {
            p = Pattern.compile(mask,
                    Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        }
        filesNumber = 0;
        directoriesNumber = 0;
        totalLength = 0;
        ArrayList res = new ArrayList(100);
        
        search(topDirectory, res, objectType);
        
        p = null;
        return res;
    }
    
    private void search(File topDirectory, List res, int objectType) {
        File[] list = topDirectory.listFiles();
        for(int i = 0; i < list.length; i++) {
            if(list[i].isDirectory()) {
                if(objectType != FILES && accept(list[i].getName())) {
                    directoriesNumber++;
                    res.add(list[i]);
                }
                search(list[i], res, objectType);
            }
            else {
                if(objectType != DIRECTORIES && accept(list[i].getName())) {
                    filesNumber++;
                    totalLength += list[i].length();
                    res.add(list[i]);
                }
            }
        }
    }
}
