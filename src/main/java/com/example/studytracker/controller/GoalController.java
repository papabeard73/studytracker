package com.example.studytracker.controller;

import com.example.studytracker.service.GoalService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
}


