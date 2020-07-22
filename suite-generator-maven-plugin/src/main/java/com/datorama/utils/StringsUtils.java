package com.datorama.utils;

public class StringsUtils {

	public static String parseClassName(String input) {

		String className = input.split("#")[0];

		return className;
	}

	public static String parseMethodName(String input) {

		String methodName = "";

		if (input.contains("#")) {
			methodName = input.replaceAll(".+\\#|=.+", "");
		}

		return methodName;
	}

	public static String parseAttributeValue(String input) {

		String attributeValue = "";

		if (input.contains("=")) {
			attributeValue = input.replaceAll(".+=", "");
		}

		return attributeValue;
	}

}
