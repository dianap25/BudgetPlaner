package org.homefinance.budget.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class ExpenseRequest {

	private Long categoryId;
	private BigDecimal amount;
	private LocalDate date;
	private String description;

}
