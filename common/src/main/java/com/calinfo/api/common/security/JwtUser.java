package com.calinfo.api.common.security;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Token décrypté
 */
@Getter
@Setter
public class JwtUser {

    private String login;

    private List<String> roles = new ArrayList<>();

    private String domain;
}
