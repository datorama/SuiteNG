/*
 * Copyright (c) 2020, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.filters;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class AnnotationsFilter implements Filter {

	private Map<Class<? extends Annotation>, Map<String, String>> annotationsFilterMap; // contains annotations with attributes to filter

	AnnotationsFilter(Map<Class<? extends Annotation>, Map<String, String>> annotationsFilterMap) {
		this.annotationsFilterMap = annotationsFilterMap;
	}

	public Map<Class<? extends Annotation>, Map<String, String>> getAnnotationsFilterMap() {
		return annotationsFilterMap;
	}

	public void setAnnotationsFilterMap(Map<Class<? extends Annotation>, Map<String, String>> annotationsFilterMap) {
		this.annotationsFilterMap = annotationsFilterMap;
	}

	@Override
	public boolean isFilterMatch(Method method, Filter filter) {

		AtomicBoolean isMatch = new AtomicBoolean(false);
		AnnotationsFilter annotationsFilter = (AnnotationsFilter) filter;

		annotationsFilter.getAnnotationsFilterMap().forEach((expectedAnnotation, expectedAttributes) -> {
			if (method.isAnnotationPresent(expectedAnnotation)) {
				if (expectedAttributes.isEmpty()) {
					isMatch.set(true);
				} else {
					isMatch.set(isAttributesMatch(method, expectedAnnotation, expectedAttributes));
				}
			}
		});

		return isMatch.get();
	}

	private boolean isAttributesMatch(Method method, Class<? extends Annotation> expectedAnnotation, Map<String, String> expectedAttributes) {

		List<Boolean> matches = new LinkedList<>();

		Annotation annotation = method.getAnnotation(expectedAnnotation);
		Method[] methods = annotation.annotationType().getDeclaredMethods();

		for (Method annotationMethod : methods) {
			if (isAttributeMethod(annotationMethod)) {
				try {
					if (expectedAttributes.containsKey(annotationMethod.getName())) {
						String expectedValue = expectedAttributes.get(annotationMethod.getName());
						Object actualValue = annotationMethod.invoke(annotation, new Object[0]);

						if (compareAttributeValue(actualValue, expectedValue)) {
							matches.add(new Boolean(true));
						}
					}
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NullPointerException | ExceptionInInitializerError e) {
					System.out.println(e);
				}
			}
		}

		return (!expectedAttributes.isEmpty() && matches.size() == expectedAttributes.size());
	}

	private boolean compareAttributeValue(Object actualValue, String expectedValue) {

		if (actualValue instanceof Class) {
			if (StringUtils.equalsIgnoreCase(expectedValue, actualValue.toString())) {
				return true;
			}
		} else if (actualValue instanceof String[]) {
			if (ArrayUtils.contains((String[]) actualValue, expectedValue)) {
				return true;
			}
		}

		return false;
	}

	private boolean isAttributeMethod(Method method) {
		if (method.getParameterTypes().length == 0
				&& method.getReturnType() != void.class) {
			return true;
		} else {
			return false;
		}
	}
}
