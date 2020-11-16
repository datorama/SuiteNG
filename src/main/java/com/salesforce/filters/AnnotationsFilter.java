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

	private final Map<Class<? extends Annotation>, Map<String, String>> annotationsWithAttributesMap;

	AnnotationsFilter(Map<Class<? extends Annotation>, Map<String, String>> annotationsWithAttributesMap) {
		this.annotationsWithAttributesMap = annotationsWithAttributesMap;
	}

	public Map<Class<? extends Annotation>, Map<String, String>> getAnnotationsWithAttributesMap() {
		return annotationsWithAttributesMap;
	}

	@Override
	public boolean isFilterMatch(Method method) {

		AtomicBoolean isMatch = new AtomicBoolean(false);

		this.getAnnotationsWithAttributesMap().forEach((expectedAnnotation, expectedAttributes) -> {
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
						Object actualValue = annotationMethod.invoke(annotation);

						if (compareAttributeValue(actualValue, expectedValue)) {
							matches.add(Boolean.TRUE);
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
			return StringUtils.equalsIgnoreCase(expectedValue, actualValue.toString());
		} else if (actualValue instanceof String[]) {
			return ArrayUtils.contains((String[]) actualValue, expectedValue);
		}

		return false;
	}

	private boolean isAttributeMethod(Method method) {
		return method.getParameterTypes().length == 0
				&& method.getReturnType() != void.class;
	}
}
