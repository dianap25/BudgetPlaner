package org.homefinance.budget.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Money {

	private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

	private Money() {
	}

	public static BigDecimal round(BigDecimal amount) {
		return amount.setScale(2, RoundingMode.HALF_UP);
	}

	public static BigDecimal percent(BigDecimal part, BigDecimal whole) {
		return part.multiply(HUNDRED).divide(whole, 1, RoundingMode.HALF_UP);
	}

}
