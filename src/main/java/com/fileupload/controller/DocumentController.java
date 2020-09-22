package com.fileupload.controller;

import com.alibaba.fastjson.JSONObject;
import com.fileupload.auth.AuthContext;
import com.fileupload.auth.AuthForHeader;
import com.fileupload.auth.Private;
import com.fileupload.domain.Document;
import com.fileupload.domain.DocumentDto;
import com.fileupload.exception.BusinessException;
import com.fileupload.exception.CustomErrorCode;
import com.fileupload.service.DocumentService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@Log4j2
@RequestMapping("/v1/files")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @Private
    @RequestMapping(value = "/upload/{documentTitle}", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject uploadPhoto(@AuthForHeader AuthContext context,
                                  @RequestParam("file") MultipartFile file,
                                  @PathVariable(value = "documentTitle", required = false) String documentTitle) {
        return new JSONObject().fluentPut("id", documentService.save(context, documentTitle, file));
    }

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    @Private
    public void getDocument(@RequestParam("docId") String docId, HttpServletResponse resp, @AuthForHeader AuthContext context) {

        Optional.ofNullable(docId)
                .orElseThrow(() -> new BusinessException("Id missing", CustomErrorCode.FIELD_ERROR));

        Document document = documentService.find(context, docId);
        resp.setContentType(document.getContentType());

        try {

            byte[] buffer = new byte[1024];
            int len;

            while ((len = document.getStream().read(buffer)) != -1) {
                resp.getOutputStream().write(buffer, 0, len);
            }

        } catch (Exception e) {
            log.error("Unable to getDocument", e);
        } finally {
            try {
                resp.getOutputStream().close();
            } catch (Exception e) {
                // ignore
            }
            try {
                document.getStream().close();
            } catch (Exception e) {
                // ignore
            }

        }

    }

    @Private
    @RequestMapping(value = "/share", method = RequestMethod.POST)
    public void share(@AuthForHeader AuthContext context,
                      @RequestParam("toEmail") String toEmail,
                      @RequestParam("docId") String docId) {
        documentService.share(context, toEmail, docId);
    }

    @Private
    @RequestMapping(value = "/getmyfiles", method = RequestMethod.GET)
    public ResponseEntity<List<DocumentDto>> getMyFiles(@AuthForHeader AuthContext authContext){
        return ResponseEntity.ok(documentService.getMyFiles(authContext)
                .stream()
                .map(DocumentDto::new)
                .collect(Collectors.toList()));
    }

    @Private
    @RequestMapping(value = "/getsharedfiles", method = RequestMethod.GET)
    public ResponseEntity<List<DocumentDto>> getSharedFiles(@AuthForHeader AuthContext authContext){
        return ResponseEntity.ok(documentService.getSharedFiles(authContext)
                .stream()
                .map(DocumentDto::new)
                .collect(Collectors.toList()));
    }

}
