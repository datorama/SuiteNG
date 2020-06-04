/*
 * Copyright (c) 2018, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.datorama;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.maven.plugin.logging.Log;

public class Commander {

	public static String executeCommand(String command, String[] envParameters, File dir, Log log) {

		Runtime runtime = Runtime.getRuntime();
		Process process;
		String buffer = null;

		try {
			process = runtime.exec(command, envParameters, dir);
			buffer = new BufferedReader(new InputStreamReader(process.getInputStream())).lines()
					.parallel().collect(Collectors.joining(System.lineSeparator()));
		} catch (IOException e) {
			log.error(e);
		}

		return Optional.ofNullable(buffer).orElse("");
	}
}
