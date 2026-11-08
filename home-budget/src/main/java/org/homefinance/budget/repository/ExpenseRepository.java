package org.homefinance.budget.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.homefinance.budget.model.Expense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class ExpenseRepository {

	private static final String SELECT_EXPENSE =
			"SELECT id, category_id, amount, expense_date, description FROM expense";

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public List<Expense> findBetween(LocalDate from, LocalDate toExclusive) {
		return jdbcTemplate.query(
				SELECT_EXPENSE + " WHERE expense_date >= ? AND expense_date < ? ORDER BY expense_date, id",
				this::mapRow, from, toExclusive);
	}

	public List<Expense> findBetweenForCategory(LocalDate from, LocalDate toExclusive, Long categoryId) {
		return jdbcTemplate.query(
				SELECT_EXPENSE + " WHERE expense_date >= ? AND expense_date < ? AND category_id = ?"
						+ " ORDER BY expense_date, id",
				this::mapRow, from, toExclusive, categoryId);
	}

	public Optional<Expense> findById(Long id) {
		List<Expense> expenses = jdbcTemplate.query(SELECT_EXPENSE + " WHERE id = ?", this::mapRow, id);
		return expenses.stream().findFirst();
	}

	public Expense insert(Expense expense) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(connection -> {
			PreparedStatement statement = connection.prepareStatement(
					"INSERT INTO expense (category_id, amount, expense_date, description) VALUES (?, ?, ?, ?)",
					new String[] {"id"});
			statement.setLong(1, expense.getCategoryId());
			statement.setBigDecimal(2, expense.getAmount());
			statement.setObject(3, expense.getDate());
			statement.setString(4, expense.getDescription());
			return statement;
		}, keyHolder);
		expense.setId(keyHolder.getKey().longValue());
		return expense;
	}

	public int deleteById(Long id) {
		return jdbcTemplate.update("DELETE FROM expense WHERE id = ?", id);
	}

	private Expense mapRow(ResultSet rs, int rowNum) throws SQLException {
		return new Expense(
				rs.getLong("id"),
				rs.getLong("category_id"),
				rs.getBigDecimal("amount"),
				rs.getObject("expense_date", LocalDate.class),
				rs.getString("description"));
	}

}
