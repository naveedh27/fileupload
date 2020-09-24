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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MongoDocumentServiceTest {

    private MongoDocumentService documentService;

    private MongoTemplate mongoTemplate;

    private UserRepository userRepository;
    private GridFsOperations operations;
    private GridFsTemplate gridFsTemplate;

    @BeforeEach
    void setup() {
        documentService = new MongoDocumentService();

        mongoTemplate = mock(MongoTemplate.class);
        userRepository = mock(UserRepository.class);
        operations = mock(GridFsOperations.class);
        gridFsTemplate = mock(GridFsTemplate.class);

        documentService.setGridFsTemplate(gridFsTemplate);
        documentService.setMongoTemplate(mongoTemplate);
        documentService.setUserRepository(userRepository);
        documentService.setOperations(operations);


    }

    @Test
    void save() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenReturn(mock(InputStream.class));
        when(file.getName()).thenReturn("name");
        when(file.getContentType()).thenReturn("contentType");
        ArgumentCaptor<Document> captor = ArgumentCaptor.forClass(Document.class);
        ObjectId value = new ObjectId();
        when(gridFsTemplate.store(any(InputStream.class), any(String.class), any(String.class), any(DBObject.class))).thenReturn(value);
        when(mongoTemplate.save(captor.capture())).thenAnswer(invocation -> captor.getValue());
        Assert.assertEquals(value.toHexString(), documentService.save(new AuthContext(), "Hello", file));
        Assert.assertEquals(value.toHexString(), captor.getValue().get_id());
    }

    @Test
    void find() {

        AuthContext context = new AuthContext();
        context.setEmail("aaadd@ffgg.com");
        context.setToken("kjsafdskd xcm klvmskdf");
        String docId = "11222HHG";
        Document document = Document.builder()
                .name("MYDOC")
                .email("aaadd@ffgg.com")
                .build();

        when(mongoTemplate.findById(context, Document.class)).thenReturn(document);




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