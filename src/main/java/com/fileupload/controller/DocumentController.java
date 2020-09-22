package com.fileupload.controller;

import com.alibaba.fastjson.JSONObject;
import com.fileupload.auth.AuthContext;
import com.fileupload.auth.AuthForHeader;
import com.fileupload.auth.Private;
import com.fileupload.exception.BusinessException;
import com.fileupload.exception.CustomErrorCode;
import com.fileupload.domain.Document;
import com.fileupload.service.DocumentService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@RestController
@Log4j2
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @Private
    @RequestMapping(value = "/upload-document/{documentTitle}", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject uploadPhoto(@AuthForHeader AuthContext context,
                                  @RequestParam("file") MultipartFile file,
                                  @PathVariable(value = "documentTitle", required = false) String documentTitle) {
        return new JSONObject().fluentPut("id", documentService.save(context, documentTitle, file));
    }

    @RequestMapping(value = "/document", method = RequestMethod.GET)
    @Private
    public void getDocument(@RequestParam("id") String id, HttpServletResponse resp, @AuthForHeader AuthContext context) {

        Optional.ofNullable(id)
                .orElseThrow(() -> new BusinessException("Id missing", CustomErrorCode.FIELD_ERROR));

        Document document = documentService.find(context, id);
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

}
