package com.fileupload.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;

import java.io.InputStream;
import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@org.springframework.data.mongodb.core.mapping.Document
public class Document {
    @Id
    String _id;
    @Indexed
    String email;
    String title;
    String name;
    String originalFileName;
    String contentType;
    String hash;
    long size;
    @Transient
    InputStream stream;
    Date created;
}
