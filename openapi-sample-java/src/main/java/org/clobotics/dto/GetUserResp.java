package org.clobotics.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetUserResp {
    private Long id;
    private String username;
    @JsonProperty("real_name")
    private String realName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

}
