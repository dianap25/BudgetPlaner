package org.homefinance.budget.util;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;

import org.homefinance.budget.exception.ValidationException;

public final class Months {

	private static final String INVALID_FORMAT = "Miesiąc musi mieć format RRRR-MM";

	private Months() {
	}

	public static YearMonth parse(String month) {
		if (month == null || month.isBlank()) {
			throw new ValidationException(INVALID_FORMAT);
		}
		try {
			return YearMonth.parse(month);
		}
		catch (DateTimeParseException ex) {
			throw new ValidationException(INVALID_FORMAT);
		}
	}

}
