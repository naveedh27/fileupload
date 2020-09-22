package com.fileupload.service;

import com.fileupload.auth.AuthContext;
import com.fileupload.domain.Document;
import com.fileupload.domain.Share;
import com.fileupload.exception.BusinessException;
import com.fileupload.exception.CustomErrorCode;
import com.fileupload.repository.UserRepository;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.fileupload.exception.CustomErrorCode.DOCUMENT_NOT_FOUND;
import static com.fileupload.exception.CustomErrorCode.DOCUMENT_UPLOAD_REJECTED;

@Service
public class MongoDocumentService implements DocumentService {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserRepository userRepository;

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
        Document doc = Document.builder().contentType(file.getContentType())
                ._id(id)
                .email(context.getEmail())
                .title(title)
                .name(file.getName())
                .originalFileName(file.getOriginalFilename())
                .size(file.getSize())
                .created(new Date())
                .name(file.getName()).build();

        mongoTemplate.save(doc);
        return id;
    }

    @Override
    public Document find(AuthContext context, String docId) {
        return Optional.ofNullable(mongoTemplate.findById(docId, Document.class))
                .filter(d -> d.getEmail().equals(context.getEmail()) || isShared(docId, context.getEmail()))
                .map(this::stream)
                .orElseThrow(() -> new BusinessException("Document missing for " + docId, DOCUMENT_NOT_FOUND));
    }

    private boolean isShared(String docId, String email) {
        return Optional.ofNullable(mongoTemplate.findOne(new BasicQuery("{documentId: '" + docId + "', toEmail:'" + email + "'}"), Share.class))
                .isPresent();
    }

    private Document stream(Document document) {
        GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(document.get_id())));
        assert file != null;
        try {
            document.setStream(operations.getResource(file).getInputStream());
        } catch (IOException e) {
            throw new BusinessException("Document missing for " + document.get_id(), DOCUMENT_NOT_FOUND);
        }
        return document;
    }

    @Override
    public void share(AuthContext context, String toEmail, String docId) {

        Optional.ofNullable(userRepository.findByEmail(toEmail))
                .orElseThrow(() -> new BusinessException("Invalid toEmail", CustomErrorCode.FIELD_ERROR));


        String quryString = "{_id:'" + docId + "'}";
        BasicQuery query = new BasicQuery(quryString);

        Document one = Optional.ofNullable(mongoTemplate.findOne(query, Document.class))
                .filter(document -> document.getEmail().equals(context.getEmail()))
                .orElseThrow(() -> new BusinessException("Permission denied", CustomErrorCode.NOT_AUTHORIZED));

        mongoTemplate.save(new Share(docId, toEmail, new Date()));
    }

    @Override
    public List<Document> getMyFiles(AuthContext authContext) {

        String quryString = "{email : '" + authContext.getEmail() + "'}";
        BasicQuery query = new BasicQuery(quryString);

        return mongoTemplate.find(query, Document.class);
    }

    @Override
    public List<Document> getSharedFiles(AuthContext authContext) {
        String quryString = "{toEmail : '" + authContext.getEmail() + "'}";
        BasicQuery query = new BasicQuery(quryString);

        String collect = mongoTemplate.find(query, Share.class)
                .stream()
                .map(Share::getDocumentId)
                .map(h -> "'" + h + "'")
                .collect(Collectors.joining(",", "[", "]"));

        return mongoTemplate.find(new BasicQuery("{ _id : { $in : " + collect + " } } "), Document.class);
    }

}
