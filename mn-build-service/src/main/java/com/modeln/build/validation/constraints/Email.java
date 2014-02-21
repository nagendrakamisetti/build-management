package com.modeln.build.validation.constraints;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Checks that a given string is a well-formed email address.
 *
 * @author gzussa
 *
 */
@Size(min = 7)
@Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\."
+ "[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*"
+ "@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?")
@ReportAsSingleViolation
@Constraint(validatedBy = {})
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Email {
	
	String message() default "Invalid Email address";
	
	Class<?>[] groups() default {};
	
	Class<? extends Payload>[] payload() default {};

	@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
	@Retention(RetentionPolicy.RUNTIME)
	@interface List {
		Email[] value();
	}
}
