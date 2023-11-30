package com.example.validation1;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class CombinedExceptions {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class ResourceNotFoundException extends RuntimeException {

        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public static class CustomException extends RuntimeException {

        public CustomException(String message) {
            super(message);
        }
    }
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public static class MethodNotAllowedException extends RuntimeException {

        public MethodNotAllowedException(String message) {
            super(message);
        }
    }
    public static class UnAuthorizedException extends RuntimeException
    {
        public UnAuthorizedException(String message)
        {
            super(message);
        }
    }

}
