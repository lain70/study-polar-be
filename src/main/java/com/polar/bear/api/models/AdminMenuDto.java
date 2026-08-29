package com.polar.bear.api.models;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

@Getter
@Setter
@NoArgsConstructor
@Alias("adminMenuDto")
public class AdminMenuDto implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long menuNo;
	private String menuName;
	private String routeName;
	private Integer displayOrder;
}
