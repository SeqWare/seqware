package net.sourceforge.seqware.pipeline.module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Interface for declaring when to do the
 * Created by IntelliJ IDEA.
 * User: xiao
 * Date: 7/25/11
 * Time: 11:13 PM
 * To change this template use File | Settings | File Templates.
 *
 * @author boconnor
 * @version $Id: $Id
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface StdoutRedirect {
   // Added fully qualified enum name to work around a bug in the sun jdk.
   // http://stackoverflow.com/questions/1425088/incompatible-types-found-required-default-enums-in-annotations
   // http://bugs.sun.com/view_bug.do?bug_id=6512707
   net.sourceforge.seqware.pipeline.module.ModuleMethod startsBefore() default net.sourceforge.seqware.pipeline.module.ModuleMethod.do_run;
   net.sourceforge.seqware.pipeline.module.ModuleMethod endsAfter() default net.sourceforge.seqware.pipeline.module.ModuleMethod.do_run;
}
