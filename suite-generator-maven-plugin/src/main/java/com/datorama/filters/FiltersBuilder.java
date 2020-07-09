/*
 * Copyright (c) 2020, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.datorama.filters;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

public class FiltersBuilder {

	public static List<Filter> buildTestngGroupsFilters(List<String> groupsValues) {

		final String TESTNG_TEST_ANNOTATION = "org.testng.annotations.Test";
		final String GROUPS = "groups";

		List<String> filters = new ArrayList<>();

		if (groupsValues != null) {
			groupsValues.forEach(groupValue -> {
				filters.add(TESTNG_TEST_ANNOTATION + "#" + GROUPS + "=" + groupValue);
			});
		}

		return buildAnnotationFilters(filters);
	}

	public static List<Filter> buildAnnotationFilters(List<String> filters) {

		List<Filter> annotationFiltersList = new ArrayList<>();

		if (filters != null) {
			filters.forEach(filter -> {
				String className = parseClassName(filter);
				String methodName = parseMethod(filter);
				String attributeValue = parseAttributeValue(filter);

				Map<String, String> attributes = (Strings.isNullOrEmpty(methodName)) ? ImmutableMap.of() : ImmutableMap.of(methodName, attributeValue);
				Map<Class<? extends Annotation>, Map<String, String>> annotationsFilterMap = ImmutableMap.of(Objects.requireNonNull(getAnnotationClass(className)), attributes);
				Filter annotationsFilter = new AnnotationsFilter(annotationsFilterMap);
				annotationFiltersList.add(annotationsFilter);
			});
		}
		return annotationFiltersList;
	}

	private static String parseClassName(String input) {

		String className = input.split("#")[0];

		return className;
	}

	private static String parseMethod(String input) {

		String attributeMethodName = "";

		if (input.contains("#")) {
			attributeMethodName = input.replaceAll(".+\\#|=.+", "");
		}

		return attributeMethodName;
	}

	private static String parseAttributeValue(String input) {

		String attributeValue = "";

		if (input.contains("=")) {
			attributeValue = input.replaceAll(".+=", "");
		}

		return attributeValue;
	}

	private static Class<?> getClass(String className) {
		Class<?> cls = null;
		try {
			cls = Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return cls;
	}

	private static Class<? extends Annotation> getAnnotationClass(String className) {

		Class<?> cls = getClass(className);
		if (cls != null) {
			return (Class<? extends Annotation>) getClass(className);
		}

		return null;
	}

}
