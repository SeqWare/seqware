/**
 * 
 */
package io.seqware.webservice.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a database entity class as "skippable", meaning that the database table that the entity maps to has a "skip"
 * field, which can be set to TRUE or FALSE 
 * @author sshorser
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SkippableEntity {

    public String skipFieldName() default "skip";
}
