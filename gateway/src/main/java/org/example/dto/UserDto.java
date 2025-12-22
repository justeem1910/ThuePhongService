package org.example.dto;

import java.util.List;

public class UserDto {
    private String userId;
    private String username;
    private List<AuthorityDto> authorities;

    // Constructor không tham số
    public UserDto() {
    }

    // Constructor đầy đủ tham số
    public UserDto(String userId, String username, List<AuthorityDto> authorities) {
        this.userId = userId;
        this.username = username;
        this.authorities = authorities;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<AuthorityDto> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<AuthorityDto> authorities) {
        this.authorities = authorities;
    }
}