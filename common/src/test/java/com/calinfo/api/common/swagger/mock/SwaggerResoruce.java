package com.calinfo.api.common.swagger.mock;

import com.calinfo.api.common.dto.Dto;
import com.calinfo.api.common.dto.MessageInfoAndWarningInterface;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Deprecated(since = "1.2.0", forRemoval = true)
public class SwaggerResoruce implements Dto, MessageInfoAndWarningInterface {

    @Size(min = 1, max = 2)
    @Pattern(regexp = "/ss/")
    @Null
    @NotEmpty
    @NotBlank
    @Email
    @NotNull
    private String string;

    @AssertTrue
    @AssertFalse
    private boolean bool;

    @Digits(integer = 1, fraction = 2)
    @DecimalMax("1.0")
    @DecimalMin("2.0")
    private BigDecimal decimal;

    @PastOrPresent
    @Past
    @Future
    @FutureOrPresent
    private Date date;

    @PositiveOrZero
    @Positive
    @NegativeOrZero
    @Negative
    @Max(1)
    @Min(2)
    private Integer integer;

    @Size(min = 0, max = 0)
    private List<String> listInfoMessages = new ArrayList<>();

    @Size(min = 0, max = 0)
    private List<String> listWarningMessages = new ArrayList<>();
}
