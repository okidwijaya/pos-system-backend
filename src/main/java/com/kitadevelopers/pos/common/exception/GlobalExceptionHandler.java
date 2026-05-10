package com.kitadevelopers.pos.common.exception;

import com.kitadevelopers.pos.common.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

//    @valid validatoin failures 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex){
        Map<String, String> errors = new HashMap<>();
        for(FieldError fieldError : ex.getBindingResult().getFieldErrors()){
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(Map.of(
                        "code", ErrorCode.VALIDATION_ERROR.name(),
                        "message", "Validation failed",
                        "fields", errors
                )));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusiness(BusinessException ex){
        return ResponseEntity
                .status(ex.getStatus())
                .body(ApiResponse.error(Map.of(
                        "code", ex.getCode().name(),
                        "message", ex.getMessage()
                )));
    }

    // 401 Bad credentials
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(Map.of(
                        "code", ErrorCode.INVALID_CREDENTIALS.name(),
                        "message", "Invalid email or password"
                )));
    }

    // Resource not found → 404
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(Map.of(
                        "code", ErrorCode.RESOURCE_NOT_FOUND.name(),
                        "message", ex.getMessage()
                )));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntime(RuntimeException ex){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(Map.of(
                        "code", ErrorCode.INVALID_STATE.name(),
                        "message", ex.getMessage()
                )));
    }

    // Catch-all → 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneric(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(Map.of(
                        "code", ErrorCode.INTERNAL_ERROR.name(),
                        "message", "Something went wrong. Please try again."
                )));
    }
}

//@ExceptionHandler(RuntimeException.class)
//public ResponseEntity<ApiResponse<Object>> handleRuntime(RuntimeException ex){
//    return ResponseEntity.badRequest()
//            .body((ApiResponse.error(ex.getMessage())));
//}
//
//// @valid validatoin failerus 400
//@ExceptionHandler(MethodArgumentNotValidException.class)
//public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex){
//    Map<String, String> errors = new HashMap<>();
//
//    ex.getBindingResult().getFieldErrors()
//            .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
//
//    return ResponseEntity.badRequest()
//            .body(ApiResponse.error(errors));
//}
//
//@ExceptionHandler(Exception.class)
//public ResponseEntity<ApiResponse<Object>> handle(Exception ex){
//    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//            .body(ApiResponse.error("Internal server error"));
//}

    //===========================
//    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
//    public ResponseEntity<ApiResponse<Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex){
//        return ResponseEntity.badRequest()
//                .body(ApiResponse.error(
//                        ex.getValue() + "Isn't a valid UUID"
//                ));
//    }
//
//    @ExceptionHandler(ProductNotFoundException.class)
//    public ResponseEntity<ApiResponse<Object>> handleProductNotFound(ProductNotFoundException ex){
//        return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                .body(ApiResponse.error(ex.getMessage()));
//    }
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex){
//        Map<String, String> errors = new HashMap<>();
//
//        ex.getBindingResult().getFieldErrors()
//                .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
//
//        return ResponseEntity.badRequest()
//                .body(ApiResponse.error(errors.toString()));
//    }
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ApiResponse<Object>> handleAll(Exception ex){
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(ApiResponse.error(ex.getMessage()));
//    }

//===========================
//    //validation err
//    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
//    public ResponseEntity<?> handleTypeMismatch(MethodArgumentTypeMismatchException ex){
//        return ResponseEntity
//                .badRequest()
//                .body(Map.of(
//                        "error", "Invalid ID format",
//                        "detail", ex.getValue() + " is not a valid UUID"
//                ));
//    }
//
//    @ExceptionHandler(ProductNotFoundException.class)
//    public ResponseEntity<?> handleProductNotFound(ProductNotFoundException ex){
//        return ResponseEntity
//                .status(HttpStatus.NOT_FOUND)
//                .body(Map.of("error", ex.getMessage()));
//    }

    //    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex){
    //        Map<String, String> errors = new HashMap<>();
    //
    //        ex.getBindingResult().getFieldErrors()
    //                .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
    //
    //        return ResponseEntity.badRequest().body(errors);
    //    }
