package com.fileupload.service;

import com.fileupload.auth.AuthContext;
import com.fileupload.domain.Document;
import com.fileupload.repository.UserRepository;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

class MongoDocumentServiceTest {

    private MongoDocumentService documentService;

    private MongoTemplate mongoTemplate;

    private UserRepository userRepository;
    private GridFsOperations operations;
    private GridFsTemplate gridFsTemplate;

    @BeforeEach
    void setup() {
        documentService = new MongoDocumentService();

        mongoTemplate = Mockito.mock(MongoTemplate.class);
        userRepository = Mockito.mock(UserRepository.class);
        operations = Mockito.mock(GridFsOperations.class);
        gridFsTemplate = Mockito.mock(GridFsTemplate.class);

        documentService.setGridFsTemplate(gridFsTemplate);
        documentService.setMongoTemplate(mongoTemplate);
        documentService.setUserRepository(userRepository);
        documentService.setOperations(operations);


    }

    @Test
    void save() {

        MultipartFile file = Mockito.mock(MultipartFile.class);

        ArgumentCaptor<Document> captor = ArgumentCaptor.forClass(Document.class);
        ObjectId value = new ObjectId();

        Mockito.doReturn(value).when(gridFsTemplate).
                store(Mockito.any(InputStream.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(DBObject.class));


        Mockito.when(mongoTemplate.save(captor.capture())).thenAnswer(invocation -> captor.getValue());

        Assert.assertEquals(value.toHexString(), documentService.save(Mockito.mock(AuthContext.class), "Hello", Mockito.mock(MultipartFile.class)));

        Assert.assertEquals("1", captor.getValue().get_id());

    }

    @Test
    void find() {
    }

    @Test
    void share() {
    }

    @Test
    void getMyFiles() {
    }

    @Test
    void getSharedFiles() {
    }
}