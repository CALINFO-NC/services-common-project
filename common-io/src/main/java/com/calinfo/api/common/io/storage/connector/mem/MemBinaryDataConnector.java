package com.calinfo.api.common.io.storage.connector.mem;

/*-
 * #%L
 * common-io
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

import com.calinfo.api.common.io.storage.connector.BinaryDataConnector;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

@Component
@ConditionalOnProperty(prefix = "common-io.storage.connector", name = "provider", havingValue = "mem")
public class MemBinaryDataConnector implements BinaryDataConnector {

    private Map<MemId, Byte[]> data = new HashMap<>();

    @Override
    public void upload(String spaceName, String id, InputStream in) throws IOException {

        try(ByteArrayOutputStream out = new ByteArrayOutputStream()){
            IOUtils.copy(in, out);

            MemId memId = new MemId(spaceName, id);
            data.computeIfAbsent(memId, k -> ArrayUtils.toObject(out.toByteArray()));
        }
    }

    @Override
    public void download(String spaceName, String id, OutputStream out) throws IOException {
        MemId memId = new MemId(spaceName, id);
        Byte[] bin = data.get(memId);

        if (bin == null){
            throw new IOException(String.format("file with space name '%s' and id '%s' not exist", spaceName, id));
        }

        try(ByteArrayInputStream in = new ByteArrayInputStream(ArrayUtils.toPrimitive(bin))){
            IOUtils.copy(in , out);
        }

    }

    @Override
    @Async("binaryDataASyncOperation")
    public Future<Boolean> delete(String spaceName, String id) throws IOException {
        MemId memId = new MemId(spaceName, id);
        data.remove(memId);
        return new AsyncResult<>(true);
    }

    @Override
    @Async("binaryDataASyncOperation")
    public Future<Boolean> createSpace(String spaceName) throws IOException {
        return new AsyncResult<>(true);
    }

    @Override
    @Async("binaryDataASyncOperation")
    public Future<Boolean> deleteSpace(String spaceName) throws IOException {

        List<MemId> lstRmv = new ArrayList<>();
        for(Map.Entry<MemId, Byte[]> entry : data.entrySet()){
            if ((entry.getKey().getSpaceName() == null && spaceName == null) || (entry.getKey().getSpaceName() != null && entry.getKey().getSpaceName().equals(spaceName))){
                lstRmv.add(entry.getKey());
            }
        }

        lstRmv.stream().forEach(data::remove);

        return new AsyncResult<>(true);
    }
}
