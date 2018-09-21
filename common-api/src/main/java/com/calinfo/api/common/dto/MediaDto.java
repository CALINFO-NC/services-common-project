package com.calinfo.api.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MediaDto implements Dto {

    @NotBlank
    @NotEmpty
    @NotNull
    private String contentType;

    @NotBlank
    @NotEmpty
    @NotNull
    private String base64;
}
