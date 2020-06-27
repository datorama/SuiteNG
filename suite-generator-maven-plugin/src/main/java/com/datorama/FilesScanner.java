/*
 * Copyright (c) 2020, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.datorama;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.logging.Log;

public class FilesScanner {

	private static final String DOT_CHARACTER = ".";
	private static final String DEFAULT_FILE_EXTENSION = ".class";

	private Log log;
	private URLClassLoader urlClassLoader;
	private Map<Class<?>, List<Method>> scanResultsMap = new HashMap<>();            // contains all methods per classname

	public FilesScanner(URLClassLoader urlClassLoader, Log log) {
		this.log = log;
		this.urlClassLoader = urlClassLoader;
	}

	public void scan(String directoryPath) {
		scan(directoryPath, DEFAULT_FILE_EXTENSION);
	}

	public void scan(String directoryPath, String fileExtension) {

		scanResultsMap.clear();
		try (Stream<Path> walk = Files.walk(Paths.get(directoryPath)).collect(Collectors.toList()).parallelStream()) {

			walk.map(x -> x.toString()).filter(f -> f.endsWith(fileExtension)).collect(Collectors.toList()).parallelStream().forEach(filename -> {
				log.debug("File absolute path: " + filename);
				String classname = convertFileAbsolutePathToClassCanonicalName(filename, directoryPath, fileExtension);
				log.debug("Class canonical name: " + classname);

				Class classObject;

				try {
					classObject = urlClassLoader.loadClass(classname);

					List<Method> methodsList = new LinkedList<>();
					Method[] methods = classObject.getDeclaredMethods();

					for (Method method : methods) {
						methodsList.add(method);
					}
					scanResultsMap.put(classObject, methodsList);

				} catch (Throwable e) {
					log.debug(e);
				}
			});

		} catch (Exception e) {
			log.debug(e);
		}
	}

	public Map<Class<?>, List<Method>> getRawResults() {

		log.debug("Scan All results:");
		log.debug("Number of All classes " + scanResultsMap.keySet().size());
		log.debug("All Classes:" + scanResultsMap.keySet().toString());
		log.debug("All Methods:" + Arrays.toString(scanResultsMap.values().toArray()));
		return scanResultsMap;
	}

	public Map<Class<?>, List<Method>> getResultsFilteredByTestAnnotation(List<AnnotationsFilter> filters) {

		Map<Class<?>, List<Method>> filteredMap = new HashMap<>();

		scanResultsMap.forEach((clazz, methods) -> {
			List<Method> filteredClassMethods = new LinkedList<>();
			methods.forEach(method -> {
				if (hasAtLeastOneFilterMatch(method, filters)) {
					filteredClassMethods.add(method);
				}
			});
			if (!filteredClassMethods.isEmpty()) {
				filteredMap.put(clazz, filteredClassMethods);
			}
		});

		log.debug("Scan filtered by TestNG @Test with Attributes results:");
		log.debug("Number of Filtered classes " + filteredMap.keySet().size());
		log.debug("Filtered Classes:" + filteredMap.keySet().toString());
		log.debug("Filtered Methods:" + Arrays.toString(filteredMap.values().toArray()));

		return filteredMap;
	}

	private boolean hasAtLeastOneFilterMatch(Method method, List<AnnotationsFilter> filters) {

		AtomicBoolean isMatch = new AtomicBoolean(false);

		filters.forEach( filter -> {
			if (hasFilterMatch(method, filter)) {
				isMatch.set(true);
			}
		});

		return isMatch.get();
	}

	private boolean hasFilterMatch(Method method, AnnotationsFilter filter) {

		AtomicBoolean isMatch = new AtomicBoolean(true);

		filter.getAnnotationsFilterMap().forEach((annotation, attributes) -> {
			if (!hasAnnotationAttributesMatch(method, annotation, attributes)){
				isMatch.set(false); // if at least one of the annotations or attributes are not matched, the method will not have a filter match
			}
		});

		return isMatch.get();
	}

	private boolean hasAnnotationAttributesMatch(Method method, Class<? extends Annotation> expectedAnnotation, Map<String, String> expectedAnnotationAttributes) {

		List<Boolean> isMatchList = new LinkedList<>();

		if (method.isAnnotationPresent(expectedAnnotation)) {
			Annotation annotation = method.getAnnotation(expectedAnnotation);
			Method[] methods = annotation.annotationType().getDeclaredMethods();
			for (Method methodItr : methods) {
				if (methodItr.getParameterTypes().length == 0
						&& methodItr.getReturnType() != void.class) {
					try {
						if (expectedAnnotationAttributes.containsKey(methodItr.getName())) {
							String expectedValue = expectedAnnotationAttributes.get(methodItr.getName());
							Object objActualValue = method.invoke(annotation, new Object[0]);

							if (objActualValue instanceof Class) {
								if (StringUtils.equals(expectedValue, objActualValue.toString())) {
									isMatchList.add(new Boolean(true));
								}
							} else if (objActualValue instanceof String[]) {
								if (ArrayUtils.contains((String[])objActualValue, expectedValue)) {
									isMatchList.add(new Boolean(true));
								}
							}
						}
					} catch (IllegalAccessException e) {
						log.debug(e);
					} catch (InvocationTargetException e) {
						log.debug(e);
					}
				}
			}
		}

		return (isMatchList.size() == expectedAnnotationAttributes.size());
	}

	private String convertFileAbsolutePathToClassCanonicalName(String absolutePath, String absolutePathDirsPrefix, String fileExtension) {

		String canonicalClassname = absolutePath.replace(fileExtension, "")
				.replace(absolutePathDirsPrefix, "")
				.replaceAll(File.separator, DOT_CHARACTER);

		return canonicalClassname;
	}
}
