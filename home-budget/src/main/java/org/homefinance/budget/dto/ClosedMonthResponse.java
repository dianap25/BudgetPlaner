package org.homefinance.budget.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ClosedMonthResponse {

	String month;
	int expenseCount;
	BigDecimal total;

}
