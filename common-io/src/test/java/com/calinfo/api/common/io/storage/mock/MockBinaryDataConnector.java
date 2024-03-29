package com.calinfo.api.common.io.storage.mock;

import com.calinfo.api.common.io.storage.connector.BinaryDataConnector;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.io.IOUtils;
import org.springframework.scheduling.annotation.AsyncResult;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

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

    private List<String> domainCreated = new ArrayList<>();

    @Override
    public void upload(String spaceName, String id, InputStream in) throws IOException {

        ItemFile one = selectOne(spaceName, id);

        if (one == null){
            one = new ItemFile();
            one.setId(id);
            one.setSpaceName(spaceName);
            dataFile.add(one);
        }
        ItemFile finalOne = one;

        try(PipedInputStream pipedIn = new PipedInputStream();
        PipedOutputStream pipedOut = new PipedOutputStream(){
            @Override
            public void close() throws IOException {
                super.close();

                finalOne.setBinData(pipedIn.readAllBytes());
            }
        }) {
            pipedIn.connect(pipedOut);

            IOUtils.copy(in, pipedOut);
        }
    }

    @Override
    public void download(String spaceName, String id, OutputStream out) throws IOException {

        ItemFile one = selectOne(spaceName, id);

        if (one == null){
            return;
        }

        try(InputStream in = new ByteArrayInputStream(one.getBinData())){
            IOUtils.copy(in, out);
        }
    }

    @Override
    public Future<Boolean> delete(String spaceName, String id) {

        ItemFile one = selectOne(spaceName, id);

        return new AsyncResult<>(one == null ? false : dataFile.remove(one));
    }

    @Override
    public Future<Boolean> createSpace(String spaceName) throws IOException {

        domainCreated.add(spaceName);

        List<ItemSpace> one = dataSapce.stream().filter(i -> (i.getSpaceName() == null && spaceName == null) || (i.getSpaceName() != null && i.getSpaceName().equals(spaceName))).collect(Collectors.toList());

        if (one.isEmpty()){
            throw new IOException();
        }

        return new AsyncResult<>(one.get(0).isCreateReturnValue());
    }

    @Override
    public boolean isSpaceExist(String spaceName) throws IOException {
        return domainCreated.contains(spaceName);
    }

    @Override
    public Future<Boolean> deleteSpace(String spaceName) throws IOException {

        List<ItemFile> lstOne = dataFile.stream().filter(i -> ((spaceName == null && i.getSpaceName() == null) || i.getSpaceName().equals(spaceName))).collect(Collectors.toList());
        dataFile.removeAll(lstOne);

        List<ItemSpace> one = dataSapce.stream().filter(i -> i.getSpaceName().equals(spaceName)).collect(Collectors.toList());

        if (one.isEmpty()){
            throw new IOException();
        }

        return new AsyncResult<>(one.get(0).isDeleteReturnValue());
    }

    private ItemFile selectOne(String spaceName, String id){

        List<ItemFile> one = dataFile.stream().filter(i -> i.getId().equals(id) && ((spaceName == null && i.getSpaceName() == null) || i.getSpaceName().equals(spaceName))).collect(Collectors.toList());

        return one.isEmpty() ? null : one.get(0);
    }
}
