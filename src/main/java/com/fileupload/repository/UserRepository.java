package com.fileupload.repository;

import com.fileupload.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface UserRepository extends MongoRepository<User, String> {
    User findByEmail(String email);
}
