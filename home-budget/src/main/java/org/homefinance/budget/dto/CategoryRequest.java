package org.homefinance.budget.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class CategoryRequest {

	private String name;
	private BigDecimal monthlyLimit;

}
