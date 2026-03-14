package com.allensandiego.systemagent.system;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;

import jakarta.servlet.http.HttpServletRequest;

public class AuthenticationService {

    private String AUTH_TOKEN_HEADER_NAME = "X-API-KEY";
    private String AUTH_TOKEN;

    public Authentication getAuthentication(HttpServletRequest request) {

        String apiKey = request.getHeader(AUTH_TOKEN_HEADER_NAME);

        try {
            File file = new File("authorizedKey.txt");
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

            AUTH_TOKEN = bufferedReader.readLine();
            bufferedReader.close();

            if (apiKey == null || !apiKey.equals(AUTH_TOKEN)) {
                throw new BadCredentialsException("Invalid authorization key.");
            }
    
            return new ApiKeyAuthentication(apiKey, AuthorityUtils.NO_AUTHORITIES);
    
        } catch (IOException e) {
            e.printStackTrace();
            throw new InternalAuthenticationServiceException("No authorized key configured.");
        }
        
    }
}