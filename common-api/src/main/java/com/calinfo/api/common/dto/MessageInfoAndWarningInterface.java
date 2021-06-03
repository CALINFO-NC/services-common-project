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

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.List;

public interface MessageInfoAndWarningInterface extends Serializable {


    @ArraySchema(arraySchema = @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Message de type 'Information' concernant cette ressource."))
    List<String> getListInfoMessages();

    void setListInfoMessages(List<String> listInfoMessages);

    @ArraySchema(arraySchema = @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Message de type 'Attention' concernant cette ressource."))
    List<String> getListWarningMessages();

    void setListWarningMessages(List<String> listWarningMessages);
}
