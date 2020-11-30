package com.calinfo.api.common.resource;

/*-
 * #%L
 * common-api
 * %%
 * Copyright (C) 2019 - 2020 CALINFO
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

import com.calinfo.api.common.dto.Dto;
import com.calinfo.api.common.dto.MessageInfoAndWarningInterface;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dalexis on 18/11/2017.
 *
 * @deprecated (1.2.0, N'est plus pris en charge par le common)
 */
@Getter
@Setter
@Deprecated(since = "1.2.0", forRemoval = true)
public class Resource implements Dto, MessageInfoAndWarningInterface {

    @Size(min = 0, max = 0)
    private List<String> listInfoMessages = new ArrayList<>();

    @Size(min = 0, max = 0)
    private List<String> listWarningMessages = new ArrayList<>();
}
