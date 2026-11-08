package org.homefinance.budget.controller;

import java.util.List;

import org.homefinance.budget.dto.ClosedMonthResponse;
import org.homefinance.budget.service.MonthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/months")
public class MonthController {

	@Autowired
	private MonthService monthService;

	@PostMapping("/{month}/close")
	public ClosedMonthResponse close(@PathVariable String month) {
		return monthService.close(month);
	}

	@GetMapping("/closed")
	public List<String> closedMonths() {
		return monthService.closedMonths();
	}

}
