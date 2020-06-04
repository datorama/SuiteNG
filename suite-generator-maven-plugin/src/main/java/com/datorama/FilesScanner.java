/*
 * Copyright (c) 2018, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.datorama;

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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.maven.plugin.logging.Log;
import org.testng.annotations.Test;

public class FilesScanner {

	public static final String DOT = ".";

	private Log log;
	private URLClassLoader urlClassLoader;

	public FilesScanner(URLClassLoader urlClassLoader, Log log) {
		this.log = log;
		this.urlClassLoader = urlClassLoader;
	}

	public Map<String, List<String>> scanFilesMethodsWithAnnotations(String directoryPath, Class annotationClass, String fileExtension) {

		Map<String, List<String>> resultMap = new HashMap<>(); // contains all test methods per classname

		try (Stream<Path> walk = Files.walk(Paths.get(directoryPath)).collect(Collectors.toList()).parallelStream()) {

			walk.map(x -> x.toString()).filter(f -> f.endsWith(fileExtension)).collect(Collectors.toList()).parallelStream().forEach(classname -> {
				log.debug("File absolute path: " + classname);
				classname = getFileCanonicalPathFromAbsolutePath(classname, directoryPath);
				log.debug("File canonical path: " + classname);

				Class classObject;

				try {
					classObject = urlClassLoader.loadClass(classname);

					List<String> methodsList = new LinkedList<>();
					Method[] methods = classObject.getDeclaredMethods();

					for (Method method : methods) {
						if (method.isAnnotationPresent(annotationClass)) {
							Test testAnnotation = (Test) method.getAnnotation(annotationClass);
							log.debug("Test Annotation found in class [" + classObject.getName() + "] --> method [" + method.getName() + "] --> Test name [" + testAnnotation.testName() + "]");
							methodsList.add(method.getName());
						}
					}
					resultMap.put(classname, methodsList);

				} catch (Throwable e) {
					log.debug(e);
				}
			});

		} catch (Exception e) {
			log.debug(e);
		}

		return resultMap;
	}

	private String getFileCanonicalPathFromAbsolutePath(String absolutePath, String absolutePathDirsPrefix) {

		String canonicalPath = absolutePath.substring(0, absolutePath.indexOf(DOT))         //remove file extension
				.replace(absolutePathDirsPrefix, "")                            //remove absolute path directories;
				.replaceAll(File.separator, DOT);

		return canonicalPath;
	}

}
