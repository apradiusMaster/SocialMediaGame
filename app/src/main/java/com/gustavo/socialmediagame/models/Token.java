package com.gustavo.socialmediagame.models;

import android.widget.Toast;

public class Token {

    private String token;

    public Token(){

    }

    public Token(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
