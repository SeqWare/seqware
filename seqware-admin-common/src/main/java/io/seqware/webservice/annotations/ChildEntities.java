/**
 * 
 */
package io.seqware.webservice.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * This annotation is used to mark a getter method that will return an entity's child entities.
 * @author sshorser
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ChildEntities {
    public String tag() default "";
    public Class<?> childType();
}
