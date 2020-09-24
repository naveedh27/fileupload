package com.fileupload.service.integration;

import com.alibaba.fastjson.JSONObject;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java8.En;
import org.junit.Assert;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class UploadStepDef implements En {

    Map<String, String> jwtMap = new HashMap<>();
    Map<String, String> fileMap = new HashMap<>();
    Map<String, String> fileOwner = new HashMap<>();
    Map<String, List<String>> usersFiles = new HashMap<>();

    @Given("user {string} loggedin with password {string}")
    public void login(String email, String password) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8080/login";
        ResponseEntity<JSONObject> response
                = restTemplate.postForEntity(url, new JSONObject().fluentPut("email", email).fluentPut("password", password), JSONObject.class);
        jwtMap.put(email, Objects.requireNonNull(response.getBody()).getString("token"));
    }

    @Given("{string} uploads file {string}")
    public void fileUpload(String email, String fileName) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8080//v1/files/upload/" + fileName;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtMap.get(email));
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(fileName)).getFile());

        MultiValueMap<String, Object> body
                = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(file));
        HttpEntity<MultiValueMap> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<JSONObject> response
                    = restTemplate.postForEntity(url, entity, JSONObject.class);

            String fileId = Objects.requireNonNull(response.getBody()).getString("id");
            usersFiles.computeIfAbsent(email, s -> new ArrayList<>()).add(fileId);
            fileMap.put(fileName, fileId);
            fileOwner.put(fileName, email);

        } catch (HttpClientErrorException e) {
            System.out.println("Error Adding File");
        }

    }

    @When("{string} share file {string} with {string}")
    public void shareFile(String fromEmail, String fileName, String toEmail) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://127.0.0.1:8080/v1/files/share?toEmail=" + toEmail + "&docId=" + fileMap.get(fileName);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtMap.get(fromEmail));
        HttpEntity<MultiValueMap> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response
                = restTemplate.postForEntity(url, entity, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Then("{string} download {string} for {string}")
    public void download(String fileName, String action, String email) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://127.0.0.1:8080/v1/files/download?docId=" + fileMap.get(fileName);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtMap.get(email));
        HttpEntity<MultiValueMap> entity = new HttpEntity<>(headers);

        HttpStatus statusCode;
        try {
            statusCode = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            ).getStatusCode();
        } catch (HttpClientErrorException e) {
            statusCode = e.getStatusCode();
        }
        Assert.assertEquals(action.equals("succeeds") ? HttpStatus.OK : HttpStatus.NOT_FOUND, statusCode);
    }


}
