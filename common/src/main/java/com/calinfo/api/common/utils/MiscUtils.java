package com.calinfo.api.common.utils;

import com.calinfo.api.common.ErrorMessageFieldConvertor;
import com.calinfo.api.common.ex.ApplicationErrorException;
import com.calinfo.api.common.ex.MessageStatusException;
import com.calinfo.api.common.resource.BadResponseResource;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Created by dalexis on 20/11/2017.
 */
public class MiscUtils {


    private final static Logger logger = LoggerFactory.getLogger(MiscUtils.class);

    public static ObjectMapper getObjectMapper(){
        final ObjectMapper result = new ObjectMapper();

        result.registerModule(new JavaTimeModule());

        // Format des date transitant dans le JSon
        result.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        // Ignorée les champs absent du model et présent dans le Json
        result.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return result;
    }

    private static ResponseEntity<String> getResponse(RestTemplate restTemplate, URI uri, HttpMethod httpMethod, HttpEntity<?> entity){

        try {
            return restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
        }
        catch(HttpClientErrorException e){
            return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
        }
        catch(Throwable e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static <T> T callRestApiService(RestTemplate restTemplate, URI uri, HttpMethod httpMethod, HttpEntity<?> entity, Class<T> clazz, ErrorMessageFieldConvertor convertor) {

        ResponseEntity<String> response = getResponse(restTemplate, uri, HttpMethod.GET, entity);
        ObjectMapper objectMapper = MiscUtils.getObjectMapper();

        if (response.getStatusCode() == HttpStatus.OK){

            if (response.getBody() == null) {
                return null;
            }

            try {
                return objectMapper.readValue(response.getBody(), clazz);
            } catch (IOException e) {
                throw new ApplicationErrorException(e);
            }
        }
        else if (response.getStatusCode() == HttpStatus.NOT_IMPLEMENTED && convertor != null){

            try {
                BadResponseResource result = new BadResponseResource();
                BadResponseResource resp = objectMapper.readValue(response.getBody(), BadResponseResource.class);
                result.setListErrorMessages(resp.getListErrorMessages());

                for (Map.Entry<String, List<String>> item : resp.getMapErrorMessagesFields().entrySet()){
                    result.getMapErrorMessagesFields().put(convertor.convert(item.getKey(), item.getValue()), item.getValue());
                }

                throw new MessageStatusException(response.getStatusCode(), objectMapper.writeValueAsString(result));
            } catch (IOException e) {
                throw new ApplicationErrorException(e);
            }

        } else {
            throw new MessageStatusException(response.getStatusCode(), response.getBody());
        }
    }

    public static <T> T callRestApiService(RestTemplate restTemplate, URI uri, HttpMethod httpMethod, HttpEntity<?> entity, Class<T> clazz){
        return callRestApiService(restTemplate, uri, httpMethod, entity, clazz, null);
    }
}
