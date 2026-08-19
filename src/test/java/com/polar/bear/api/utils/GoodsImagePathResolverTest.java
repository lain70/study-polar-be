package com.polar.bear.api.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class GoodsImagePathResolverTest {
	@Test
	void keepsAbsoluteStoragePath() {
		Path absolutePath = Path.of("/opt/polar-app/uploads/goods");

		assertThat(GoodsImagePathResolver.resolve(absolutePath.toString())).isEqualTo(absolutePath);
	}

	@Test
	void resolvesRelativeStoragePathToExistingDirectory() {
		assertThat(GoodsImagePathResolver.resolve("./uploads/goods"))
				.isEqualTo(Path.of("").toAbsolutePath().resolve("uploads/goods").normalize());
	}

	@Test
	void resolvesRelativeStoragePathFromWorkspaceRoot() {
		Path backendDirectory = Path.of("").toAbsolutePath().normalize();
		Path workspaceDirectory = backendDirectory.getParent();

		assertThat(GoodsImagePathResolver.resolve("./uploads/goods", workspaceDirectory))
				.isEqualTo(backendDirectory.resolve("uploads/goods").normalize());
	}
}
