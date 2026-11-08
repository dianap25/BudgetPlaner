package org.homefinance.budget.service;

import java.math.BigDecimal;

import org.homefinance.budget.dto.LimitEvaluation;
import org.homefinance.budget.model.CategoryStatus;
import org.homefinance.budget.util.Money;
import org.springframework.stereotype.Service;

@Service
public class LimitEvaluator {

	private static final BigDecimal WARNING_PERCENT = BigDecimal.valueOf(80);
	private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

	public LimitEvaluation evaluate(BigDecimal spent, BigDecimal limit) {
		if (limit.signum() == 0) {
			CategoryStatus status = spent.signum() > 0 ? CategoryStatus.EXCEEDED : CategoryStatus.OK;
			return LimitEvaluation.builder().status(status).percentUsed(null).build();
		}
		return LimitEvaluation.builder()
				.status(statusOf(spent, limit))
				.percentUsed(Money.percent(spent, limit))
				.build();
	}

	private CategoryStatus statusOf(BigDecimal spent, BigDecimal limit) {
		if (spent.compareTo(limit) > 0) {
			return CategoryStatus.EXCEEDED;
		}
		if (spent.multiply(HUNDRED).compareTo(limit.multiply(WARNING_PERCENT)) >= 0) {
			return CategoryStatus.WARNING;
		}
		return CategoryStatus.OK;
	}

}
