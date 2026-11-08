package org.homefinance.budget.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Expense {

	private Long id;
	private Long categoryId;
	private BigDecimal amount;
	private LocalDate date;
	private String description;

}
