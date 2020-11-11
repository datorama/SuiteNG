/*
 * Copyright (c) 2020, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.scanners;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.logging.Log;
import org.testng.annotations.Test;

import com.salesforce.filters.AnnotationsFilter;
import com.salesforce.filters.Filter;
import com.salesforce.filters.MethodsFilter;

public class FilesScanner {

	private static final String DEFAULT_FILE_EXTENSION = ".class";

	private Log log;
	private URLClassLoader urlClassLoader;
	private Map<Class<?>, List<Method>> scanResultsMap = new HashMap<>();
	private static FilesScanner instance;

	private FilesScanner() {
	}

	public void init(URLClassLoader urlClassLoader, Log log) {
		this.log = log;
		this.urlClassLoader = urlClassLoader;
	}

	public static FilesScanner getInstance() {
		if (instance == null) {
			synchronized (FilesScanner.class) {
				if (instance == null) {
					instance = new FilesScanner();
				}

			}
		}
		return instance;
	}

	public void scan(String directoryPath) {
		scan(directoryPath, DEFAULT_FILE_EXTENSION);
	}

	public void scan(String directoryPath, String fileExtension) {

		scanResultsMap.clear();
		try (Stream<Path> walk = Files.walk(Paths.get(directoryPath)).collect(Collectors.toList()).parallelStream()) {
			walk.map(x -> x.toString()).filter(f -> f.endsWith(fileExtension)).collect(Collectors.toList()).parallelStream().forEach(filename -> {
				String classname = convertFileAbsolutePathToClassCanonicalName(filename, directoryPath, fileExtension);
				log.debug(String.format("File absolute path: %s - Class canonical name: %s", filename, classname));

				try {
					Class classObject = urlClassLoader.loadClass(classname);

					List<Method> methodsList = new LinkedList<>();
					Method[] methods = classObject.getDeclaredMethods();

					for (Method method : methods) {
						if (method.isAnnotationPresent(Test.class)) {
							methodsList.add(method);
						}
					}
					scanResultsMap.put(classObject, methodsList);
				} catch (Exception e) {
					log.debug(e);
				}
			});
		} catch (Exception e) {
			log.debug(e);
		}
	}

	public Map<Class<?>, List<Method>> getRawResults() {
		log.debug("Number of classes found in scan: " + scanResultsMap.keySet().size());
		return scanResultsMap;
	}

	public Map<Class<?>, List<Method>> getFilteredResults(List<Filter> includedFilters, List<Filter> excludedFilters) {

		if (includedFilters.isEmpty() && excludedFilters.isEmpty()) {
			return scanResultsMap;
		}

		Map<Class<?>, List<Method>> filteredMap = new HashMap<>();

		scanResultsMap.forEach((clazz, methods) -> {
			List<Method> filteredClassMethods = new LinkedList<>();
			methods.forEach(method -> {
				if (isAtLeastOneFilterMatch(method, includedFilters) && !isAtLeastOneFilterMatch(method, excludedFilters)) {
					filteredClassMethods.add(method);
				}
			});
			if (!filteredClassMethods.isEmpty()) {
				filteredMap.put(clazz, filteredClassMethods);
			}
		});

		log.debug("Number of classes found after filters applied: " + filteredMap.keySet().size());
		return filteredMap;
	}

	private boolean isAtLeastOneFilterMatch(Method method, List<Filter> filters) {

		AtomicBoolean isMatch = new AtomicBoolean(false);

		filters.forEach(filter -> {
			if (filter instanceof AnnotationsFilter) {
				if (isAnnotationsFilterMatch(method, (AnnotationsFilter) filter)) {
					isMatch.set(true);
				}
			}
			if (filter instanceof MethodsFilter) {
				if (isMethodsFilterMatch(method, (MethodsFilter) filter)) {
					isMatch.set(true);
				}
			}
		});

		return isMatch.get();
	}

	private boolean isMethodsFilterMatch(Method method, MethodsFilter filter) {
		AtomicBoolean isMatch = new AtomicBoolean(false);

		if (StringUtils.isEmpty(filter.getClassName())) {
			if (StringUtils.isNotEmpty(filter.getMethodName())) {
				if(StringUtils.equalsIgnoreCase(filter.getMethodName(),method.getName())) {
					isMatch.set(true);
				}
			}
		}
		else if (StringUtils.equalsIgnoreCase(filter.getClassName(),method.getDeclaringClass().getCanonicalName())) {
			if (StringUtils.isNotEmpty(filter.getMethodName())) {
				if(StringUtils.equalsIgnoreCase(filter.getMethodName(),method.getName())) {
					isMatch.set(true);
				}
			} else { // No method defined by the user in filter --> all methods in class will match
				isMatch.set(true);
			}
		}

		return isMatch.get();
	}

	private boolean isAnnotationsFilterMatch(Method method, AnnotationsFilter filter) {

		AtomicBoolean isMatch = new AtomicBoolean(false);

		filter.getAnnotationsFilterMap().forEach((expectedAnnotation, expectedAttributes) -> {
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
					log.debug(e);
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

	private String convertFileAbsolutePathToClassCanonicalName(String absolutePath, String absolutePathDirsPrefix, String fileExtension) {

		String canonicalClassname = absolutePath.replace(fileExtension, "")
				.replace(absolutePathDirsPrefix, "")
				.replaceAll(File.separator, ".");

		return canonicalClassname;
	}

}
