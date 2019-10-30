package com.calinfo.api.common.io.storage.connector.mem;

import com.calinfo.api.common.io.storage.connector.AbstractBinaryDataConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.testng.annotations.Test;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("mem")
public class MemBinaryDataConnectorTest extends AbstractBinaryDataConnector {

    @Autowired
    private MemBinaryDataConnector binaryDataConnector;

    @Test
    public void call() throws Exception{
        super.call(binaryDataConnector);
    }
}
