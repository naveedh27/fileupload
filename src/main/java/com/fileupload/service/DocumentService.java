package com.fileupload.service;

import com.fileupload.auth.AuthContext;
import com.fileupload.domain.Document;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentService {

    String save(AuthContext context, String title, MultipartFile file);

    Document find(AuthContext context, String id);

}
