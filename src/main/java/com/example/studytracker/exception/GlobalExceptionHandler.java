package com.example.studytracker.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final boolean showErrorDetail;

    public GlobalExceptionHandler(@Value("${app.error.show-detail:false}") boolean showErrorDetail) {
        this.showErrorDetail = showErrorDetail;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(ResourceNotFoundException ex, Model model) {
        // ログ
        log.warn("Resource not found", ex); // stacktrace付き

        model.addAttribute("message", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoResourceFound(NoResourceFoundException ex, Model model) {
        log.warn("No resource found: {}", ex.getResourcePath());
        model.addAttribute("message", "指定されたページは見つかりませんでした");
        return "error/404";
    }

    @ExceptionHandler({ BindException.class, ConstraintViolationException.class, IllegalArgumentException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequest(Exception ex, Model model) {
        log.warn("Bad request", ex);
        model.addAttribute("message", "入力内容に誤りがあります。内容を確認してください。");
        if (showErrorDetail) {
            model.addAttribute("detail", ex.getMessage());
        }
        return "error/400";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleServerError(Exception ex, Model model) {
        // ログ
        log.error("Unexpected error occurred", ex);

        model.addAttribute("message", "予期しないエラーが発生しました");
        if (showErrorDetail) {
            model.addAttribute("detail", ex.getMessage());
        }
        return "error/500";
    }
}
