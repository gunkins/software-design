package com.github.ntngunkin.homework4.controller

import com.github.ntngunkin.homework4.dao.GoalDao
import com.github.ntngunkin.homework4.dao.TaskDao
import com.github.ntngunkin.homework4.model.TaskForm
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequestMapping("/goals/{goalId}/tasks")
class TaskController(
    private val goalDao: GoalDao,
    private val taskDao: TaskDao
) {
    @GetMapping
    fun getAll(
        @PathVariable goalId: Int,
        model: ModelMap
    ): String {
        val goal = goalDao.findById(goalId) ?: return "error/404"
        val tasks = taskDao.findAllByGoalId(goalId)
        model.addAttribute("goal", goal)
        model.addAttribute("tasks", tasks)
        model.addAttribute("taskForm", TaskForm())
        return "tasks"
    }

    @PostMapping("/add")
    fun addTask(@ModelAttribute taskForm: TaskForm, @PathVariable goalId: Int): String {
        taskDao.insert(taskForm.description!!, goalId)
        return "redirect:/goals/$goalId/tasks"
    }

    @PostMapping("/{taskId}/delete")
    fun deleteTask(
        @PathVariable goalId: Int,
        @PathVariable taskId: Int
    ): String {
        taskDao.deleteByTaskId(taskId)
        return "redirect:/goals/$goalId/tasks"
    }

    @PostMapping("/{taskId}/finish")
    fun finishTask(
        @PathVariable goalId: Int,
        @PathVariable taskId: Int
    ): String {
        taskDao.finishTask(taskId)
        return "redirect:/goals/$goalId/tasks"
    }
}