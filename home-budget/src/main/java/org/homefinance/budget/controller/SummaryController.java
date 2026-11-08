package org.homefinance.budget.controller;

import org.homefinance.budget.dto.MonthSummary;
import org.homefinance.budget.service.BudgetSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/summary")
public class SummaryController {

	@Autowired
	private BudgetSummaryService budgetSummaryService;

	@GetMapping
	public MonthSummary summary(@RequestParam String month) {
		return budgetSummaryService.summary(month);
	}

}
