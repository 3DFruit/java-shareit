package ru.practicum.shareit.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.utils.exceptions.*;
import ru.practicum.shareit.utils.exceptions.UnsupportedOperationException;
import ru.practicum.shareit.utils.model.ErrorResponse;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({MethodArgumentNotValidException.class,
            ValidationException.class,
            UnavailableItemException.class,
            UnsupportedOperationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidateException(final Exception e) {
        log.warn("Ошибка запроса: {}", e.getMessage(), e);
        return new ErrorResponse(
                "Ошибка запроса: " + e.getMessage()
        );
    }

    @ExceptionHandler({UnknownStateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnknownStateException(final Exception e) {
        log.warn("Ошибка запроса: {}", e.getMessage(), e);
        return new ErrorResponse(
                "Unknown state: " + e.getMessage()
        );
    }

    @ExceptionHandler({DataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleNotUniqueValueException(final Exception e) {
        log.warn("Нарушение целостности данных: {}", e.getMessage(), e);
        return new ErrorResponse(
                "Нарушение целостности данных: " + e.getMessage()
        );
    }

    @ExceptionHandler({UnauthorizedAccessException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleUnauthorizedAccessException(final Exception e) {
        log.warn("Доступ запрещен: {}", e.getMessage(), e);
        return new ErrorResponse(
                "Доступ запрещен: " + e.getMessage()
        );
    }

    @ExceptionHandler({ObjectNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final Exception e) {
        log.warn("Объект не найден: {}", e.getMessage(), e);
        return new ErrorResponse(
                "Объект не найден: " + e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Exception e) {
        log.warn("Необработанная ошибка: {}", e.getMessage(), e);
        return new ErrorResponse(
                "Произошла непредвиденная ошибка. " + e.getMessage()
        );
    }
}
