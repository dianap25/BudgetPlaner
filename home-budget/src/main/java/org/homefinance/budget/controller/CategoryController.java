package org.homefinance.budget.controller;

import java.util.List;

import org.homefinance.budget.dto.CategoryRequest;
import org.homefinance.budget.dto.LimitRequest;
import org.homefinance.budget.model.Category;
import org.homefinance.budget.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

	@Autowired
	private CategoryService categoryService;

	@GetMapping
	public List<Category> findAll() {
		return categoryService.findAll();
	}

	@GetMapping("/{id}")
	public Category getById(@PathVariable Long id) {
		return categoryService.getById(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Category create(@RequestBody CategoryRequest request) {
		return categoryService.create(request);
	}

	@PutMapping("/{id}/limit")
	public Category updateLimit(@PathVariable Long id, @RequestBody LimitRequest request) {
		return categoryService.updateLimit(id, request);
	}

}
