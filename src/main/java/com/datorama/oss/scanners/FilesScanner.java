/*
 * Copyright (c) 2020, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.datorama.oss.scanners;

import java.io.File;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.maven.plugin.logging.Log;
import org.testng.annotations.Test;

import com.datorama.oss.filters.Filter;

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

		Map<Class<?>, List<Method>> filteredMap = new HashMap<>();
		AtomicInteger numOfMethodsMatchFilters = new AtomicInteger();

		if (includedFilters.isEmpty() && excludedFilters.isEmpty()) {
			return scanResultsMap;
		}

		log.debug("The list of methods after filters applied:");
		scanResultsMap.forEach((clazz, methods) -> {
			List<Method> filteredClassMethods = new LinkedList<>();
			methods.forEach(method -> {
				if (isAtLeastOneFilterMatch(method, includedFilters) && !isAtLeastOneFilterMatch(method, excludedFilters)) {
					filteredClassMethods.add(method);
					numOfMethodsMatchFilters.getAndIncrement();
					log.debug(clazz.getCanonicalName() + "#" + method.getName());
				}
			});
			if (!filteredClassMethods.isEmpty()) {
				filteredMap.put(clazz, filteredClassMethods);
			}
		});

		log.info("Total number of classes found after filters applied: " + filteredMap.keySet().size());
		log.info("Total number of methods found after filters applied: " + numOfMethodsMatchFilters.get());

		return filteredMap;
	}

	private boolean isAtLeastOneFilterMatch(Method method, List<Filter> filters) {

		AtomicBoolean isMatch = new AtomicBoolean(false);

		filters.forEach(filter -> {
			if (filter.isFilterMatch(method)) {
				isMatch.set(true);
			}
		});

		return isMatch.get();
	}

	private String convertFileAbsolutePathToClassCanonicalName(String absolutePath, String absolutePathDirsPrefix, String fileExtension) {

		String canonicalClassname = absolutePath.replace(fileExtension, "")
				.replace(absolutePathDirsPrefix, "")
				.replaceAll(File.separator, ".");

		return canonicalClassname;
	}

}
