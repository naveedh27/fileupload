package com.fileupload.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fileupload.service.AuthService;

import java.util.Objects;

@RestController
public class LoginController {

    @Autowired
    private AuthService authService;

    @RequestMapping("/login")
    public ResponseEntity<JSONObject> login(@RequestBody JSONObject body){
        String email = Objects.requireNonNull(body.getString("email"));
        char[] password = Objects.requireNonNull(body.getString("password")).toCharArray();

        String token = authService.auth(email, password);
        return ResponseEntity.ok(new JSONObject().fluentPut("token", token));
    }


}
