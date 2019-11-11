package com.calinfo.api.common.io.storage.connector.file;

import com.calinfo.api.common.io.storage.connector.AbstractBinaryDataConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("file")
public class FileBinaryDataConnectorTest extends AbstractBinaryDataConnector {


    @Autowired
    private FileBinaryDataConnector fileBinaryDataConnector;

    /**
     * Pour faire passer ce test, il faut mettre le bon paramétrage dans le fichier application-file.yml
     * 
     * @throws Exception
     */
    @Ignore("Ce test est ignoré car il ne peut pas passé en TU. On est dépendant du filesystem du développeur pour vérifier son bon fonctionnement")
    @Test
    public void call() throws Exception{
        super.call(fileBinaryDataConnector);
    }
}