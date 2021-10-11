package com.github.ntngunkin.homework4.dao

import com.github.ntngunkin.homework4.model.Task
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

private const val INSERT =
    """
        insert into Tasks (description, goal_id)
        values (:description, :goalId)
    """

private const val FIND_ALL_BY_GOAL_ID =
    """
        select id, description, finished
        from Tasks 
        where goal_id = :id
    """

private const val DELETE_BY_GOAL_ID = "delete from Tasks where goal_id = :goalId"
private const val DELETE_BY_TASK_ID = "delete from Tasks where id = :taskId"
private const val UPDATE_FINISHED_BY_ID = "update Tasks set finished = :finished where id = :taskId"

@Repository
class TaskDao(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    fun insert(description: String, goalId: Int) =
        jdbcTemplate.update(INSERT, mapOf("description" to description, "goalId" to goalId))

    fun findAllByGoalId(goalId: Int): List<Task> =
        jdbcTemplate.query(
            FIND_ALL_BY_GOAL_ID,
            mapOf("id" to goalId)
        ) { rs, _ ->
            Task(
                rs.getInt("id"),
                rs.getString("description"),
                rs.getBoolean("finished")
            )
        }

    fun deleteByGoalId(goalId: Int) =
        jdbcTemplate.update(DELETE_BY_GOAL_ID, mapOf("goalId" to goalId))

    fun deleteByTaskId(taskId: Int) =
        jdbcTemplate.update(DELETE_BY_TASK_ID, mapOf("taskId" to taskId))

    fun finishTask(taskId: Int) =
        jdbcTemplate.update(UPDATE_FINISHED_BY_ID, mapOf("taskId" to taskId, "finished" to true))
}