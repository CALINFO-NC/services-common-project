package com.calinfo.api.common.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MediaDto implements Dto {

    private String base64;

    private String contentType;
}
