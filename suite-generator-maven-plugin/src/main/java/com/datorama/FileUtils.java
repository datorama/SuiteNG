/*
 * Copyright (c) 2020, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.datorama;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.maven.plugin.logging.Log;

public class FileUtils {

	public static void writeFile(String pathname, String content, Log log) {

		Path path = Paths.get(pathname);

		try {
			Files.write(path, content.getBytes());
			log.info(String.format("File %s generated successfully", path.toAbsolutePath()));
		} catch (IOException e) {
			log.error(String.format("Failed to generate file %s", path.toAbsolutePath()) + e);
		}
	}
}
