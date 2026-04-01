package com.example.economix_android.network.auth.dto;

import java.util.List;

public class UserInfo {
    private Integer userId;
    private String username;
    private List<String> roles;
    private boolean twoFactorEnabled;

    public Integer getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public boolean isTwoFactorEnabled() {
        return twoFactorEnabled;
    }
}
