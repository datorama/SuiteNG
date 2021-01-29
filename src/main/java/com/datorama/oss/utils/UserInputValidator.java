package com.datorama.oss.utils;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class UserInputValidator {

	public static boolean validateListContainsNoNullElements(List<String> input) {
		return input.removeIf(Objects::isNull);
	}

	public static String validatePathEndsWithFileSeparator(String input) {
		return (input.endsWith(File.separator)) ? input : (input + File.separator);
	}
}
