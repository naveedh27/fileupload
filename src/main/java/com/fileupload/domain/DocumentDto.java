package com.fileupload.domain;

import java.util.Date;

public class DocumentDto {
    String _id;
    String email;
    String title;
    String name;
    String originalFileName;
    String contentType;
    String hash;
    long size;
    Date created;

    public DocumentDto(Document document){
        this._id = document.get_id();
        this.email = document.getEmail();
        this.title = document.getTitle();
        this.name = document.getName();
        this.originalFileName = document.getOriginalFileName();
        this.contentType = document.getContentType();
        this.size = document.getSize();
        this.created = document.getCreated();
    }
}
