package com.fileupload.exception;

import com.fileupload.domain.ApiResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Log4j2
public class ExceptionCatcher {

    @ExceptionHandler({BusinessException.class})
    public ResponseEntity<ApiResponse> handleBusinessException(BusinessException be) {
        ApiResponse response = ApiResponse.builder()
                .status(false)
                .data(be.getErrCode())
                .build();
        return ResponseEntity.status(be.getErrCode().getStatus()).body(response);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ApiResponse> handleGeneralException(Exception be) {
        log.error("Server error", be);
        ApiResponse response = ApiResponse.builder()
                .status(false)
                .data(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

}
