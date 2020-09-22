package com.fileupload.auth;

import com.fileupload.exception.BusinessException;
import com.fileupload.exception.CustomErrorCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.Optional;

@Getter
@Setter
public class AuthContext {
    String email;
    String token;

    static AuthContext build(NativeWebRequest nativeWebRequest) {
        AuthContext authContext = new AuthContext();
        String headerStr = nativeWebRequest.getHeader(HttpHeaders.AUTHORIZATION);

        int index = Optional.ofNullable(headerStr)
                .map(f -> f.indexOf("Bearer "))
                .orElseThrow(() -> new BusinessException("Auth Token Missing", CustomErrorCode.NOT_AUTHORIZED));

        if (index != 0 || headerStr.length() < 8) {
            throw new BusinessException("Auth Token Missing", CustomErrorCode.NOT_AUTHORIZED);
        }
        authContext.setToken(headerStr.substring(7));
        return authContext;
    }
}
