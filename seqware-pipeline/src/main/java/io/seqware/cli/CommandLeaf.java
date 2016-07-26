package io.seqware.cli;

import java.util.List;

interface CommandLeaf {

    /**
     * Output options for Bash completion
     * @return output options for Bash completion
     */
    default String bashOpts(){
        return "";
    }

    /**
     * Key for preceding layer
     * @return Key for preceding layer
     */
    String getCommand();

    /**
     * Call this command (with the preceding tree removed)
     * @param args arguments
     */
    void invoke(List<String> args);

    /**
     * Display a description of the command
     * @return a one line description of the command
     */
    String displayOneLineDescription();
}
