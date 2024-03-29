package com.calinfo.api.common.dto;

/*-
 * #%L
 * common-api
 * %%
 * Copyright (C) 2019 - 2023 CALINFO
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.calinfo.api.common.type.TypeAttribut;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Schema(name = "Attribut")
public class AttributDto implements Dto {


    @Schema(description = "Name of the HTTP parameter or the property of the resource at issue.")
    private String name;

    @Schema(description = "The field type is incorrect. This could be due to an HTTP request parameter or a property of the resource.")
    private TypeAttribut type;

    @Schema(description = "Enumeration of errors associated with the field identified by 'name'.")
    private List<String> listMessages = new ArrayList<>();
}
