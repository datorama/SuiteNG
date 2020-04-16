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
