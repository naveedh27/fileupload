package com.fileupload.service;

import com.fileupload.auth.AuthContext;
import com.fileupload.domain.Document;
import com.fileupload.exception.BusinessException;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

import static com.fileupload.exception.CustomErrorCode.DOCUMENT_NOT_FOUND;
import static com.fileupload.exception.CustomErrorCode.DOCUMENT_UPLOAD_REJECTED;

@Service
public class MongoDocumentService implements DocumentService {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private GridFsOperations operations;

    @Override
    public String save(AuthContext context, String title, MultipartFile file) {
        DBObject metaData = new BasicDBObject();
        String id = null;
        try {
            id = gridFsTemplate.store(
                    file.getInputStream(), file.getName(), file.getContentType(), metaData).toHexString();
        } catch (IOException e) {
            throw new BusinessException("Document upload failed, " + title, DOCUMENT_UPLOAD_REJECTED);
        }
        Document.builder().contentType(file.getContentType())
                ._id(id)
                .email(context.getEmail())
                .title(title)
                .name(file.getName())
                .originalFileName(file.getOriginalFilename())
                .size(file.getSize())
                .created(new Date())
                .name(file.getName());
        return id;
    }

    @Override
    public Document find(AuthContext context, String id){
        //Add permission check based on sharing
        return Optional.ofNullable(mongoTemplate.findById(id, Document.class))
                .map(this::stream)
                .orElseThrow(() -> new BusinessException("Document missing for " + id, DOCUMENT_NOT_FOUND));
    }


    public Document stream(Document document) {
        GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(document.get_id())));
        assert file != null;
        try {
            document.setStream(operations.getResource(file).getInputStream());
        } catch (IOException e) {
            throw new BusinessException("Document missing for " + document.get_id(), DOCUMENT_NOT_FOUND);
        }
        return document;
    }

}
