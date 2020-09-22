package com.fileupload.service;

import com.fileupload.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import com.fileupload.repository.UserRepository;

class AuthServiceTest {

    @Mock
    UserRepository userRepository;

    @BeforeEach
    public void setup(){

        //Mockito.when(userRepository.findByEmail(Mockito.anyString())).thenReturn(new User(""))

    }

    @Test
    void auth() {

    }
}