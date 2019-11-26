package com.calinfo.api.common.io.storage.connector.ftp;

import com.calinfo.api.common.io.AutowiredConfig;
import com.calinfo.api.common.io.storage.connector.AbstractBinaryDataConnector;
import com.calinfo.api.common.io.storage.mock.RandomPortInitailizer;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@ContextConfiguration(initializers = RandomPortInitailizer.class)
@SpringBootTest(classes = {AutowiredConfig.class})
@DirtiesContext
@ActiveProfiles("ftp")
public class FtpBinaryDataConnectorTest extends AbstractBinaryDataConnector {


    @Autowired
    private FtpBinaryDataConnector ftpBinaryDataConnector;

    @Autowired
    private FtpConfigProperties ftpConfigProperties;

    private FakeFtpServer fakeFtpServer;

    @BeforeClass
    public void setup() {
        fakeFtpServer = new FakeFtpServer();
        fakeFtpServer.addUserAccount(new UserAccount(ftpConfigProperties.getUsername(), ftpConfigProperties.getPassword(), ftpConfigProperties.getPath()));

        FileSystem fileSystem = new UnixFakeFileSystem();
        fileSystem.add(new DirectoryEntry(ftpConfigProperties.getPath()));

        fakeFtpServer.setFileSystem(fileSystem);

        fakeFtpServer.setServerControlPort(ftpConfigProperties.getPort());

        fakeFtpServer.start();
    }

    @AfterClass
    public void teardown() {
        fakeFtpServer.stop();
    }

    @Test
    public void call() throws Exception{
        super.call(ftpBinaryDataConnector);
    }
}