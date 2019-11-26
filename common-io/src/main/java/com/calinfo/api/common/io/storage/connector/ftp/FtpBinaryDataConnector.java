package com.calinfo.api.common.io.storage.connector.ftp;


import com.calinfo.api.common.io.storage.connector.BinaryDataConnector;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.concurrent.Future;

@Component
@ConditionalOnProperty(prefix = "common-io.storage.connector", name = "provider", havingValue = "ftp")
public class FtpBinaryDataConnector implements BinaryDataConnector {

    private static final Logger log = LoggerFactory.getLogger(FtpBinaryDataConnector.class);


    @Autowired
    private FtpConfigProperties ftpConfigProperties;

    @Override
    public void upload(String spaceName, String id, InputStream in) throws IOException {


        try(ByteArrayOutputStream outLog = new ByteArrayOutputStream()){

            try {
                FTPClient ftpClient = connectFtp(outLog);
                ftpClient.changeWorkingDirectory(getPath(spaceName));
                ftpClient.storeFile(id, in);

                logoutFtp(ftpClient);
                disconnectFtp(ftpClient);
            }
            catch (Exception e){
                String ftpLog = new String(outLog.toByteArray());
                throw new IOException(ftpLog, e);
            }

            String ftpLog = new String(outLog.toByteArray());
            log.error(ftpLog);
        }
    }

    @Override
    public void download(String spaceName, String id, OutputStream out) throws IOException {

        try(ByteArrayOutputStream outLog = new ByteArrayOutputStream()){

            try {
                FTPClient ftpClient = connectFtp(outLog);
                ftpClient.changeWorkingDirectory(getPath(spaceName));

                if (!ftpClient.retrieveFile(id, out)){
                    throw new IOException(String.format("file with space name '%s' and id '%s' not exist", spaceName, id));
                }
            }
            catch (Exception e){
                String ftpLog = new String(outLog.toByteArray());
                throw new IOException(ftpLog, e);
            }
        }
    }

    @Override
    @Async("binaryDataASyncOperation")
    public Future<Boolean> delete(String spaceName, String id) throws IOException {

        boolean result = false;
        try(ByteArrayOutputStream outLog = new ByteArrayOutputStream()){

            try {
                FTPClient ftpClient = connectFtp(outLog);
                ftpClient.changeWorkingDirectory(getPath(spaceName));
                result = ftpClient.deleteFile(id);
                

            }
            catch (Exception e){
                String ftpLog = new String(outLog.toByteArray());
                throw new IOException(ftpLog, e);
            }
        }

        return new AsyncResult<>(result);
    }

    @Override
    @Async("binaryDataASyncOperation")
    public Future<Boolean> createSpace(String spaceName) throws IOException{

        boolean result = false;
        try(ByteArrayOutputStream outLog = new ByteArrayOutputStream()){

            try {
                FTPClient ftpClient = connectFtp(outLog);
                result = ftpClient.makeDirectory(getPath(spaceName));

            }
            catch (Exception e){
                String ftpLog = new String(outLog.toByteArray());
                throw new IOException(ftpLog, e);
            }
        }

        return new AsyncResult<>(result);
    }

    @Override
    @Async("binaryDataASyncOperation")
    public Future<Boolean> deleteSpace(String spaceName) throws IOException {

        boolean result = false;
        try(ByteArrayOutputStream outLog = new ByteArrayOutputStream()){

            try {
                FTPClient ftpClient = connectFtp(outLog);
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
        }

        return new AsyncResult<>(result);
    }


    private String getPath(String spaceName){


        return spaceName == null ? String.format("%s/%s", ftpConfigProperties.getPath(), ftpConfigProperties.getDefaultSpaceName()) : String.format("%s/%s%s", ftpConfigProperties.getPath(), ftpConfigProperties.getPrefixSpaceName(), spaceName);
    }


    private FTPClient connectFtp (OutputStream outLog) throws IOException {

        FTPClient ftp = new FTPClient();
        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(outLog)));

        ftp.connect(ftpConfigProperties.getHost(), ftpConfigProperties.getPort());



        if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
            throw new IOException(String.format("FTP connection refused to %s:%s", ftpConfigProperties.getHost(), ftpConfigProperties.getPort()));
        }

        ftp.login(ftpConfigProperties.getUsername(), ftpConfigProperties.getPassword());
        ftp.setFileType(FTP.BINARY_FILE_TYPE);
        ftp.enterLocalPassiveMode();

        return ftp;

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
