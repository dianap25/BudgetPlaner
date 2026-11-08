package org.homefinance.budget.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.homefinance.budget.dto.ExpenseCreatedResponse;
import org.homefinance.budget.dto.ExpenseRequest;
import org.homefinance.budget.dto.ExpenseResponse;
import org.homefinance.budget.dto.LimitEvaluation;
import org.homefinance.budget.exception.BudgetRuleException;
import org.homefinance.budget.exception.NotFoundException;
import org.homefinance.budget.exception.ValidationException;
import org.homefinance.budget.model.Category;
import org.homefinance.budget.model.CategoryStatus;
import org.homefinance.budget.model.Expense;
import org.homefinance.budget.repository.CategoryRepository;
import org.homefinance.budget.repository.ClosedMonthRepository;
import org.homefinance.budget.repository.ExpenseRepository;
import org.homefinance.budget.util.Money;
import org.homefinance.budget.util.Months;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExpenseService {

	private static final BigDecimal MAX_AMOUNT = new BigDecimal("100000.00");
	private static final int DESCRIPTION_MAX_LENGTH = 100;

	@Autowired
	private ExpenseRepository expenseRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private ClosedMonthRepository closedMonthRepository;

	@Autowired
	private BudgetSummaryService budgetSummaryService;

	public List<ExpenseResponse> findByMonth(String month, Long categoryId) {
		YearMonth yearMonth = Months.parse(month);
		LocalDate from = yearMonth.atDay(1);
		LocalDate toExclusive = yearMonth.plusMonths(1).atDay(1);
		Map<Long, String> categoryNames = categoryNames();

		List<Expense> expenses;
		if (categoryId == null) {
			expenses = expenseRepository.findBetween(from, toExclusive);
		}
		else {
			if (!categoryNames.containsKey(categoryId)) {
				throw categoryNotFound(categoryId);
			}
			expenses = expenseRepository.findBetweenForCategory(from, toExclusive, categoryId);
		}
		return expenses.stream()
				.map(expense -> toResponse(expense, categoryNames.get(expense.getCategoryId())))
				.toList();
	}

	public ExpenseResponse getById(Long id) {
		Expense expense = findExpense(id);
		return toResponse(expense, categoryNames().get(expense.getCategoryId()));
	}

	public ExpenseCreatedResponse create(ExpenseRequest request) {
		if (request.getCategoryId() == null) {
			throw new ValidationException("Kategoria jest wymagana");
		}
		if (request.getAmount() == null) {
			throw new ValidationException("Kwota jest wymagana");
		}
		BigDecimal amount = Money.round(request.getAmount());
		if (amount.signum() <= 0) {
			throw new ValidationException("Kwota musi być większa od zera");
		}
		if (amount.compareTo(MAX_AMOUNT) > 0) {
			throw new ValidationException("Kwota nie może przekraczać 100000.00");
		}
		if (request.getDate() == null) {
			throw new ValidationException("Data jest wymagana");
		}
		String description = normalizeDescription(request.getDescription());
		Category category = categoryRepository.findById(request.getCategoryId())
				.orElseThrow(() -> categoryNotFound(request.getCategoryId()));
		YearMonth month = YearMonth.from(request.getDate());
		requireOpen(month);

		Expense saved = expenseRepository.insert(
				new Expense(null, category.getId(), amount, request.getDate(), description));
		LimitEvaluation evaluation = budgetSummaryService.statusFor(category.getId(), month);
		return ExpenseCreatedResponse.builder()
				.expense(toResponse(saved, category.getName()))
				.status(evaluation.getStatus())
				.percentUsed(evaluation.getPercentUsed())
				.warning(warningFor(evaluation, category, month))
				.build();
	}

	public void delete(Long id) {
		Expense expense = findExpense(id);
		requireOpen(YearMonth.from(expense.getDate()));
		expenseRepository.deleteById(id);
	}

	private Expense findExpense(Long id) {
		return expenseRepository.findById(id)
				.orElseThrow(() -> new NotFoundException(String.format("Wydatek o id %d nie istnieje", id)));
	}

	private void requireOpen(YearMonth month) {
		if (closedMonthRepository.isClosed(month.toString())) {
			throw new BudgetRuleException(String.format("Miesiąc %s jest zamknięty", month));
		}
	}

	private String normalizeDescription(String description) {
		if (description == null) {
			return null;
		}
		String trimmed = description.trim();
		if (trimmed.isEmpty()) {
			return null;
		}
		if (trimmed.length() > DESCRIPTION_MAX_LENGTH) {
			throw new ValidationException("Opis może mieć najwyżej 100 znaków");
		}
		return trimmed;
	}

	private String warningFor(LimitEvaluation evaluation, Category category, YearMonth month) {
		if (evaluation.getStatus() == CategoryStatus.WARNING) {
			return String.format("Wykorzystano %s%% limitu kategorii %s",
					evaluation.getPercentUsed().toPlainString(), category.getName());
		}
		if (evaluation.getStatus() == CategoryStatus.EXCEEDED) {
			BigDecimal excess = spentInMonth(category.getId(), month).subtract(category.getMonthlyLimit());
			return String.format("Przekroczono limit kategorii %s o %s", category.getName(), excess.toPlainString());
		}
		return null;
	}

	private BigDecimal spentInMonth(Long categoryId, YearMonth month) {
		BigDecimal spent = BigDecimal.ZERO;
		for (Expense expense : expenseRepository.findBetweenForCategory(
				month.atDay(1), month.plusMonths(1).atDay(1), categoryId)) {
			spent = spent.add(expense.getAmount());
		}
		return spent;
	}

	private Map<Long, String> categoryNames() {
		Map<Long, String> names = new HashMap<>();
		for (Category category : categoryRepository.findAll()) {
			names.put(category.getId(), category.getName());
		}
		return names;
	}

	private NotFoundException categoryNotFound(Long categoryId) {
		return new NotFoundException(String.format("Kategoria o id %d nie istnieje", categoryId));
	}

	private ExpenseResponse toResponse(Expense expense, String categoryName) {
		return ExpenseResponse.builder()
				.id(expense.getId())
				.categoryId(expense.getCategoryId())
				.categoryName(categoryName)
				.amount(expense.getAmount())
				.date(expense.getDate())
				.description(expense.getDescription())
				.build();
	}

}
