package com.calinfo.api.common.mocks;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dalexis on 06/01/2018.
 */
@Getter
@Setter
public class JsonizablePrincipal {

    private String username;

    private String domain;

    private List<String> roles = new ArrayList<>();

}
