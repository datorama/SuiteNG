package com.datorama;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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
