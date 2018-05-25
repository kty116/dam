package com.deltaworks.damlink.model;

/**
 * Created by Administrator on 2018-05-24.
 */

public class TokenModel {
    String message;
    String valid;
    String token;
    String responseCode;

    public TokenModel(String message, String valid, String token, String responseCode) {
        this.message = message;
        this.valid = valid;
        this.token = token;
        this.responseCode = responseCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getValid() {
        return valid;
    }

    public void setValid(String valid) {
        this.valid = valid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    @Override
    public String toString() {
        return "TokenModel{" +
                "message='" + message + '\'' +
                ", valid='" + valid + '\'' +
                ", token='" + token + '\'' +
                ", responseCode='" + responseCode + '\'' +
                '}';
    }
}
