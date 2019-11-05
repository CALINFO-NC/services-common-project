package com.calinfo.api.common.io.storage.connector.file;


import com.calinfo.api.common.io.storage.connector.BinaryDataConnector;
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

    private File getPath(String spaceName){
        return new File(String.format("%s/%s", fileConfigProperties.getPath(), getReelSpaceName(spaceName)));
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
