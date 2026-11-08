package org.homefinance.budget.repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.homefinance.budget.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class CategoryRepository {

	private static final String SELECT_CATEGORY = "SELECT id, name, monthly_limit FROM category";

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public List<Category> findAll() {
		return jdbcTemplate.query(SELECT_CATEGORY + " ORDER BY name", this::mapRow);
	}

	public Optional<Category> findById(Long id) {
		List<Category> categories = jdbcTemplate.query(SELECT_CATEGORY + " WHERE id = ?", this::mapRow, id);
		return categories.stream().findFirst();
	}

	public boolean existsByNameIgnoreCase(String name) {
		Integer count = jdbcTemplate.queryForObject(
				"SELECT COUNT(*) FROM category WHERE LOWER(name) = LOWER(?)", Integer.class, name);
		return count != null && count > 0;
	}

	public Category insert(Category category) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(connection -> {
			PreparedStatement statement = connection.prepareStatement(
					"INSERT INTO category (name, monthly_limit) VALUES (?, ?)", new String[] {"id"});
			statement.setString(1, category.getName());
			statement.setBigDecimal(2, category.getMonthlyLimit());
			return statement;
		}, keyHolder);
		category.setId(keyHolder.getKey().longValue());
		return category;
	}

	public int updateLimit(Long id, BigDecimal limit) {
		return jdbcTemplate.update("UPDATE category SET monthly_limit = ? WHERE id = ?", limit, id);
	}

	private Category mapRow(ResultSet rs, int rowNum) throws SQLException {
		return new Category(
				rs.getLong("id"),
				rs.getString("name"),
				rs.getBigDecimal("monthly_limit"));
	}

}
