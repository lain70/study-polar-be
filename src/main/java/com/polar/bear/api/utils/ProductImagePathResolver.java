package com.polar.bear.api.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class ProductImagePathResolver {
	private static final String BACKEND_MODULE_DIRECTORY = "polar-be";

	private ProductImagePathResolver() {
	}

	public static Path resolve(String configuredPath) {
		return resolve(configuredPath, Paths.get("").toAbsolutePath().normalize());
	}

	static Path resolve(String configuredPath, Path workingDirectory) {
		Path path = Paths.get(configuredPath);
		if (path.isAbsolute()) {
			return path.normalize();
		}

		Path backendModuleDirectory = workingDirectory.resolve(BACKEND_MODULE_DIRECTORY);
		if (Files.isDirectory(backendModuleDirectory.resolve("src/main"))) {
			return backendModuleDirectory.resolve(path).normalize();
		}
		return workingDirectory.resolve(path).normalize();
	}
}
