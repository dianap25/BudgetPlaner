package org.homefinance.budget.dto;

import java.math.BigDecimal;

import org.homefinance.budget.model.CategoryStatus;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class LimitEvaluation {

	CategoryStatus status;
	BigDecimal percentUsed;

}
