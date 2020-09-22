package com.fileupload.domain;

import lombok.*;
import org.springframework.data.annotation.Transient;

import java.io.InputStream;
import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Document {
    String _id;
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
