package com.hyrepo.comments_tree.exception;

import com.hyrepo.comments_tree.model.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final String DUPLICATE_USER_MESSAGE = "User already existed.";
    private static final String UNAUTHORIZED_MESSAGE = "Login required.";
    private static final String NO_SUCH_USER_MESSAGE = "No such user.";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        StringBuilder errors = new StringBuilder();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.append("%s:%s\n".formatted(fieldName, errorMessage));
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(), errors.toString()));
    }


    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateUserExceptions() {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiErrorResponse(HttpStatus.CONFLICT.value(), DUPLICATE_USER_MESSAGE));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFoundExceptions() {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiErrorResponse(HttpStatus.CONFLICT.value(), NO_SUCH_USER_MESSAGE));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleFailedAuthenticationExceptions() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiErrorResponse(HttpStatus.UNAUTHORIZED.value(), UNAUTHORIZED_MESSAGE));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidTokenExceptions(InvalidTokenException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiErrorResponse(HttpStatus.UNAUTHORIZED.value(), exception.getMessage()));
    }
}
