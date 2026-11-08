package org.homefinance.budget.service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import org.homefinance.budget.dto.CategorySummary;
import org.homefinance.budget.dto.LimitEvaluation;
import org.homefinance.budget.dto.MonthSummary;
import org.homefinance.budget.exception.NotFoundException;
import org.homefinance.budget.model.Category;
import org.homefinance.budget.model.Expense;
import org.homefinance.budget.repository.CategoryRepository;
import org.homefinance.budget.repository.ClosedMonthRepository;
import org.homefinance.budget.repository.ExpenseRepository;
import org.homefinance.budget.util.Money;
import org.homefinance.budget.util.Months;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BudgetSummaryService {

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private ExpenseRepository expenseRepository;

	@Autowired
	private ClosedMonthRepository closedMonthRepository;

	@Autowired
	private LimitEvaluator limitEvaluator;

	public MonthSummary summary(String month) {
		YearMonth yearMonth = Months.parse(month);
		List<Expense> expenses = expenseRepository.findBetween(yearMonth.atDay(1), yearMonth.plusMonths(1).atDay(1));

		List<CategorySummary> categories = new ArrayList<>();
		BigDecimal totalLimit = BigDecimal.ZERO;
		BigDecimal totalSpent = BigDecimal.ZERO;
		for (Category category : categoryRepository.findAll()) {
			BigDecimal spent = BigDecimal.ZERO;
			for (Expense expense : expenses) {
				if (expense.getCategoryId().equals(category.getId())) {
					spent = spent.add(expense.getAmount());
				}
			}
			spent = Money.round(spent);
			categories.add(toCategorySummary(category, spent));
			totalLimit = totalLimit.add(category.getMonthlyLimit());
			totalSpent = totalSpent.add(spent);
		}

		return MonthSummary.builder()
				.month(yearMonth.toString())
				.closed(closedMonthRepository.isClosed(yearMonth.toString()))
				.categories(categories)
				.totalLimit(Money.round(totalLimit))
				.totalSpent(Money.round(totalSpent))
				.totalRemaining(Money.round(totalLimit.subtract(totalSpent)))
				.build();
	}

	public LimitEvaluation statusFor(Long categoryId, YearMonth month) {
		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new NotFoundException(String.format("Kategoria o id %d nie istnieje", categoryId)));
		BigDecimal spent = BigDecimal.ZERO;
		for (Expense expense : expenseRepository.findBetweenForCategory(
				month.atDay(1), month.plusMonths(1).atDay(1), categoryId)) {
			spent = spent.add(expense.getAmount());
		}
		return limitEvaluator.evaluate(spent, category.getMonthlyLimit());
	}

	private CategorySummary toCategorySummary(Category category, BigDecimal spent) {
		LimitEvaluation evaluation = limitEvaluator.evaluate(spent, category.getMonthlyLimit());
		return CategorySummary.builder()
				.categoryId(category.getId())
				.name(category.getName())
				.limit(category.getMonthlyLimit())
				.spent(spent)
				.remaining(category.getMonthlyLimit().subtract(spent))
				.percentUsed(evaluation.getPercentUsed())
				.status(evaluation.getStatus())
				.build();
	}

}
