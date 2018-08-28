package com.calinfo.api.common.swagger.mock;

import com.calinfo.api.common.resource.Resource;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class SwaggerResoruce extends Resource {

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
}
