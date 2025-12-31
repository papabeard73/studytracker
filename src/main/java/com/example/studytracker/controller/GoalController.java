package com.example.studytracker.controller;

import com.example.studytracker.service.GoalService;
import com.example.studytracker.entity.Goal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

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
        model.addAttribute("isEdit", false);
        return "goals/form";
    }

    @PostMapping("/goals")
    public String create(@Valid Goal goal, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "goals/form";
        }
        // 今はログインなしなので仮のユーザーIDを固定で入れる
        goal.setUserId(1L);

        goalService.createGoal(goal);
        return "redirect:/goals";
    }

    @GetMapping("/goals/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Goal goal = goalService.getGoalById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found: " + id));

        model.addAttribute("goal", goal);
        model.addAttribute("isEdit", true); // フォームで分岐に使う
        return "goals/form";
    }

    @PostMapping("/goals/{id}")
    public String update(@PathVariable Long id, @Valid Goal goal, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            // 編集時は id が必要なので戻す
            goal.setId(id);
            model.addAttribute("isEdit", true);
            return "goals/form";
        }
        // 今はログインなしなので固定（将来はログインユーザーから取得）
        goal.setUserId(1L);

        goalService.updateGoal(id, goal);
        return "redirect:/goals";
    }

    @PostMapping("/goals/{id}/delete")
    public String delete(@PathVariable Long id) {
        goalService.deleteGoal(id);
        return "redirect:/goals";
    }
}
