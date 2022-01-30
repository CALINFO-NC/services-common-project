package com.calinfo.api.common.io.storage.connector.file;

/*-
 * #%L
 * common-io
 * %%
 * Copyright (C) 2019 - 2022 CALINFO
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
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.concurrent.Future;

@Component
@ConditionalOnProperty(prefix = "common-io.storage.connector", name = "provider", havingValue = "file")
public class FileBinaryDataConnector implements BinaryDataConnector {

    @Autowired
    private FileConfigProperties fileConfigProperties;

    private static String testPath = null;

    @Override
    public void upload(String spaceName, String id, InputStream in) throws IOException {

        File outFile = getFile(spaceName, id);

        // Création d'un blob vide s'il n'existe pas
        if (outFile.exists()){
            FileUtils.forceDelete(outFile);
        }

        try (OutputStream out = new FileOutputStream(outFile)){
            IOUtils.copy(in, out);
        }
    }

    @Override
    public void download(String spaceName, String id, OutputStream out) throws IOException {

        File inFile = getFile(spaceName, id);

        if (!inFile.exists()){
            throw new IOException(String.format("file with space name '%s' and id '%s' not exist", spaceName, id));
        }

        try(InputStream in = new FileInputStream(inFile)){
            IOUtils.copy(in, out);
        }
    }

    @Override
    @Async("binaryDataASyncOperation")
    public Future<Boolean> delete(String spaceName, String id) throws IOException {

        File file = getFile(spaceName, id);
        FileUtils.forceDelete(file);
        return new AsyncResult<>(true);
    }

    @Override
    @Async("binaryDataASyncOperation")
    public Future<Boolean> createSpace(String spaceName) throws IOException{

        File pathSpace = getPath(spaceName);

        boolean result = false;
        if (!pathSpace.exists()){
            pathSpace.mkdirs();
            result = true;
        }

        return new AsyncResult<>(result);
    }

    @Override
    @Async("binaryDataASyncOperation")
    public Future<Boolean> deleteSpace(String spaceName) throws IOException {

        FileUtils.deleteDirectory(getPath(spaceName));

        return new AsyncResult<>(true);
    }

    @SneakyThrows
    private File getPath(String spaceName){

        String path = fileConfigProperties.getPath();
        if (fileConfigProperties.getPath().equals("_tmpFile")){

            if (testPath == null) {
                File fPath = File.createTempFile("commonio", "file");
                fPath.delete();
                fPath.mkdirs();
                path = fPath.getAbsolutePath();
                testPath = path;
            }
            else{
                path = testPath;
            }
        }
        return new File(String.format("%s/%s", path, getReelSpaceName(spaceName)));
    }

    private File getFile(String spaceName, String id){
        return new File(String.format("%s/%s", getPath(spaceName), id));
    }


    private String getReelSpaceName(String spaceName){
        String reelSpaceName = fileConfigProperties.getDefaultSpaceName();

        if (!StringUtils.isBlank(spaceName)){
            reelSpaceName = String.format("%s%s", fileConfigProperties.getPrefixSpaceName(), spaceName);
        }

        return reelSpaceName;
    }
}
