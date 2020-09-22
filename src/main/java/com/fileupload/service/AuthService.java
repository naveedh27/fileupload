package com.fileupload.service;

import com.fileupload.auth.JWTHandler;
import com.fileupload.domain.User;
import com.fileupload.exception.BusinessException;
import com.fileupload.exception.CustomErrorCode;
import com.fileupload.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private JWTHandler jwtHandler;

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void setup() {
        userRepository.save(new User("hello@world.com", DigestUtils.md5DigestAsHex("V:x5Z)K'h".getBytes()), new Date()));
        userRepository.save(new User("abc@xyz.com", DigestUtils.md5DigestAsHex("^cHF9PmH^".getBytes()), new Date()));
    }

    public String auth(String email, char[] pwd) {
        if (checkValidity(email, pwd)) {
            return jwtHandler.generateToken(email);
        }
        throw new BusinessException("Password didn't match", CustomErrorCode.NOT_AUTHORIZED);
    }

    private boolean checkValidity(String email, char[] pwd) {
        User one = Optional.ofNullable(userRepository.findByEmail(email))
                .orElseThrow(() -> new BusinessException("mail id not found", CustomErrorCode.NOT_AUTHORIZED));
        return Arrays.equals(pwd, one.getPasswordHash().toCharArray());
    }


}
