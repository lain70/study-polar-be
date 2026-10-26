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

import com.polar.bear.api.mappers.GoodsInfoMapper;
import com.polar.bear.api.models.CustomerGoodsDetailDto;
import com.polar.bear.api.models.CustomerGoodsDto;
import com.polar.bear.api.models.GoodsImageDto;
import com.polar.bear.api.models.GoodsInfoDto;
import com.polar.bear.api.utils.GoodsImagePathResolver;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoodsInfoService {
	private static final String IMAGE_URL_PREFIX = "/goods-images/";
	private static final long MAX_IMAGE_SIZE = 10L * 1024L * 1024L;
	private final GoodsInfoMapper goodsInfoMapper;
	private final GoodsHistoryService goodsHistoryService;

	@Value("${goods.image-storage-path:./uploads/goods}")
	private String imageStoragePath;

	public Map<String, Object> selectGoodsList(String searchType, String keyword, LocalDateTime registeredFrom,
			LocalDateTime registeredTo, List<String> statuses, List<Long> brandNos, List<Long> categoryNos,
			int page, int size) throws Exception {
		int safePage = Math.max(page, 1);
		int safeSize = Math.min(Math.max(size, 1), 100);
		int offset = calculateOffset(safePage, safeSize);
		Map<String, Object> result = new HashMap<>();
		String trimmedKeyword = StringUtils.trimToNull(keyword);
		result.put("items", goodsInfoMapper.selectGoodsList(searchType, trimmedKeyword, registeredFrom,
				registeredTo, statuses, brandNos, categoryNos, offset, safeSize));
		result.put("totalCount", goodsInfoMapper.countGoodsList(searchType, trimmedKeyword, registeredFrom,
				registeredTo, statuses, brandNos, categoryNos));
		result.put("brands", goodsInfoMapper.selectGoodsBrands());
		result.put("categories", goodsInfoMapper.selectGoodsCategories());
		result.put("page", safePage);
		result.put("size", safeSize);
		return result;
	}

	public GoodsInfoDto selectGoodsInfo(Long goodsNo) throws Exception {
		GoodsInfoDto goods = goodsInfoMapper.selectGoodsInfo(goodsNo);
		if (goods != null) {
			goods.setImages(goodsInfoMapper.selectGoodsImages(goodsNo));
		}
		return goods;
	}

	public List<CustomerGoodsDto> selectFeaturedGoods(int limit) throws Exception {
		return goodsInfoMapper.selectFeaturedGoods(Math.min(Math.max(limit, 1), 20));
	}

	public CustomerGoodsDetailDto selectCustomerGoodsDetail(Long goodsNo) throws Exception {
		CustomerGoodsDetailDto goods = goodsInfoMapper.selectCustomerGoodsDetail(goodsNo);
		if (goods != null) {
			goods.setImages(goodsInfoMapper.selectCustomerGoodsImages(goodsNo));
		}
		return goods;
	}

	public Map<String, Object> selectGoodsHistory(Long goodsNo, int page, int size) throws Exception {
		int safePage = Math.max(page, 1);
		int safeSize = Math.min(Math.max(size, 1), 100);
		int offset = calculateOffset(safePage, safeSize);
		Map<String, Object> result = new HashMap<>();
		result.put("goodsInfoHistory", goodsInfoMapper.selectGoodsInfoHistory(goodsNo, offset, safeSize));
		result.put("goodsPriceHistory", goodsInfoMapper.selectGoodsPriceHistory(goodsNo, offset, safeSize));
		result.put("goodsInfoHistoryTotalCount", goodsInfoMapper.countGoodsInfoHistory(goodsNo));
		result.put("goodsPriceHistoryTotalCount", goodsInfoMapper.countGoodsPriceHistory(goodsNo));
		result.put("page", safePage);
		result.put("size", safeSize);
		return result;
	}

	@Transactional(rollbackFor = Exception.class)
	public Long updateGoodsInfo(GoodsInfoDto goods) throws Exception {
		if (goods.getGoodsInfoHistoryNo() == null || goods.getGoodsInfoHistoryNo() <= 0) {
			throw new IllegalArgumentException("조회한 상품 정보 이력 번호를 입력해 주세요.");
		}
		GoodsInfoDto before = goodsInfoMapper.selectGoodsInfoForUpdate(goods.getGoodsNo());
		if (before == null) {
			throw new IllegalArgumentException("상품을 찾을 수 없습니다.");
		}
		Long latest = goodsInfoMapper.selectLatestGoodsInfoHistoryNoForUpdate(goods.getGoodsNo());
		if (!goods.getGoodsInfoHistoryNo().equals(latest)) {
			throw new OptimisticLockingFailureException("상품이 변경되었습니다. 다시 조회한 후 수정해 주세요.");
		}
		if (goodsInfoMapper.updateGoodsInfo(goods) != 1) {
			throw new IllegalStateException("상품을 저장하지 못했습니다.");
		}
		GoodsInfoDto after = goodsInfoMapper.selectGoodsInfoForUpdate(goods.getGoodsNo());
		return goodsHistoryService.record(before, after, "UPDATE");
	}

	@Transactional(rollbackFor = Exception.class)
	public Long insertGoodsInfo(GoodsInfoDto goods, List<MultipartFile> images) throws Exception {
		List<Path> savedFiles = new ArrayList<>();
		try {
			if (images != null && images.size() > 5) {
				throw new IllegalArgumentException("상품 이미지는 최대 5개까지 등록할 수 있습니다.");
			}
			goodsInfoMapper.insertGoodsInfo(goods);
			if (images != null) {
				for (int index = 0; index < images.size(); index++) {
					MultipartFile image = images.get(index);
					if (image == null || image.isEmpty()) {
						continue;
					}
					validateImage(image);
					Path savedFile = saveImage(image);
					savedFiles.add(savedFile);
					GoodsImageDto imageDto = new GoodsImageDto();
					imageDto.setGoodsNo(goods.getGoodsNo());
					imageDto.setImageUrl(IMAGE_URL_PREFIX + savedFile.getFileName());
					imageDto.setOriginalFileName(Paths.get(image.getOriginalFilename()).getFileName().toString());
					imageDto.setRepresentativeYn(index == 0 ? "Y" : "N");
					imageDto.setSortOrder(index);
					imageDto.setRegId(goods.getRegId());
					goodsInfoMapper.insertGoodsImage(imageDto);
				}
			}
			goodsHistoryService.record(null, goodsInfoMapper.selectGoodsInfoForUpdate(goods.getGoodsNo()), "CREATE");
			return goods.getGoodsNo();
		} catch (Exception e) {
			for (Path savedFile : savedFiles) {
				Files.deleteIfExists(savedFile);
			}
			throw e;
		}
	}

	private int calculateOffset(int page, int size) {
		long offset = ((long) page - 1) * size;
		if (offset > Integer.MAX_VALUE) {
			throw new IllegalArgumentException("페이지 번호가 허용 범위를 초과했습니다.");
		}
		return (int) offset;
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
		Path storageDirectory = GoodsImagePathResolver.resolve(imageStoragePath);
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
