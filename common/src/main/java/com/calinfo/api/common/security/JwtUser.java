package com.calinfo.api.common.security;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Token décrypté
 */
@Getter
@Setter
public class JwtUser {

    private String login;

    private Map<String, List<String>> rolesApp = new HashMap<>();

    private String domain;
}
