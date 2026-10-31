package com.polar.bear.api.models;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartRequestVo {
	@NotNull
	@Min(1)
	private Integer quantity;
}
