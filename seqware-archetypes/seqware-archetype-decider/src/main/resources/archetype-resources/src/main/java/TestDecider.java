package ${package};

import java.util.*;
import net.sourceforge.seqware.common.module.FileMetadata;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.pipeline.deciders.BasicDecider;

/**
 *
 * @author mtaschuk
 */
public class TestDecider extends BasicDecider {

    public TestDecider() {
        super();
    }

    @Override
    public ReturnValue init() {
        Log.debug("INIT");
        //this.setHeader(Header.IUS_SWA);
        this.setMetaType(Arrays.asList("application/bam"));

        //allows anything defined on the command line to override the defaults here.
        ReturnValue val = super.init();
        return val;

    }

    @Override
    protected String handleGroupByAttribute(String attribute) {
        Log.debug("GROUP BY ATTRIBUTE: " + getHeader().getTitle() + " " + attribute);
        return attribute;
    }

    @Override
    protected boolean checkFileDetails(ReturnValue returnValue, FileMetadata fm) {
        Log.debug("CHECK FILE DETAILS:" + fm);
        //pathToAttributes.put(fm.getFilePath(), returnValue);
        return super.checkFileDetails(returnValue, fm);
    }

    @Override
    protected Map<String, String> modifyIniFile(String commaSeparatedFilePaths, String commaSeparatedParentAccessions) {
        Log.debug("INI FILE:" + commaSeparatedFilePaths);

        Map<String, String> iniFileMap = new TreeMap<String, String>();
        iniFileMap.put("input_file", commaSeparatedFilePaths);

        return iniFileMap;
    }
}
