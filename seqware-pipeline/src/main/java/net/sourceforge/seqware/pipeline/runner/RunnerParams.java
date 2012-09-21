package net.sourceforge.seqware.pipeline.runner;

import org.kohsuke.args4j.Option;

import java.util.*;

/**
 * This class is used to pass information from the Runner class to the Configuration class, which can instantiate the
 * module and setup the bean.
 * <p/>
 * Created by IntelliJ IDEA.
 * User: Xiaoshu Wang (xiao@renci.org)
 * Date: 8/11/11
 * Time: 2:48 PM
 */
public class RunnerParams {

    private Map<String, List<String>> params = new HashMap<String, List<String>>();

    public RunnerParams() {
    }

    public List<String> getArgs(String namespace) {
        return getArgs(namespace, true);
    }

    public List<String> getArgs(String namespace, boolean create) {
        if (params.containsKey(namespace)) {
            return params.get(namespace);
        } else {
            if (create) {
                List<String> args = new ArrayList<String>();
                params.put(namespace, args);
                return args;
            } else {
                return null;
            }
        }
    }

    public String get(String namespace, String key, boolean remove){
        String value = null;
        List<String> args = getArgs(namespace);
        if (args != null) {
            int vidx = args.indexOf(key);
            if (vidx >= 0){
                value = args.get(vidx + 1);
                if (remove) {
                    args.remove(vidx);
                    args.remove(vidx);
                }
            }
        }
        return value;
    }

    public void add(String namespace, String item){
        getArgs(namespace).add(item);
    }

    public Set<String> keys() {
        return params.keySet();
    }

    public static interface Bean {
        String APP = "run";
        String MODULE = "module";
        String LOG = "log";
        String REDIRECT = "redirect";
    }
}
