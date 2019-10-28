package com.calinfo.api.common.io.storage.connector.google;

/*-
 * #%L
 * common-io
 * %%
 * Copyright (C) 2019 CALINFO
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
import com.google.cloud.storage.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;

@Component
@ConditionalOnProperty(prefix = "common-io.storage.connector", name = "provider", havingValue = "google")
public class GoogleBinaryDataConnector implements BinaryDataConnector {

    @Autowired
    private GoogleConfigProperties googleConfigProperties;

    @Override
    public OutputStream getOutputStream(String spaceName, String id) throws IOException {
        WriteChannel writer = getBucket(spaceName).get(id).writer();
        return Channels.newOutputStream(writer);
    }

    @Override
    public InputStream getInputStream(String spaceName, String id) throws IOException {
        ReadChannel reader = getBucket(spaceName).get(id).reader();
        return Channels.newInputStream(reader);
    }

    @Override
    public boolean delete(String spaceName, String id) throws IOException {
        return getBucket(spaceName).get(id).delete();
    }

    @Override
    public boolean createSpace(String spaceName) throws IOException{

        try {
            return getStorage().create(BucketInfo.of(getReelSpaceName(spaceName))) != null;
        }
        catch (StorageException e){
            throw new IOException(e);
        }
    }

    @Override
    public boolean deleteSpace(String spaceName) throws IOException {
        return getBucket(spaceName).delete();
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

    private Bucket getBucket(String spaceName) throws IOException{



        return getStorage().get(getReelSpaceName(spaceName));
    }

    private String getReelSpaceName(String spaceName){
        String reelSpaceName = googleConfigProperties.getDefaultBuckateName();

        if (!StringUtils.isBlank(spaceName)){
            reelSpaceName = String.format("%s%s", googleConfigProperties.getPrefixSpaceName(), spaceName);
        }

        return reelSpaceName;
    }
}
