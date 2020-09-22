package com.fileupload.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document
public class User {
    @Id
    String email;
    String passwordHash;
    Date lastLoginTIme;

    @Override
    public String toString() {
        return "User :: email - " + email + " , lastLoginTime" + lastLoginTIme;
    }
}
