package com.calinfo.api.common.io.storage.connector.google;

/*-
 * #%L
 * common-io
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

import com.calinfo.api.common.io.storage.connector.BinaryDataConnector;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.ReadChannel;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import jakarta.ws.rs.InternalServerErrorException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Component
@ConditionalOnProperty(prefix = "common-io.storage.connector", name = "provider", havingValue = "google")
public class GoogleBinaryDataConnector implements BinaryDataConnector {

    @Autowired
    private GoogleConfigProperties googleConfigProperties;

    @Override
    public void upload(String spaceName, String id, InputStream in) throws IOException {

        Bucket bucket = getBucket(getStorage());
        Blob blob = bucket.get(getFileName(spaceName, id));

        // Création d'un blob vide s'il n'existe pas
        if (blob == null){
            Bucket.BlobTargetOption[] opt = new Bucket.BlobTargetOption [0];
            blob = bucket.create(getFileName(spaceName, id), new byte[0], opt);
        }

        WriteChannel writer = blob.writer();
        try (OutputStream out = Channels.newOutputStream(writer)){
            IOUtils.copy(in, out);
        }
    }

    @Override
    public void download(String spaceName, String id, OutputStream out) throws IOException {

        Bucket bucket = getBucket(getStorage());
        Blob blob = bucket.get(getFileName(spaceName, id));

        if (blob == null){
            throw new IOException(String.format("file with space name '%s' and id '%s' not exist", spaceName, id));
        }

        ReadChannel reader = blob.reader();
        try(InputStream in = Channels.newInputStream(reader)){
            IOUtils.copy(in, out);
        }
    }

    @Override
    @Async("binaryDataASyncOperation")
    public Future<Boolean> delete(String spaceName, String id) throws IOException {
        return new AsyncResult<>(getBucket(getStorage()).get(getFileName(spaceName, id)).delete());
    }

    @Override
    @Async("binaryDataASyncOperation")
    public Future<Boolean> createSpace(String spaceName) throws IOException{
        // Ici ilm n'y a rien à faire
        return new AsyncResult<>(getStorage() != null);
    }

    @Override
    public boolean isSpaceExist(String spaceName) throws IOException{
        try {
            return Boolean.TRUE.equals(createSpace(spaceName).get());
        } catch (InterruptedException | ExecutionException e) {
            throw new InternalServerErrorException(e);
        }
    }

    @Override
    @Async("binaryDataASyncOperation")
    public Future<Boolean> deleteSpace(String spaceName) throws IOException {

        String prefix = String.format("%s/", getReelSpaceName(spaceName));
        getBucket(getStorage()).list(Storage.BlobListOption.prefix(prefix)).iterateAll().forEach(Blob::delete);

        return new AsyncResult<>(true);
    }

    private String getFileName(String spaceName, String id){
        return String.format("%s/%s", getReelSpaceName(spaceName), id);
    }


    private Storage getStorage() throws IOException{

        try(InputStream inCredential = new ByteArrayInputStream(googleConfigProperties.getCredentials().getBytes())){

            Storage storage = StorageOptions.newBuilder()
                    .setCredentials(GoogleCredentials.fromStream(inCredential))
                    .setProjectId(googleConfigProperties.getProjectId())
                    .build().getService();

            return storage;
        }


    }

    private Bucket getBucket(Storage storage){
        return storage.get(googleConfigProperties.getBuckatName());
    }

    private String getReelSpaceName(String spaceName){
        String reelSpaceName = googleConfigProperties.getDefaultSpaceName();

        if (!StringUtils.isBlank(spaceName)){
            reelSpaceName = String.format("%s%s", googleConfigProperties.getPrefixSpaceName(), spaceName);
        }

        return reelSpaceName;
    }
}
