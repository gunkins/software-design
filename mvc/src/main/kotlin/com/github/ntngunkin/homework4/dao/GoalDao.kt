package com.github.ntngunkin.homework4.dao

import com.github.ntngunkin.homework4.model.Goal
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

private const val INSERT_GOAL =
    """
        insert into Goals (name)
        values (:name)
    """

private const val FIND_ALL = "select id, name from Goals"

private const val FIND_BY_ID = "select id, name from Goals where id = :id"

private const val DELETE_BY_ID = "delete from Goals where id = :id"

@Repository
class GoalDao(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    private val goalMapper: RowMapper<Goal> = RowMapper { rs, _ ->
        Goal(
            rs.getInt("id"),
            rs.getString("name")
        )
    }

    fun insert(name: String) =
        jdbcTemplate.update(INSERT_GOAL, mapOf("name" to name))

    fun findAllGoals(): List<Goal> =
        jdbcTemplate.query(FIND_ALL, goalMapper)

    fun findById(goalId: Int): Goal? =
        jdbcTemplate.query(FIND_BY_ID, mapOf("id" to goalId), goalMapper).firstOrNull()

    fun deleteById(goalId: Int) =
        jdbcTemplate.update(DELETE_BY_ID, mapOf("id" to goalId))

}