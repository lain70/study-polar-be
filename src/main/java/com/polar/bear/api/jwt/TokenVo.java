package com.polar.bear.api.jwt;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Getter
@ToString
@NoArgsConstructor
@Builder
public class TokenVo implements Serializable {

    private static final long serialVersionUID = 6244772611429248804L;

    private String type;

    private long creation;

    private long expiration;

    private String userKey;

    private String userId;

    public TokenVo(String type, long creation, long expiration, String userKey, String userId){
        this.type = type;
        this.creation = creation;
        this.expiration = expiration;
        this.userKey = userKey;
        this.userId = userId;
    }
}
