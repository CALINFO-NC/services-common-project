package com.calinfo.api.common.io.storage.connector.webdav;

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
import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import com.github.sardine.impl.SardineException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.concurrent.Future;

@Slf4j
@RequiredArgsConstructor
@Component
@ConditionalOnProperty(prefix = "common-io.storage.connector", name = "provider", havingValue = "webdav")
public class WebdavBinaryDataConnector implements BinaryDataConnector {

    private final WebdavConfigProperties webdavConfigProperties;

    @Override
    public void upload(String spaceName, String id, InputStream in) throws IOException {
        String url = getUrl(spaceName, id);
        Sardine sardine = SardineFactory.begin(this.webdavConfigProperties.getUsername(), this.webdavConfigProperties.getPassword());
        sardine.put(url, in);
    }

    @Override
    public void download(String spaceName, String id, OutputStream out) throws IOException {
        String url = getUrl(spaceName, id);

        Sardine sardine = SardineFactory.begin(this.webdavConfigProperties.getUsername(), this.webdavConfigProperties.getPassword());

        try (InputStream in = sardine.get(url)) {
            IOUtils.copy(in, out);
        }
    }

    @Override
    public Future<Boolean> delete(String spaceName, String id) throws IOException {

        String url = getUrl(spaceName, id);
        Sardine sardine = SardineFactory.begin(this.webdavConfigProperties.getUsername(), this.webdavConfigProperties.getPassword());
        try {
            sardine.delete(url);
        } catch (IOException e) {

            // silently return if file to delete not found in webdav
            if (e instanceof SardineException && ((SardineException) e).getStatusCode() == HttpStatus.NOT_FOUND.value()) {
                log.warn("Impossible de supprimer le fichier '" + url + "' car il n'existe pas dans le webdav");
                log.debug(e.getMessage(), e);
                return new AsyncResult<>(true);
            }

            throw e;
        }
        return new AsyncResult<>(true);
    }

    @Override
    public Future<Boolean> createSpace(String spaceName) throws IOException {

        String url = getUrl(spaceName);
        Sardine sardine = SardineFactory.begin(this.webdavConfigProperties.getUsername(), this.webdavConfigProperties.getPassword());

        boolean exists = sardine.exists(url);

        if (!exists) {
            sardine.createDirectory(url);
        }

        return new AsyncResult<>(!exists);
    }

    @Override
    public boolean isSpaceExist(String spaceName) throws IOException {
        String url = getUrl(spaceName);
        Sardine sardine = SardineFactory.begin(this.webdavConfigProperties.getUsername(), this.webdavConfigProperties.getPassword());

        return sardine.exists(url);

    }

    @Override
    public Future<Boolean> deleteSpace(String spaceName) throws IOException {

        String url = getUrl(spaceName);
        Sardine sardine = SardineFactory.begin(this.webdavConfigProperties.getUsername(), this.webdavConfigProperties.getPassword());

        for (DavResource item : sardine.list(url)) {

            if (!item.getName().equals(spaceName)) {
                String itemUrl = String.format("%s/%s/%s", this.webdavConfigProperties.getUrl(), spaceName, item.getName());

                try {
                    sardine.delete(itemUrl);
                }
                catch (SardineException e){
                    if (e.getStatusCode() == 404){
                        log.debug( e.getMessage(), e);
                    }
                }
            }
        }

        return new AsyncResult<>(true);
    }

    private String getUrl(String... pathItem) {
        return String.format("%s/%s", this.webdavConfigProperties.getUrl(), String.join("/", pathItem));
    }
}
