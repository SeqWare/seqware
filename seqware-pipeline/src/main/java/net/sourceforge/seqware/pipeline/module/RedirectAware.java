package net.sourceforge.seqware.pipeline.module;

import java.io.File;

/**
 * This module, in conjunction with the Tag and Run annotation are used to replace the old Module with
 * the Runner code.
 *
 * The Methods used in ModuleInterface will be replaced by the Run annotation.
 * The stdout/stderr will be replaced with this interface.
 * Any metadata, such as, the algorithm will be replaced with the Tag annotation.
 *
 * Created by IntelliJ IDEA.
 * User: Xiaoshu Wang (xiao@renci.org)
 * Date: 8/22/11
 * Time: 3:08 PM
 */
public interface RedirectAware {
    void setStdout(File stdout);
    void setStderr(File stderr);
}
