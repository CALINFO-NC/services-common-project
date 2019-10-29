package com.calinfo.api.common.io.storage.mock;

import com.calinfo.api.common.io.storage.connector.BinaryDataConnector;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Profile("storage")
public class MockBinaryDataConnector implements BinaryDataConnector {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class ItemFile {

        private String id;

        private String spaceName;

        private byte[] binData;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class ItemSpace {

        private String spaceName;

        private boolean createReturnValue;

        private boolean deleteReturnValue;
    }

    @Getter
    private List<ItemFile> dataFile = new ArrayList<>();

    @Getter
    private List<ItemSpace> dataSapce = new ArrayList<>();


    @Override
    public OutputStream getOutputStream(String spaceName, String id) throws IOException {

        ItemFile one = selectOne(spaceName, id);

        if (one == null){
            one = new ItemFile();
            one.setId(id);
            one.setSpaceName(spaceName);
            dataFile.add(one);
        }
        ItemFile finalOne = one;

        PipedInputStream pipedIn = new PipedInputStream();
        PipedOutputStream pipedOut = new PipedOutputStream(){
            @Override
            public void close() throws IOException {
                super.close();

                finalOne.setBinData(pipedIn.readAllBytes());
            }
        };
        pipedIn.connect(pipedOut);

        return pipedOut;
    }

    @Override
    public InputStream getInputStream(String spaceName, String id) throws IOException {


        ItemFile one = selectOne(spaceName, id);

        if (one == null){
            return null;
        }

        return new ByteArrayInputStream(one.getBinData());
    }

    @Override
    public boolean delete(String spaceName, String id) throws IOException {

        ItemFile one = selectOne(spaceName, id);

        return one == null ? false : dataFile.remove(one);
    }

    @Override
    public boolean createSpace(String spaceName) throws IOException {

        List<ItemSpace> one = dataSapce.stream().filter(i -> i.getSpaceName().equals(spaceName)).collect(Collectors.toList());

        if (one.isEmpty()){
            throw new IOException();
        }

        return one.get(0).isCreateReturnValue();
    }

    @Override
    public boolean deleteSpace(String spaceName) throws IOException {

        List<ItemFile> lstOne = dataFile.stream().filter(i -> ((spaceName == null && i.getSpaceName() == null) || i.getSpaceName().equals(spaceName))).collect(Collectors.toList());
        dataFile.removeAll(lstOne);

        List<ItemSpace> one = dataSapce.stream().filter(i -> i.getSpaceName().equals(spaceName)).collect(Collectors.toList());

        if (one.isEmpty()){
            throw new IOException();
        }

        return one.get(0).isDeleteReturnValue();
    }

    private ItemFile selectOne(String spaceName, String id){

        List<ItemFile> one = dataFile.stream().filter(i -> i.getId().equals(id) && ((spaceName == null && i.getSpaceName() == null) || i.getSpaceName().equals(spaceName))).collect(Collectors.toList());

        return one.isEmpty() ? null : one.get(0);
    }
}
