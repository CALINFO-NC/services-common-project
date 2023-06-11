package com.calinfo.api.common.io.storage.connector.webdav;

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
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

@SpringBootTest(classes = {AutowiredConfig.class})
@DirtiesContext
@ActiveProfiles("webdav")
public class WebdavBinaryDataConnectorTest extends AbstractBinaryDataConnector {


    @Autowired
    private WebdavBinaryDataConnector webdavBinaryDataConnector;


    @Ignore("Ce test est ignoré car il ne peut pas passé en TU. On est dépendant d'une' plateforme webdav pour vérifier son bon fonctionnement")
    @Test
    public void call() throws Exception{
        super.call(webdavBinaryDataConnector);
    }
}
