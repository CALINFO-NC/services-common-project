package com.calinfo.api.common.io.storage.scheduler;

import com.calinfo.api.common.io.AutowiredConfig;
import com.calinfo.api.common.io.storage.mock.MockBinaryDataConnector;
import com.calinfo.api.common.io.storage.mock.MockBinaryDataDomainService;
import com.calinfo.api.common.io.storage.mock.MockBinaryDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest(classes = {AutowiredConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("storage")
public class TransfertStorageSchedulerTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private TransfertStorageScheduler transfertStorageScheduler;

    @Autowired
    private MockBinaryDataConnector mockBinaryDataConnector;

    @Autowired
    private MockBinaryDataService mockBinaryDataService;

    @Autowired
    private MockBinaryDataDomainService mockBinaryDataDomainService;

    @Test
    public void call(){

        mockBinaryDataDomainService.getData().add("dom1");
        mockBinaryDataDomainService.getData().add("dom2");

        mockBinaryDataConnector.getDataSapce().add(new MockBinaryDataConnector.ItemSpace("dom1", true, true));
        mockBinaryDataConnector.getDataSapce().add(new MockBinaryDataConnector.ItemSpace("dom2", true, true));
        mockBinaryDataConnector.getDataSapce().add(new MockBinaryDataConnector.ItemSpace(null, true, true));

        mockBinaryDataService.getData().add(new MockBinaryDataService.Item("id1", "dom1", true, new ByteArrayInputStream("ABC".getBytes())));
        mockBinaryDataService.getData().add(new MockBinaryDataService.Item("id2", "dom1", true, new ByteArrayInputStream("123".getBytes())));
        mockBinaryDataService.getData().add(new MockBinaryDataService.Item("id1", null, true, new ByteArrayInputStream("ABC123".getBytes())));

        transfertStorageScheduler.runTransfertBinaryData();


        //////////////////////
        List<MockBinaryDataConnector.ItemFile> oneFile = mockBinaryDataConnector.getDataFile().stream().filter(i -> i.getId().equals("id1") && i.getSpaceName() != null && i.getSpaceName().equals("dom1")).collect(Collectors.toList());
        Assert.assertEquals(1, oneFile.size());
        Assert.assertEquals("ABC", new String(oneFile.get(0).getBinData()));

        oneFile = mockBinaryDataConnector.getDataFile().stream().filter(i -> i.getId().equals("id2") && i.getSpaceName() != null && i.getSpaceName().equals("dom1")).collect(Collectors.toList());
        Assert.assertEquals(1, oneFile.size());
        Assert.assertEquals("123", new String(oneFile.get(0).getBinData()));

        oneFile = mockBinaryDataConnector.getDataFile().stream().filter(i -> i.getId().equals("id1") && i.getSpaceName() == null).collect(Collectors.toList());
        Assert.assertEquals(1, oneFile.size());
        Assert.assertEquals("ABC123", new String(oneFile.get(0).getBinData()));

        //////////////////////
        List<MockBinaryDataService.Item> oneItem = mockBinaryDataService.getData().stream().filter(i -> i.getId().equals("id1") && i.getDomain() != null && i.getDomain().equals("dom1")).collect(Collectors.toList());
        Assert.assertEquals(1, oneItem.size());
        Assert.assertTrue(oneItem.get(0).isFinalizeSuccess());

        oneItem = mockBinaryDataService.getData().stream().filter(i -> i.getId().equals("id2") && i.getDomain() != null && i.getDomain().equals("dom1")).collect(Collectors.toList());
        Assert.assertEquals(1, oneItem.size());
        Assert.assertTrue(oneItem.get(0).isFinalizeSuccess());

        oneItem = mockBinaryDataService.getData().stream().filter(i -> i.getId().equals("id1") && i.getDomain() == null).collect(Collectors.toList());
        Assert.assertEquals(1, oneItem.size());
        Assert.assertTrue(oneItem.get(0).isFinalizeSuccess());
    }
}
