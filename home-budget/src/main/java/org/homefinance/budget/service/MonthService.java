package org.homefinance.budget.service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

import org.homefinance.budget.dto.ClosedMonthResponse;
import org.homefinance.budget.exception.ConflictException;
import org.homefinance.budget.model.Expense;
import org.homefinance.budget.repository.ClosedMonthRepository;
import org.homefinance.budget.repository.ExpenseRepository;
import org.homefinance.budget.util.Money;
import org.homefinance.budget.util.Months;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MonthService {

	@Autowired
	private ClosedMonthRepository closedMonthRepository;

	@Autowired
	private ExpenseRepository expenseRepository;

	public ClosedMonthResponse close(String month) {
		YearMonth yearMonth = Months.parse(month);
		String monthKey = yearMonth.toString();
		if (closedMonthRepository.isClosed(monthKey)) {
			throw new ConflictException(String.format("Miesiąc %s jest już zamknięty", monthKey));
		}
		closedMonthRepository.close(monthKey);

		List<Expense> expenses = expenseRepository.findBetween(yearMonth.atDay(1), yearMonth.plusMonths(1).atDay(1));
		BigDecimal total = BigDecimal.ZERO;
		for (Expense expense : expenses) {
			total = total.add(expense.getAmount());
		}
		return ClosedMonthResponse.builder()
				.month(monthKey)
				.expenseCount(expenses.size())
				.total(Money.round(total))
				.build();
	}

	public List<String> closedMonths() {
		return closedMonthRepository.findAll();
	}

}
