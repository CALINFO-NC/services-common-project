package com.calinfo.api.common.io.storage.connector;

import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class AbstractBinaryDataConnector extends AbstractTestNGSpringContextTests {

    public void call(BinaryDataConnector binaryDataConnector) throws Exception{

        binaryDataConnector.createSpace("toto");
        binaryDataConnector.createSpace(null);

        upload(binaryDataConnector, "toto", "id1", "AZERT");
        upload(binaryDataConnector, "toto", "id2", "1234");
        upload(binaryDataConnector, "toto", "id3", "1234AZERT");
        upload(binaryDataConnector, null, "id1", ",;:=azaza");
        upload(binaryDataConnector, null, "id2", ",;:=3997");
        upload(binaryDataConnector, null, "id3", "azae&@é");

        assertValue(binaryDataConnector, "toto", "id1", "AZERT");
        assertValue(binaryDataConnector, "toto", "id2", "1234");
        assertValue(binaryDataConnector, "toto", "id3", "1234AZERT");
        assertValue(binaryDataConnector, null, "id1", ",;:=azaza");
        assertValue(binaryDataConnector, null, "id2", ",;:=3997");
        assertValue(binaryDataConnector, null, "id3", "azae&@é");

        binaryDataConnector.delete("toto", "id1");
        binaryDataConnector.delete(null, "id1");

        assertValue(binaryDataConnector, "toto", "id1", null);
        assertValue(binaryDataConnector, "toto", "id2", "1234");
        assertValue(binaryDataConnector, "toto", "id3", "1234AZERT");
        assertValue(binaryDataConnector, null, "id1", null);
        assertValue(binaryDataConnector, null, "id2", ",;:=3997");
        assertValue(binaryDataConnector, null, "id3", "azae&@é");

        binaryDataConnector.deleteSpace("toto");
        binaryDataConnector.deleteSpace(null);

        assertValue(binaryDataConnector, "toto", "id1", null);
        assertValue(binaryDataConnector, "toto", "id2", null);
        assertValue(binaryDataConnector, "toto", "id3", null);
        assertValue(binaryDataConnector, null, "id1", null);
        assertValue(binaryDataConnector, null, "id2", null);
        assertValue(binaryDataConnector, null, "id3", null);
    }

    private void upload(BinaryDataConnector binaryDataConnector, String spaceName, String id, String data) throws IOException {

        try(InputStream in = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8))){
            binaryDataConnector.upload(spaceName, id, in);
        }
    }

    private void assertValue(BinaryDataConnector binaryDataConnector, String spaceName, String id, String result){

        String val;

        try(ByteArrayOutputStream out = new ByteArrayOutputStream()){

            binaryDataConnector.download(spaceName, id, out);
            val = new String(out.toByteArray(), StandardCharsets.UTF_8);
        }
        catch (IOException e){
            val = null;
        }

        Assert.assertEquals(result, val);
    }
}
