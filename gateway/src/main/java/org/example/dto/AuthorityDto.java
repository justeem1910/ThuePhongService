package org.example.dto;

public class AuthorityDto {
    private String authority;

    public AuthorityDto() {
    }

    public AuthorityDto(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}