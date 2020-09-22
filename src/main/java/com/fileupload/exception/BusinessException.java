package com.fileupload.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessException extends RuntimeException {

    CustomErrorCode errCode;

    public BusinessException(String message, CustomErrorCode errCode) {
        super(message);
        this.errCode = errCode;
    }
}
