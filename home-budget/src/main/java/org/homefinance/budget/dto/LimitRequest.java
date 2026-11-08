package org.homefinance.budget.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class LimitRequest {

	private BigDecimal monthlyLimit;

}
