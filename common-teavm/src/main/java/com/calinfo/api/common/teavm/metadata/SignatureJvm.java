package com.calinfo.api.common.teavm.metadata;

/*-
 * #%L
 * common-teavm
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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(of = { "nameSpaceAndMethodName" })
public class SignatureJvm {

    private String returnType = void.class.getName();

    private String classAndMethodName;

    private String nameSpaceAndMethodName;

    private List<String> parameters = new ArrayList<>();

    @Override
    public String toString() {
        return MessageFormat.format("{2} {0}({1})", classAndMethodName, String.join(", ", parameters.toArray(new String[parameters.size()])), returnType);
    }
}
