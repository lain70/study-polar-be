package com.polar.bear.api.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.time.LocalDateTime;

import com.polar.bear.api.mappers.ProductInfoMapper;
import com.polar.bear.api.models.ProductImageDto;
import com.polar.bear.api.models.ProductInfoDto;
import com.polar.bear.api.utils.ProductImagePathResolver;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductInfoService {
	private static final String IMAGE_URL_PREFIX = "/product-images/";
	private static final long MAX_IMAGE_SIZE = 10L * 1024L * 1024L;
	private final ProductInfoMapper productInfoMapper;

	@Value("${product.image-storage-path:./uploads/product}")
	private String imageStoragePath;

	public Map<String, Object> selectProductList(String searchType, String keyword, LocalDateTime registeredFrom,
			LocalDateTime registeredTo, List<String> statuses, List<String> brands, List<String> categories,
			int page, int size) throws Exception {
		int safePage = Math.max(page, 1);
		int safeSize = Math.min(Math.max(size, 1), 100);
		Map<String, Object> result = new HashMap<>();
		String trimmedKeyword = StringUtils.trimToNull(keyword);
		result.put("items", productInfoMapper.selectProductList(searchType, trimmedKeyword, registeredFrom,
				registeredTo, statuses, brands, categories, (safePage - 1) * safeSize, safeSize));
		result.put("totalCount", productInfoMapper.countProductList(searchType, trimmedKeyword, registeredFrom,
				registeredTo, statuses, brands, categories));
		result.put("brands", productInfoMapper.selectProductBrands());
		result.put("categories", productInfoMapper.selectProductCategories());
		result.put("page", safePage);
		result.put("size", safeSize);
		return result;
	}

	public ProductInfoDto selectProductInfo(Long productNo) throws Exception {
		ProductInfoDto product = productInfoMapper.selectProductInfo(productNo);
		if (product != null) {
			product.setImages(productInfoMapper.selectProductImages(productNo));
		}
		return product;
	}

	@Transactional(rollbackFor = Exception.class)
	public Long insertProductInfo(ProductInfoDto product, List<MultipartFile> images) throws Exception {
		List<Path> savedFiles = new ArrayList<>();
		try {
			if (images != null && images.size() > 5) {
				throw new IllegalArgumentException("상품 이미지는 최대 5개까지 등록할 수 있습니다.");
			}
			productInfoMapper.insertProductInfo(product);
			if (images != null) {
				for (int index = 0; index < images.size(); index++) {
					MultipartFile image = images.get(index);
					if (image == null || image.isEmpty()) {
						continue;
					}
					validateImage(image);
					Path savedFile = saveImage(image);
					savedFiles.add(savedFile);
					ProductImageDto imageDto = new ProductImageDto();
					imageDto.setProductNo(product.getProductNo());
					imageDto.setImageUrl(IMAGE_URL_PREFIX + savedFile.getFileName());
					imageDto.setOriginalFileName(Paths.get(image.getOriginalFilename()).getFileName().toString());
					imageDto.setRepresentativeYn(index == 0 ? "Y" : "N");
					imageDto.setSortOrder(index);
					imageDto.setRegId(product.getRegId());
					productInfoMapper.insertProductImage(imageDto);
				}
			}
			return product.getProductNo();
		} catch (Exception e) {
			for (Path savedFile : savedFiles) {
				Files.deleteIfExists(savedFile);
			}
			throw e;
		}
	}

	private void validateImage(MultipartFile image) {
		String contentType = image.getContentType();
		if (contentType == null || !contentType.matches("image/(jpeg|png|webp|gif)")) {
			throw new IllegalArgumentException("JPG, PNG, WEBP, GIF 이미지만 등록할 수 있습니다.");
		}
		if (image.getSize() > MAX_IMAGE_SIZE) {
			throw new IllegalArgumentException("이미지는 파일당 10MB 이하로 등록해 주세요.");
		}
	}

	private Path saveImage(MultipartFile image) throws IOException {
		Path storageDirectory = ProductImagePathResolver.resolve(imageStoragePath);
		Files.createDirectories(storageDirectory);
		String originalFileName = StringUtils.defaultString(image.getOriginalFilename());
		String extension = originalFileName.lastIndexOf('.') >= 0
				? originalFileName.substring(originalFileName.lastIndexOf('.')).toLowerCase() : "";
		Path target = storageDirectory.resolve(UUID.randomUUID() + extension).normalize();
		if (!target.startsWith(storageDirectory)) {
			throw new IllegalArgumentException("이미지 파일명이 올바르지 않습니다.");
		}
		Files.copy(image.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
		return target;
	}
}
