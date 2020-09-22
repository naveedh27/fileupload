package com.fileupload.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum CustomErrorCode {

    NOT_AUTHORIZED("Not an authorized request", HttpStatus.UNAUTHORIZED),
    INVALID_API("Invalid Request", HttpStatus.NOT_ACCEPTABLE),
    FIELD_ERROR("Field Error", HttpStatus.NOT_ACCEPTABLE),
    DOCUMENT_UPLOAD_REJECTED("Upload Rejected", HttpStatus.BAD_REQUEST),
    DOCUMENT_NOT_FOUND("Document Not Found", HttpStatus.NOT_FOUND);

    String message;
    HttpStatus status;

}
