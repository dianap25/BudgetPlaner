package org.homefinance.budget.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ClosedMonthRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public boolean isClosed(String month) {
		Integer count = jdbcTemplate.queryForObject(
				"SELECT COUNT(*) FROM closed_month WHERE month_key = ?", Integer.class, month);
		return count != null && count > 0;
	}

	public void close(String month) {
		jdbcTemplate.update("INSERT INTO closed_month (month_key) VALUES (?)", month);
	}

	public List<String> findAll() {
		return jdbcTemplate.queryForList("SELECT month_key FROM closed_month ORDER BY month_key", String.class);
	}

}
