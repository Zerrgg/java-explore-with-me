package ru.practicum.ewm.server.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Objects;

import static ru.practicum.ewm.dto.GlobalConstants.DT_FORMATTER;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.error(exception.toString());
        return new ApiError(HttpStatus.BAD_REQUEST.name(),
                "Incorrectly made request.",
                getFieldErrorDetails(Objects.requireNonNull(exception.getFieldError())),
                getErrors(exception),
                LocalDateTime.now().format(DT_FORMATTER));
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentTypeMismatchException(RuntimeException exception) {
        log.error(exception.toString());
        return new ApiError(HttpStatus.BAD_REQUEST.name(),
                "Incorrectly made request.",
                exception.getMessage(),
                getErrors(exception),
                LocalDateTime.now().format(DT_FORMATTER));
    }

    @ExceptionHandler({BadRequestException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequestException(BadRequestException exception) {
        log.error(exception.toString());
        return new ApiError(HttpStatus.BAD_REQUEST.name(),
                "Incorrectly made request.",
                exception.getMessage(),
                getErrors(exception),
                LocalDateTime.now().format(DT_FORMATTER));
    }

    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIllegalArgumentException(IllegalArgumentException exception) {
        log.error(exception.toString());
        return new ApiError(HttpStatus.BAD_REQUEST.name(),
                "Incorrectly made request.",
                exception.getMessage(),
                getErrors(exception),
                LocalDateTime.now().format(DT_FORMATTER));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
        log.error(exception.toString());
        return new ApiError(HttpStatus.CONFLICT.name(),
                "Integrity constraint has been violated.",
                exception.getMessage(),
                getErrors(exception),
                LocalDateTime.now().format(DT_FORMATTER));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConstraintViolationException(ConstraintViolationException exception) {
        log.error(exception.toString());
        return new ApiError(HttpStatus.CONFLICT.name(),
                "For the requested operation the conditions are not met.",
                exception.getMessage(),
                getErrors(exception),
                LocalDateTime.now().format(DT_FORMATTER));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleException(Exception exception) {
        log.error("Error 500: {}", exception.getMessage(), exception);
        return new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.name(),
                "Unhandled exception.",
                exception.getMessage(),
                getErrors(exception),
                LocalDateTime.now().format(DT_FORMATTER));
    }

    private String getFieldErrorDetails(FieldError fieldError) {
        return String.format("Field: %s. Error: %s", fieldError.getField(), fieldError.getDefaultMessage());
    }

    private String getErrors(Exception exception) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        exception.printStackTrace(printWriter);
        return stringWriter.toString();
    }

}

