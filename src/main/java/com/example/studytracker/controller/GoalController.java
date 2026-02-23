package com.example.studytracker.controller;

import com.example.studytracker.service.GoalService;
import com.example.studytracker.entity.Goal;
import com.example.studytracker.service.StudyRecordService;
import com.example.studytracker.entity.StudyRecord;
import com.example.studytracker.exception.ResourceNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import java.time.LocalDate;
import java.util.List;

@Controller
public class GoalController {

    // フィールド
    private final GoalService goalService;
    private final StudyRecordService studyRecordService;

    // コンストラクタ
    public GoalController(GoalService goalService, StudyRecordService studyRecordService) {
        this.goalService = goalService;
        this.studyRecordService = studyRecordService;
    }

    // リダイレクト用
    @GetMapping("/")
    public String root() {
        return "redirect:/goals";
    }

    // 目標の一覧表示
    @GetMapping("/goals")
    public String list(Model model) {
        model.addAttribute("goals", goalService.getAllGoals());
        model.addAttribute("pageTitle", "TOP - 目標一覧");
        return "goals/list";
    }

    // 目標の追加フォーム
    @GetMapping("/goals/new")
    public String newForm(Model model) {
        model.addAttribute("goal", new Goal());
        model.addAttribute("pageTitle", "目標を追加");
        model.addAttribute("isEdit", false);
        return "goals/form";
    }

    // 目標の追加
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

    // 目標の編集
    @GetMapping("/goals/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Goal goal = goalService.getGoalOrThrow(id);

        model.addAttribute("goal", goal);
        model.addAttribute("pageTitle", "目標を編集");
        model.addAttribute("isEdit", true); // フォームで分岐に使う
        return "goals/form";
    }

    // 目標の保存
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

    // 目標の削除
    @PostMapping("/goals/{id}/delete")
    public String delete(@PathVariable Long id) {
        goalService.deleteGoal(id);
        return "redirect:/goals";
    }

    // 学習記録の詳細表示
    @GetMapping("/goals/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Goal goal = goalService.getGoalOrThrow(id);
        List<StudyRecord> records = studyRecordService.getRecordsByGoalId(id);
        int totalDurationMinutes = records.stream()
                .map(StudyRecord::getDurationMinutes)
                .filter(java.util.Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();
        int totalHours = totalDurationMinutes / 60;
        int remainingMinutes = totalDurationMinutes % 60;

        model.addAttribute("goal", goal);
        model.addAttribute("pageTitle", "詳細を表示");
        model.addAttribute("records", records);
        model.addAttribute("totalHours", totalHours);
        model.addAttribute("remainingMinutes", remainingMinutes);
        return "goals/detail";
    }

    // 学習記録の保存（POST）
    @PostMapping("/goals/{goalId}/records")
    public String createRecord(
            @PathVariable Long goalId,
            @Valid @ModelAttribute("record") StudyRecord record,
            BindingResult bindingResult,
            Model model) {

        Goal goal = goalService.getGoalOrThrow(goalId);

        // エラー特定のため一時的なログ
        // System.out.println("### hasErrors = " + bindingResult.hasErrors());
        // System.out.println("### errors = " + bindingResult.getAllErrors());

        if (bindingResult.hasErrors()) {
            model.addAttribute("goal", goal);
            model.addAttribute("record", record); // 念のため明示
            model.addAttribute("isEdit", false);
            return "goals/record_form";
        }

        // 念のため：新規作成なのでIDを必ずnullに（更新扱いにならないように）
        record.setId(null);

        studyRecordService.addRecord(goal, record);
        return "redirect:/goals/" + goalId;
    }

    // 学習記録の追加フォーム（GET）
    @GetMapping("/goals/{goalId}/records/new")
    public String newRecordForm(@PathVariable Long goalId, Model model) {
        Goal goal = goalService.getGoalOrThrow(goalId);

        StudyRecord record = new StudyRecord();
        // デフォルト日付を今日に
        record.setRecordedAt(LocalDate.now());

        model.addAttribute("goal", goal);
        model.addAttribute("record", record);
        model.addAttribute("isEdit", false);
        return "goals/record_form";
    }

    // 学習記録の編集フォーム（GET）
    @GetMapping("/goals/{goalId}/records/{recordId}/edit")
    public String editRecordForm(
            @PathVariable Long goalId,
            @PathVariable Long recordId,
            Model model) {
        Goal goal = goalService.getGoalOrThrow(goalId);
        StudyRecord record = studyRecordService.getRecordOrThrow(recordId);
        if (!record.getGoal().getId().equals(goalId)) {
            throw new ResourceNotFoundException("StudyRecord not found for goal: " + recordId);
        }

        model.addAttribute("goal", goal);
        model.addAttribute("record", record);
        model.addAttribute("isEdit", true);
        return "goals/record_form";
    }

    // 学習記録の更新（POST）
    @PostMapping("/goals/{goalId}/records/{recordId}")
    public String updateRecord(
            @PathVariable Long goalId,
            @PathVariable Long recordId,
            @Valid @ModelAttribute("record") StudyRecord record,
            BindingResult bindingResult,
            Model model) {

        Goal goal = goalService.getGoalOrThrow(goalId);

        if (bindingResult.hasErrors()) {
            record.setId(recordId);
            model.addAttribute("goal", goal);
            model.addAttribute("record", record);
            model.addAttribute("isEdit", true);
            return "goals/record_form";
        }

        studyRecordService.updateRecord(goal, recordId, record);
        return "redirect:/goals/" + goalId;
    }

    // 学習記録の削除（POST）
    @PostMapping("/goals/{goalId}/records/{recordId}/delete")
    public String deleteRecord(
            @PathVariable Long goalId,
            @PathVariable Long recordId) {
        Goal goal = goalService.getGoalOrThrow(goalId);
        studyRecordService.deleteRecord(goal, recordId);
        return "redirect:/goals/" + goalId;
    }
}
