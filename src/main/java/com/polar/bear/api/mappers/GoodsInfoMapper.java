package com.polar.bear.api.mappers;

import java.util.List;
import java.time.LocalDateTime;

import com.polar.bear.api.models.GoodsImageDto;
import com.polar.bear.api.models.GoodsInfoDto;
import com.polar.bear.api.models.GoodsInfoHistoryDto;
import com.polar.bear.api.models.GoodsPriceHistoryDto;
import com.polar.bear.api.models.CustomerGoodsDetailDto;
import com.polar.bear.api.models.CustomerGoodsDto;
import com.polar.bear.api.models.CustomerGoodsImageDto;
import com.polar.bear.api.models.BrandInfoDto;
import com.polar.bear.api.models.CategoryInfoDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface GoodsInfoMapper {
	List<GoodsInfoDto> selectGoodsList(@Param("searchType") String searchType, @Param("keyword") String keyword,
			@Param("registeredFrom") LocalDateTime registeredFrom, @Param("registeredTo") LocalDateTime registeredTo,
			@Param("statuses") List<String> statuses, @Param("brandNos") List<Long> brandNos,
			@Param("categoryNos") List<Long> categoryNos, @Param("offset") int offset,
			@Param("limit") int limit) throws Exception;
	int countGoodsList(@Param("searchType") String searchType, @Param("keyword") String keyword,
			@Param("registeredFrom") LocalDateTime registeredFrom, @Param("registeredTo") LocalDateTime registeredTo,
			@Param("statuses") List<String> statuses, @Param("brandNos") List<Long> brandNos,
			@Param("categoryNos") List<Long> categoryNos) throws Exception;
	List<BrandInfoDto> selectGoodsBrands() throws Exception;
	List<CategoryInfoDto> selectGoodsCategories() throws Exception;
	GoodsInfoDto selectGoodsInfo(@Param("goodsNo") Long goodsNo) throws Exception;
	List<GoodsImageDto> selectGoodsImages(@Param("goodsNo") Long goodsNo) throws Exception;
	List<CustomerGoodsDto> selectFeaturedGoods(@Param("limit") int limit) throws Exception;
	CustomerGoodsDetailDto selectCustomerGoodsDetail(@Param("goodsNo") Long goodsNo) throws Exception;
	List<CustomerGoodsImageDto> selectCustomerGoodsImages(@Param("goodsNo") Long goodsNo) throws Exception;
	GoodsInfoDto selectGoodsInfoForUpdate(@Param("goodsNo") Long goodsNo) throws Exception;
	Long selectLatestGoodsInfoHistoryNoForUpdate(@Param("goodsNo") Long goodsNo) throws Exception;
	int insertGoodsInfoHistory(GoodsInfoHistoryDto history) throws Exception;
	int insertGoodsPriceHistory(GoodsPriceHistoryDto history) throws Exception;
	List<GoodsInfoHistoryDto> selectGoodsInfoHistory(@Param("goodsNo") Long goodsNo,
			@Param("offset") int offset, @Param("limit") int limit) throws Exception;
	List<GoodsPriceHistoryDto> selectGoodsPriceHistory(@Param("goodsNo") Long goodsNo,
			@Param("offset") int offset, @Param("limit") int limit) throws Exception;
	int countGoodsInfoHistory(@Param("goodsNo") Long goodsNo) throws Exception;
	int countGoodsPriceHistory(@Param("goodsNo") Long goodsNo) throws Exception;
	int updateGoodsInfo(GoodsInfoDto goodsInfoDto) throws Exception;
	int insertGoodsInfo(GoodsInfoDto goodsInfoDto) throws Exception;
	int insertGoodsImage(GoodsImageDto goodsImageDto) throws Exception;
}
