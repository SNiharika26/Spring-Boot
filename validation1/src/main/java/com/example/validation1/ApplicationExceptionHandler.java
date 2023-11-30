package com.example.validation1;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.MethodNotAllowedException;
import java.time.LocalDateTime;
import java.util.*;
@RestControllerAdvice
public class ApplicationExceptionHandler {
    public static Map<String,Object> createMap(HttpStatus status, Exception ex)
    {
        Map<String, Object> errorMap = new HashMap<>();
        LocalDateTime timestamp = LocalDateTime.now();
        errorMap.put("timestamp", timestamp);
        errorMap.put("status", status.value());
        errorMap.put("error", status.getReasonPhrase());
        errorMap.put("message", ex.getMessage());
        return errorMap;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, Object> handleError(MethodArgumentNotValidException ex)
    {
        Map<String, Object> errorMap = new HashMap<>();
        LocalDateTime timestamp = LocalDateTime.now();
        errorMap.put("timestamp", timestamp);
        errorMap.put("status", HttpStatus.BAD_REQUEST.value());
        errorMap.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        Map<String, String> message = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                message.put(error.getField(), error.getDefaultMessage()));
        errorMap.put("message", message);
        return errorMap;
    }
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CombinedExceptions.ResourceNotFoundException.class)
    public static Map<String, Object> handleResourceNotFound(CombinedExceptions.ResourceNotFoundException ex) {
        return createMap(HttpStatus.NOT_FOUND,ex);
    }
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(CombinedExceptions.CustomException.class)
    public static Map<String, Object> handleCustomException(CombinedExceptions.CustomException ex) {
        return createMap(HttpStatus.INTERNAL_SERVER_ERROR,ex);
    }
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(CombinedExceptions.MethodNotAllowedException.class)
    public static Map<String,Object> handleMethodNotAllowed(MethodNotAllowedException ex) {

        return createMap(HttpStatus.METHOD_NOT_ALLOWED,ex) ;
    }
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(CombinedExceptions.UnAuthorizedException.class)
    public static Map<String , Object> UnAuthorizedException(CombinedExceptions.UnAuthorizedException ex)
    {
        return createMap(HttpStatus.UNAUTHORIZED,ex);
    }

}

