package net.sourceforge.seqware.pipeline.module.ann;

import java.lang.annotation.*;

/**
 * This Annotation is used to tag a ModuleInterface. Once tagged, the ModuleInterface will be called with the following
 * sequence:
 *
 * The parameters will be setup via annotation following org.kohsuke.args4j's convention. After the following method
 * are called.
 *    do_verify_parameters()
 *    init()
 *
 * If this annotation is absent, then, the Seqware module must extend from Module. And the module's setParameters() is
 * called followed by
 *    init()
 *    do_verify_parameter().
 *
 * It is worth nothing that the sequence of the above two events are reversed. It makes more sense to verify the parameter
 * first before initiating a module. The latter is so called for backward compatibility.
 *
 * The rest of method will be called in the same sequence
 *    do_verify_input()
 *    do_run()
 *    do_verify_output()
 *    clean_up()
 *
 * I do not know how to use the do_test(), hence omitting it here.
 *
 * Another changes of using this annotation are:
 * (1) We will not use ReturnValue to signal success/failure. I find it inconvenient. In this new style, if things
 * goes wrong, you raise a SeqwareException, which is unchecked to help clean the API. Otherwise, it will be considered
 * a success. The method signature is reserved to minimize the change.
 *
 * (2) The stderr/stdout will be implemented as another interface called RedirectAware. If user needs to get stdout/stderr
 * they will simply call that method.
 *
 * User: Xiaoshu Wang (xiao@renci.org)
 * Date: 9/1/11
 * Time: 2:42 PM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface NewSeqwareStyle {
}
