package org.homefinance.budget.service;

import java.math.BigDecimal;
import java.util.List;

import org.homefinance.budget.dto.CategoryRequest;
import org.homefinance.budget.dto.LimitRequest;
import org.homefinance.budget.exception.ConflictException;
import org.homefinance.budget.exception.NotFoundException;
import org.homefinance.budget.exception.ValidationException;
import org.homefinance.budget.model.Category;
import org.homefinance.budget.repository.CategoryRepository;
import org.homefinance.budget.util.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

	private static final int NAME_MIN_LENGTH = 2;
	private static final int NAME_MAX_LENGTH = 40;
	private static final BigDecimal MAX_LIMIT = new BigDecimal("1000000.00");

	@Autowired
	private CategoryRepository categoryRepository;

	public List<Category> findAll() {
		return categoryRepository.findAll();
	}

	public Category getById(Long id) {
		return categoryRepository.findById(id)
				.orElseThrow(() -> new NotFoundException(String.format("Kategoria o id %d nie istnieje", id)));
	}

	public Category create(CategoryRequest request) {
		String name = request.getName() == null ? "" : request.getName().trim();
		if (name.isEmpty()) {
			throw new ValidationException("Nazwa kategorii jest wymagana");
		}
		if (name.length() < NAME_MIN_LENGTH || name.length() > NAME_MAX_LENGTH) {
			throw new ValidationException("Nazwa kategorii musi mieć od 2 do 40 znaków");
		}
		BigDecimal limit = validateLimit(request.getMonthlyLimit());
		if (categoryRepository.existsByNameIgnoreCase(name)) {
			throw new ConflictException(String.format("Kategoria '%s' już istnieje", name));
		}
		return categoryRepository.insert(new Category(null, name, limit));
	}

	public Category updateLimit(Long id, LimitRequest request) {
		BigDecimal limit = validateLimit(request.getMonthlyLimit());
		Category category = getById(id);
		categoryRepository.updateLimit(id, limit);
		category.setMonthlyLimit(limit);
		return category;
	}

	private BigDecimal validateLimit(BigDecimal limit) {
		if (limit == null) {
			throw new ValidationException("Limit jest wymagany");
		}
		if (limit.signum() < 0) {
			throw new ValidationException("Limit nie może być ujemny");
		}
		if (limit.compareTo(MAX_LIMIT) > 0) {
			throw new ValidationException("Limit nie może przekraczać 1000000.00");
		}
		return Money.round(limit);
	}

}
