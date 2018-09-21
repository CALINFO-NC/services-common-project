package com.calinfo.api.common.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class MediaDto implements Dto {

    @NotBlank
    @NotEmpty
    @NotNull
    private String base64;

    @NotBlank
    @NotEmpty
    @NotNull
    private String contentType;
}
