package com.example.studytracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

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
    public void handleNoResourceFound(NoResourceFoundException ex) {
        // favicon.ico などの静的リソース未検出は “想定内” として静かにする
        // 何もしない（ログも出さない）方が運用上見やすい
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleServerError(Exception ex, Model model) {
        // ログ
        log.error("Unexpected error occurred", ex);

        model.addAttribute("message", "予期しないエラーが発生しました");
        // 開発中は ex.getMessage() を出してもOK（本番は控える）
        model.addAttribute("detail", ex.getMessage());
        return "error/500";
    }
}
