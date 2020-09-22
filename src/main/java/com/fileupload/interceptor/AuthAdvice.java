package com.fileupload.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.fileupload.auth.AuthContext;
import com.fileupload.auth.JWTHandler;
import com.fileupload.exception.BusinessException;
import com.fileupload.exception.CustomErrorCode;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuthAdvice {

    @Autowired
    private JWTHandler jwtHandler;

    @Around(value = "@annotation(com.fileupload.auth.Private)")
    public Object privateCheck(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        AuthContext authContext = null;

        for (Object obj : args) {
            if (null == obj) {
                continue;
            }
            if (AuthContext.class.isAssignableFrom(obj.getClass())) {
                authContext = (AuthContext) obj;
                break;
            }
        }
        if (authContext == null) {
            throw new BusinessException("Auth Missing", CustomErrorCode.NOT_AUTHORIZED);
        }

        JSONObject o = jwtHandler.validAndGetDetailFromToken(authContext.getToken());
        authContext.setEmail(o.getString("email"));

        return pjp.proceed();
    }

}
