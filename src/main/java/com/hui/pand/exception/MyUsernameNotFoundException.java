package com.hui.pand.exception;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * @author daihui
 * @date 2020/6/4 11:17
 */
public class MyUsernameNotFoundException extends AuthenticationException{

    private static final long serialVersionUID = 1L;

    public MyUsernameNotFoundException(String msg, Throwable t) {
        super(msg, t);
    }

    public MyUsernameNotFoundException(String msg) {
        super(msg);
    }
}
