package com.polar.bear.api.mappers;

import java.util.List;
import java.time.LocalDateTime;

import com.polar.bear.api.models.ProductImageDto;
import com.polar.bear.api.models.ProductInfoDto;
import com.polar.bear.api.models.CustomerProductDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProductInfoMapper {
	List<ProductInfoDto> selectProductList(@Param("searchType") String searchType, @Param("keyword") String keyword,
			@Param("registeredFrom") LocalDateTime registeredFrom, @Param("registeredTo") LocalDateTime registeredTo,
			@Param("statuses") List<String> statuses, @Param("brands") List<String> brands,
			@Param("categories") List<String> categories, @Param("offset") int offset,
			@Param("limit") int limit) throws Exception;
	int countProductList(@Param("searchType") String searchType, @Param("keyword") String keyword,
			@Param("registeredFrom") LocalDateTime registeredFrom, @Param("registeredTo") LocalDateTime registeredTo,
			@Param("statuses") List<String> statuses, @Param("brands") List<String> brands,
			@Param("categories") List<String> categories) throws Exception;
	List<String> selectProductBrands() throws Exception;
	List<String> selectProductCategories() throws Exception;
	ProductInfoDto selectProductInfo(@Param("productNo") Long productNo) throws Exception;
	List<ProductImageDto> selectProductImages(@Param("productNo") Long productNo) throws Exception;
	List<CustomerProductDto> selectFeaturedProducts(@Param("limit") int limit) throws Exception;
	int insertProductInfo(ProductInfoDto productInfoDto) throws Exception;
	int insertProductImage(ProductImageDto productImageDto) throws Exception;
}
