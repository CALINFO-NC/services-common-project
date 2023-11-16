package com.calinfo.api.common.io.storage.connector.ftp;

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


import com.calinfo.api.common.ex.ApplicationErrorException;
import com.calinfo.api.common.io.storage.connector.BinaryDataConnector;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "common-io.storage.connector", name = "provider", havingValue = "ftp")
public class FtpBinaryDataConnector implements BinaryDataConnector {


    @Autowired
    private FtpConfigProperties ftpConfigProperties;

    @Override
    public void upload(String spaceName, String id, InputStream in) throws IOException {

        FTPClient ftpClient = new FTPClient();
        try(ByteArrayOutputStream outLog = new ByteArrayOutputStream()){

            try {
                connectFtp(ftpClient, outLog);
                ftpClient.changeWorkingDirectory(getPath(spaceName));
                ftpClient.storeFile(id, in);
            }
            catch (Exception e){
                String ftpLog = new String(outLog.toByteArray());
                throw new IOException(ftpLog, e);
            }
            finally {
                logoutFtp(ftpClient);
                disconnectFtp(ftpClient);
            }
        }
    }

    @Override
    public void download(String spaceName, String id, OutputStream out) throws IOException {

        FTPClient ftpClient = new FTPClient();
        try(ByteArrayOutputStream outLog = new ByteArrayOutputStream()){

            try {
                connectFtp(ftpClient, outLog);
                ftpClient.changeWorkingDirectory(getPath(spaceName));

                if (!ftpClient.retrieveFile(id, out)){
                    throw new IOException(String.format("file with space name '%s' and id '%s' not exist", spaceName, id));
                }
            }
            catch (Exception e){
                String ftpLog = new String(outLog.toByteArray());
                throw new IOException(ftpLog, e);
            }
            finally {
                logoutFtp(ftpClient);
                disconnectFtp(ftpClient);
            }
        }
    }

    @Override
    @Async("binaryDataASyncOperation")
    public Future<Boolean> delete(String spaceName, String id) throws IOException {

        FTPClient ftpClient = new FTPClient();
        boolean result = false;
        try(ByteArrayOutputStream outLog = new ByteArrayOutputStream()){

            try {
                connectFtp(ftpClient, outLog);
                ftpClient.changeWorkingDirectory(getPath(spaceName));
                result = ftpClient.deleteFile(id);
            }
            catch (Exception e){
                String ftpLog = new String(outLog.toByteArray());
                throw new IOException(ftpLog, e);
            }
            finally {
                logoutFtp(ftpClient);
                disconnectFtp(ftpClient);
            }
        }

        return new AsyncResult<>(result);
    }

    @Override
    @Async("binaryDataASyncOperation")
    public Future<Boolean> createSpace(String spaceName) throws IOException{

        FTPClient ftpClient = new FTPClient();
        boolean result = false;
        try(ByteArrayOutputStream outLog = new ByteArrayOutputStream()){

            try {
                connectFtp(ftpClient, outLog);
                result = ftpClient.makeDirectory(getPath(spaceName));

            }
            catch (Exception e){
                String ftpLog = new String(outLog.toByteArray());
                throw new IOException(ftpLog, e);
            }
            finally {
                logoutFtp(ftpClient);
                disconnectFtp(ftpClient);
            }
        }

        return new AsyncResult<>(result);
    }

    @Override
    public boolean isSpaceExist(String spaceName) throws IOException{
        try {
            return Boolean.TRUE.equals(createSpace(spaceName).get());
        } catch (ExecutionException e) {
            throw new ApplicationErrorException(e);
        }
        catch (InterruptedException e){
            Thread.currentThread().interrupt();
            throw new ApplicationErrorException(e);
        }
    }

    @Override
    @Async("binaryDataASyncOperation")
    public Future<Boolean> deleteSpace(String spaceName) throws IOException {

        FTPClient ftpClient = new FTPClient();
        boolean result = false;
        try(ByteArrayOutputStream outLog = new ByteArrayOutputStream()){

            try {
                connectFtp(ftpClient, outLog);
                ftpClient.changeWorkingDirectory(getPath(spaceName));

                FTPFile[] lstFile = ftpClient.listFiles();
                for (FTPFile ff : lstFile){
                    ftpClient.deleteFile(ff.getName());
                }

                result = ftpClient.removeDirectory(getPath(spaceName));
            }
            catch (Exception e){
                String ftpLog = new String(outLog.toByteArray());
                throw new IOException(ftpLog, e);
            }
            finally {
                logoutFtp(ftpClient);
                disconnectFtp(ftpClient);
            }
        }

        return new AsyncResult<>(result);
    }


    private String getPath(String spaceName){


        return spaceName == null ? String.format("%s/%s", ftpConfigProperties.getPath(), ftpConfigProperties.getDefaultSpaceName()) : String.format("%s/%s%s", ftpConfigProperties.getPath(), ftpConfigProperties.getPrefixSpaceName(), spaceName);
    }


    private void connectFtp (FTPClient ftp, OutputStream outLog) throws IOException {

        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(outLog)));

        ftp.connect(ftpConfigProperties.getHost(), ftpConfigProperties.getPort());



        if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
            throw new IOException(String.format("FTP connection refused to %s:%s", ftpConfigProperties.getHost(), ftpConfigProperties.getPort()));
        }

        ftp.login(ftpConfigProperties.getUsername(), ftpConfigProperties.getPassword());
        ftp.setFileType(FTP.BINARY_FILE_TYPE);
        ftp.enterLocalPassiveMode();


    }

    private void disconnectFtp(FTPClient ftpClient){

        if (ftpClient.isConnected()){
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                log.warn(e.getMessage(), e);
            }
        }

    }


    private void logoutFtp(FTPClient ftpClient){

        if (ftpClient.isConnected()){
            try {
                ftpClient.logout();
            } catch (IOException e) {
                log.warn(e.getMessage(), e);
            }
        }

    }

}
