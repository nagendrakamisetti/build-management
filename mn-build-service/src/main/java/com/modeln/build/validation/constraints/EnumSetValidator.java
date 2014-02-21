package com.modeln.build.validation.constraints;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.NotNull;

/**
 * Validator implementation behind the EnumSet bean validator.
 * 
 * @author gzussa
 *
 */
public class EnumSetValidator implements ConstraintValidator<EnumSet, String> {

	private Class<? extends Enum> enumClass;
	
	private String[] excludes;

	public void initialize(EnumSet enumSet) {
		this.enumClass = enumSet.value();
		this.excludes = enumSet.excludes();
	}
	
	/**
	 * Return Enum constants names for a given Enum class.
	 * @param enumType : Enum class to get constants from.
	 * @return List constants names defined in the given Enum class.
	 */
	public <T extends Enum<T>> List<String> enumValues(@NotNull Class<T> enumType) {
        List<String> result = new ArrayList<String>();
		for (T c : enumType.getEnumConstants()) {
			result.add(c.name());
        }
        return result;
	}
	
	/**
	 * Convert String Array into a list of String
	 * @param array : String Array
	 * @return List Object
	 */
	public List<String> convertToList(@NotNull String[] array) {
        List<String> result = new ArrayList<String>();
		for (String value : array) {
			result.add(value);
        }
        return result;
	}
	
	/**
	 * Validator entry point
	 * @param value :  string to be validated
	 * @param context : constraint validator context
	 * @return true is the value is valid, otherwise return false.
	 */
	public boolean isValid(@NotNull String value, @NotNull ConstraintValidatorContext context) {
		if (value == null || value.length() == 0) {
			return true;
		}
		
		String trimedValue = value.toUpperCase().trim();
		String[] valuesToCheck = trimedValue.split(",");
		
		List<String> enumValues = enumValues(enumClass);
		List<String> excludedValues = convertToList(excludes);
		
		Set<String> previousValues = new HashSet<String>();
		forEachValueToCheck: for(int i = 0; i < valuesToCheck.length; i++){
			for (String enumValue : enumValues) {
				//We check if :
				//1. the currentValue is a valid enum value
				//2. the currentValue is NOT a excluded value
				//3. the currentValue is unique
		        if (enumValues.contains(valuesToCheck[i]) 
		        		&& !excludedValues.contains(valuesToCheck[i]) 
		        		&& !previousValues.contains(valuesToCheck[i])) {
		        	previousValues.add(valuesToCheck[i]);
		        	continue forEachValueToCheck;
		        }else{
		        	return false;
		        }
		    }
		}
		return true;
	}
}
