package com.alazyboy.user;

import java.io.Serializable;

public class User implements Serializable {


    public final static String USER_ATTR = "_USER";

    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
