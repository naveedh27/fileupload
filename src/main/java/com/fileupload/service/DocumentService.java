package com.fileupload.service;

import com.fileupload.auth.AuthContext;
import com.fileupload.domain.Document;
import com.fileupload.domain.DocumentDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {

    String save(AuthContext context, String title, MultipartFile file);

    Document find(AuthContext context, String id);

    void share(AuthContext context, String toEmail, String docId);

    List<Document> getMyFiles(AuthContext authContext);

    List<Document> getSharedFiles(AuthContext authContext);
}
