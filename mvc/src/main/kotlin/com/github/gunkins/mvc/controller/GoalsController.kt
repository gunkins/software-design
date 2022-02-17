package com.github.gunkins.mvc.controller


import com.github.gunkins.mvc.dao.GoalDao
import com.github.gunkins.mvc.dao.TaskDao
import com.github.gunkins.mvc.model.GoalForm
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/goals")
class GoalsController(
    private val goalDao: GoalDao,
    private val taskDao: TaskDao
) {
    @GetMapping
    fun getAll(model: ModelMap): String {
        val goals = goalDao.findAllGoals()
        model.addAttribute("goals", goals)
        model.addAttribute("goalForm", GoalForm())
        return "goals"
    }

    @PostMapping("/add")
    fun addNewGoal(@ModelAttribute goalForm: GoalForm): String {
        goalDao.insert(goalForm.name!!)
        return "redirect:/goals"
    }

    @PostMapping("/{goalId}/delete")
    fun deleteGoal(@PathVariable goalId: Int): String {
        taskDao.deleteByGoalId(goalId)
        goalDao.deleteById(goalId)
        return "redirect:/goals"
    }
}