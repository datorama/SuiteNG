package com.datorama;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.maven.plugin.logging.Log;

public class Utils {

	public static String execCommand(String command, String[] envParameters, File dir, Log log) {

		Runtime rt = Runtime.getRuntime();
		Process pr;
		String buffer = null;

		try {
			pr = rt.exec(command, envParameters, dir);
			buffer = new BufferedReader(new InputStreamReader(pr.getInputStream())).lines()
					.parallel().collect(Collectors.joining(System.lineSeparator()));
		} catch (IOException e) {
			log.error(e);
		}

		return Optional.ofNullable(buffer).orElse("");
	}

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
