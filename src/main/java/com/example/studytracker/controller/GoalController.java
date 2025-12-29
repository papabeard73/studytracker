package com.example.studytracker.controller;

import com.example.studytracker.service.GoalService;
import com.example.studytracker.entity.Goal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    // リダイレクト用
    @GetMapping("/")
    public String root() {
        return "redirect:/goals";
    }

    @GetMapping("/goals")
    public String list(Model model) {
        model.addAttribute("goals", goalService.getAllGoals());
        return "goals/list";
    }

    @GetMapping("/goals/new")
    public String newForm(Model model) {
        model.addAttribute("goal", new Goal());
        return "goals/form";
    }

    @PostMapping("/goals")
    public String create(Goal goal) {
        // 今はログインなしなので仮のユーザーIDを固定で入れる
        goal.setUserId(1L);

        goalService.createGoal(goal);
        return "redirect:/goals";
    }
}


