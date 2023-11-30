package com.example.validation1;

import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.validation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.MethodNotAllowedException;


import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ExceptionTest {
    @Test
    void createMap_ReturnsErrorMapWithExpectedValues() {
        // Arrange
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Exception ex = new Exception("Test error message");

        // Act
        Map<String, Object> errorMap = ApplicationExceptionHandler.createMap(status, ex);

        // Assert
        assertNotNull(errorMap);

        assertNotNull(errorMap.get("timestamp"));
        assertEquals(status.value(), errorMap.get("status"));
        assertEquals(status.getReasonPhrase(), errorMap.get("error"));
        assertEquals("Test error message", errorMap.get("message"));
    }


    @Test
    void testUnAuthorizedException() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("bundle", Locale.getDefault());
        String errorMessage = resourceBundle.getString("error.unauthorized");
        CombinedExceptions.UnAuthorizedException exception = new CombinedExceptions.UnAuthorizedException(errorMessage);

        assertEquals(errorMessage, exception.getMessage());

        Map<String, Object> errorMap = ApplicationExceptionHandler.UnAuthorizedException(exception);

        // Assert
        assertNotNull(errorMap);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), errorMap.get("status"));
        assertEquals(HttpStatus.UNAUTHORIZED.getReasonPhrase(), errorMap.get("error"));
        assertEquals(errorMessage, errorMap.get("message"));

    }

    @Test
    void testMethodNotAllowed() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("bundle", Locale.getDefault());
        String errorMessage = resourceBundle.getString("error.methodNotAllowed");
        CombinedExceptions.MethodNotAllowedException exception = new CombinedExceptions.MethodNotAllowedException(errorMessage);
        //assertEquals(errorMessage, exception.getMessage());
        Map<String,Object> errorMap = ApplicationExceptionHandler.handleMethodNotAllowed(new MethodNotAllowedException("GET",null));
        assertNotNull(errorMap);
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED.value(), errorMap.get("status"));
        assertThat(errorMap.get("message").toString().contains("Request method 'GET' is not supported."));
    }

    @Test
    void testInternalServer() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("bundle");
        String errorMessage = resourceBundle.getString("error.server");
        CombinedExceptions.CustomException exception = new CombinedExceptions.CustomException(errorMessage);
        assertEquals(errorMessage, exception.getMessage());

        Map<String,Object> errorMap = ApplicationExceptionHandler.handleCustomException(exception);
        assertNotNull(errorMap);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorMap.get("status"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), errorMap.get("error"));
        assertEquals(errorMessage, errorMap.get("message"));

    }
    @Test
    void testNotFound()
    {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("bundle",Locale.getDefault());
        String errorMsg = resourceBundle.getString("error.notFound");
        CombinedExceptions.ResourceNotFoundException exception=new CombinedExceptions.ResourceNotFoundException(errorMsg);
        assertEquals(errorMsg,exception.getMessage());

        Map<String,Object> errorMap = ApplicationExceptionHandler.handleResourceNotFound(exception);
        assertNotNull(errorMap);
        assertEquals(HttpStatus.NOT_FOUND.value(),errorMap.get("status"));
        assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(),errorMap.get("error"));
        assertEquals(errorMsg,errorMap.get("message"));

    }
    @Test
    void handleError_ReturnsCorrectMap() {
        // Arrange
        ApplicationExceptionHandler exceptionHandler = new ApplicationExceptionHandler();
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("fieldName", "errorCode", "Error message");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        // Act
        Map<String, Object> errorMap = exceptionHandler.handleError(ex);

        // Assert
        Assertions.assertNotNull(errorMap);

        // Check timestamp
        Assertions.assertTrue(errorMap.containsKey("timestamp"));
        Assertions.assertTrue(errorMap.get("timestamp") instanceof LocalDateTime);

        // Check status
        Assertions.assertTrue(errorMap.containsKey("status"));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), errorMap.get("status"));

        // Check error
        Assertions.assertTrue(errorMap.containsKey("error"));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), errorMap.get("error"));

        // Check message
        Assertions.assertTrue(errorMap.containsKey("message"));
        Assertions.assertTrue(errorMap.get("message") instanceof Map);
        Map<String, String> message = (Map<String, String>) errorMap.get("message");
        Assertions.assertFalse(message.isEmpty());
        Assertions.assertEquals("Error message", message.get(fieldError.getField()));
    }
    }


