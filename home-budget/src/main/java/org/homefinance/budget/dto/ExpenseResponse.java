package org.homefinance.budget.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ExpenseResponse {

	Long id;
	Long categoryId;
	String categoryName;
	BigDecimal amount;
	LocalDate date;
	String description;

}
