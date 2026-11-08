package org.homefinance.budget.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MonthSummary {

	String month;
	boolean closed;
	List<CategorySummary> categories;
	BigDecimal totalLimit;
	BigDecimal totalSpent;
	BigDecimal totalRemaining;

}
