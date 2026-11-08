package org.homefinance.budget.controller;

import java.util.List;

import org.homefinance.budget.dto.ExpenseCreatedResponse;
import org.homefinance.budget.dto.ExpenseRequest;
import org.homefinance.budget.dto.ExpenseResponse;
import org.homefinance.budget.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

	@Autowired
	private ExpenseService expenseService;

	@GetMapping
	public List<ExpenseResponse> findByMonth(@RequestParam String month,
			@RequestParam(required = false) Long categoryId) {
		return expenseService.findByMonth(month, categoryId);
	}

	@GetMapping("/{id}")
	public ExpenseResponse getById(@PathVariable Long id) {
		return expenseService.getById(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ExpenseCreatedResponse create(@RequestBody ExpenseRequest request) {
		return expenseService.create(request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		expenseService.delete(id);
	}

}
