package com.calinfo.api.common.resource;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dalexis on 18/11/2017.
 */
@Getter
@Setter
public class Resource {

    private List<String> listInfoMessages = new ArrayList<>();

    private List<String> listWarningMessages = new ArrayList<>();
}
