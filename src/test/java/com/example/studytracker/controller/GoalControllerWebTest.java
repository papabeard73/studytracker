package com.example.studytracker.controller;

import com.example.studytracker.entity.Goal;
import com.example.studytracker.entity.GoalStatus;
import com.example.studytracker.entity.StudyRecord;
import com.example.studytracker.service.GoalService;
import com.example.studytracker.service.StudyRecordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(GoalController.class)
class GoalControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GoalService goalService;

    @MockBean
    private StudyRecordService studyRecordService;

    @Test
    void getGoals_returnsListView() throws Exception {
        Goal goal = sampleGoal(1L, GoalStatus.ACTIVE);
        when(goalService.getAllGoals()).thenReturn(List.of(goal));

        mockMvc.perform(get("/goals"))
                .andExpect(status().isOk())
                .andExpect(view().name("goals/list"))
                .andExpect(model().attributeExists("goals"));
    }

    @Test
    void getGoalDetail_returnsDetailViewWithTotals() throws Exception {
        Goal goal = sampleGoal(1L, GoalStatus.ACTIVE);

        StudyRecord record = new StudyRecord();
        record.setId(10L);
        record.setGoal(goal);
        record.setRecordedAt(LocalDate.now());
        record.setContent("study");
        record.setDurationMinutes(90);

        when(goalService.getGoalOrThrow(1L)).thenReturn(goal);
        when(studyRecordService.getRecordsByGoalId(1L)).thenReturn(List.of(record));

        mockMvc.perform(get("/goals/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("goals/detail"))
                .andExpect(model().attributeExists("goal", "records"))
                .andExpect(model().attribute("totalHours", 1))
                .andExpect(model().attribute("remainingMinutes", 30));
    }

    @Test
    void postGoals_validInput_redirectsToList() throws Exception {
        when(goalService.createGoal(any(Goal.class))).thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(post("/goals")
                        .param("title", "Spring学習")
                        .param("targetDate", LocalDate.now().plusDays(1).toString())
                        .param("description", "説明")
                        .param("status", "ACTIVE"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/goals"));

        verify(goalService).createGoal(any(Goal.class));
    }

    @Test
    void postGoals_invalidInput_returnsFormWithErrors() throws Exception {
        mockMvc.perform(post("/goals")
                        .param("title", "")
                        .param("targetDate", "")
                        .param("description", "説明")
                        .param("status", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("goals/form"))
                .andExpect(model().attributeHasFieldErrors("goal", "title", "targetDate", "status"));

        verify(goalService, never()).createGoal(any(Goal.class));
    }

    @Test
    void postRecords_invalidInput_returnsRecordFormWithErrors() throws Exception {
        Goal goal = sampleGoal(1L, GoalStatus.ACTIVE);
        when(goalService.getGoalOrThrow(1L)).thenReturn(goal);

        mockMvc.perform(post("/goals/1/records")
                        .param("recordedAt", "")
                        .param("content", "")
                        .param("durationMinutes", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("goals/record_form"))
                .andExpect(model().attributeHasFieldErrors("record", "recordedAt", "content", "durationMinutes"));

        verify(studyRecordService, never()).addRecord(eq(goal), any(StudyRecord.class));
    }

    private Goal sampleGoal(Long id, GoalStatus status) {
        Goal goal = new Goal();
        goal.setId(id);
        goal.setUserId(1L);
        goal.setTitle("Sample Goal");
        goal.setTargetDate(LocalDate.now().plusDays(30));
        goal.setDescription("desc");
        goal.setStatus(status);
        return goal;
    }
}
