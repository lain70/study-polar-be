package com.polar.bear.api.mappers;

import java.util.List;
import com.polar.bear.api.models.CartItemDto;
import com.polar.bear.api.models.GoodsInfoDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CartMapper {
	List<CartItemDto> selectItems(@Param("userNo") Integer userNo);
	GoodsInfoDto selectAvailableGoodsForUpdate(@Param("goodsNo") Long goodsNo);
	Integer selectQuantity(@Param("userNo") Integer userNo, @Param("goodsNo") Long goodsNo);
	int insertItem(@Param("userNo") Integer userNo, @Param("goodsNo") Long goodsNo, @Param("quantity") int quantity);
	int updateQuantity(@Param("userNo") Integer userNo, @Param("goodsNo") Long goodsNo, @Param("quantity") int quantity);
	int deleteItem(@Param("userNo") Integer userNo, @Param("goodsNo") Long goodsNo);
}
