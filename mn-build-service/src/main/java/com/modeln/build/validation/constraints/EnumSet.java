package com.modeln.build.validation.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Check that a given String is a comma separated list of enum values from the same Enum.
 * Attributes:	
 * - value : Enum class that constitute the enum set.
 * - excludes : list of excluded enum values.
 * 
 * @author gzussa
 *
 */
@Constraint(validatedBy = EnumSetValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumSet {
	
	String message() default "String is not a comma separeted list of enum {value} with exclusion of the following values {excludes}";
	
	Class<?>[] groups() default {};
	
	Class<? extends Payload>[] payload() default {};
	
	Class<? extends Enum<?>> value();
	
	String[] excludes() default {};
	
	@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
	@Retention(RetentionPolicy.RUNTIME)
	@interface List {
		EnumSet[] value();
	}
}
