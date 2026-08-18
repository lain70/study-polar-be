package com.polar.bear.api.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class ProductImagePathResolverTest {
	@Test
	void keepsAbsoluteStoragePath() {
		Path absolutePath = Path.of("/opt/polar-app/uploads/product");

		assertThat(ProductImagePathResolver.resolve(absolutePath.toString())).isEqualTo(absolutePath);
	}

	@Test
	void resolvesRelativeStoragePathToExistingDirectory() {
		assertThat(ProductImagePathResolver.resolve("./uploads/product"))
				.isEqualTo(Path.of("").toAbsolutePath().resolve("uploads/product").normalize());
	}

	@Test
	void resolvesRelativeStoragePathFromWorkspaceRoot() {
		Path backendDirectory = Path.of("").toAbsolutePath().normalize();
		Path workspaceDirectory = backendDirectory.getParent();

		assertThat(ProductImagePathResolver.resolve("./uploads/product", workspaceDirectory))
				.isEqualTo(backendDirectory.resolve("uploads/product").normalize());
	}
}
