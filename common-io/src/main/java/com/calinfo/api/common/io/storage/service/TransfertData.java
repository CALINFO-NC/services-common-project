package com.calinfo.api.common.io.storage.service;

/*-
 * #%L
 * common-io
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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TransfertData implements Closeable {

    private InputStream inputStream;

    private Long version;

    @Override
    public void close() throws IOException {
        inputStream.close();
    }
}
