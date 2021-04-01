package com.calinfo.api.common.dto;

/*-
 * #%L
 * common-api
 * %%
 * Copyright (C) 2019 - 2021 CALINFO
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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.domain.Pageable;

/**
 * Created by dalexis on 18/11/2017.
 * Utiliser {{@link #PageInfoDto}}
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "ChargementInfo")
@Deprecated
public class ChargementInfoDto extends PageInfoDto {
    public ChargementInfoDto() {
    }

    public ChargementInfoDto(Pageable page) {
        super(page);
    }

    public ChargementInfoDto(int start) {
        super(start);
    }

    public ChargementInfoDto(int start, Integer limit) {
        super(start, limit);
    }
}
