package com.calinfo.api.common.utils;

/*-
 * #%L
 * common
 * %%
 * Copyright (C) 2019 - 2022 CALINFO
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.calinfo.api.common.ErrorMessageFieldConvertor;
import com.calinfo.api.common.MessageStructure;
import com.calinfo.api.common.dto.BadResponseDto;
import com.calinfo.api.common.ex.ApplicationErrorException;
import com.calinfo.api.common.ex.MessageException;
import com.calinfo.api.common.ex.MessageStatusException;
import com.calinfo.api.common.kafka.KafkaTopicPrefix;
import com.calinfo.api.common.kafka.KafkaTopic;
import com.calinfo.api.common.service.MessageService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.base.Optional;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.spi.service.contexts.OperationContext;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by dalexis on 20/11/2017.
 */
public class MiscUtils {

    /**
     * Permet déviter que l'on puisse instancier une classe utilitaire
     */
    private MiscUtils(){
    }

    public static ObjectMapper getObjectMapper(){
        final ObjectMapper result = new ObjectMapper();
        configureObjectMapper(result);
        return result;
    }

    public static void configureObjectMapper(ObjectMapper mapper){

        mapper.registerModule(new JavaTimeModule());

        // Format des date transitant dans le JSon
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        // Ignorée les champs absent du model et présent dans le Json
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private static ResponseEntity<String> getResponse(RestTemplate restTemplate, URI uri, HttpMethod httpMethod, HttpEntity<?> entity){

        try {
            return restTemplate.exchange(uri, httpMethod, entity, String.class);
        }
        catch(HttpClientErrorException e){
            return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static <T> T callRestApiService(RestTemplate restTemplate, URI uri, HttpMethod httpMethod, HttpEntity<?> entity, Class<T> clazz, ErrorMessageFieldConvertor convertor) {

        ResponseEntity<String> response = getResponse(restTemplate, uri, httpMethod, entity);
        ObjectMapper objectMapper = MiscUtils.getObjectMapper();

        try {
            if (response.getStatusCode() == HttpStatus.OK) {

                if (StringUtils.isBlank(response.getBody())){
                    return null;
                }

                return objectMapper.readValue(response.getBody(), clazz);
            }
            else if (response.getStatusCode() == HttpStatus.NOT_IMPLEMENTED && convertor != null){

                BadResponseDto result = new BadResponseDto();
                BadResponseDto resp = objectMapper.readValue(response.getBody(), BadResponseDto.class);
                result.setListErrorMessages(resp.getListErrorMessages());

                for (Map.Entry<String, List<String>> item : resp.getMapErrorMessagesFields().entrySet()){
                    result.getMapErrorMessagesFields().put(convertor.convert(item.getKey(), item.getValue()), item.getValue());
                }

                throw new MessageStatusException(response.getStatusCode(), objectMapper.writeValueAsString(result));


            } else {
                throw new MessageStatusException(response.getStatusCode(), response.getBody());
            }
        } catch (IOException e) {
            throw new ApplicationErrorException(e);
        }
    }

    public static <T> T callRestApiService(RestTemplate restTemplate, URI uri, HttpMethod httpMethod, HttpEntity<?> entity, Class<T> clazz){
        return callRestApiService(restTemplate, uri, httpMethod, entity, clazz, null);
    }

    public static BadResponseDto messageExceptionToBadResponseDto(MessageException ex, MessageService messageService, Locale locale){

        BadResponseDto result = new BadResponseDto();
        for (MessageStructure ms: ex.getErrors().getGlobalErrors()){
            result.getListErrorMessages().add(messageService.translate(locale, ms.getMessageCode(), ms.getParameters().stream().toArray(size -> new Serializable[size])));
        }

        for (Map.Entry<String, List<MessageStructure>> entry : ex.getErrors().getFieldsErrors().entrySet()){
            List<MessageStructure> value = entry.getValue();
            List<String> lstMsg = new ArrayList<>();

            for (MessageStructure ms: value){
                String msg = messageService.translate(locale, ms.getMessageCode(), ms.getParameters().stream().toArray(size -> new Serializable[size]));
                lstMsg.add(msg);
            }

            result.getMapErrorMessagesFields().put(entry.getKey(), lstMsg);
        }

        return result;
    }

    public static String getTopicFullKey(String prefixTopicKey, String topicKey){

        String fullTopicKey = topicKey;
        if (!StringUtils.isBlank(prefixTopicKey)){
            fullTopicKey = String.format("%s.%s", prefixTopicKey, topicKey);
        }
        return fullTopicKey;
    }

    public static String getTopicFullName(String applicationId, String domainName, KafkaTopic kafkaTopic, KafkaTopicPrefix kafkaTopicPrefix){
        String topicName = getTopicFullKey(kafkaTopicPrefix != null ? kafkaTopicPrefix.value() : null, kafkaTopic.value());
        return getTopicFullName(applicationId, domainName, topicName, kafkaTopic.prefixTopicNameWithApplicationId(), kafkaTopic.prefixTopicNameWithDomain());
    }

    public static String getTopicFullName(String applicationId, String domainName, String topicName, boolean prefixTopicNameWithApplicationName, boolean prefixTopicNameWithDomain){

        String domain = domainName;
        if (domain == null){
            domain = "";
        }

        String result = topicName;
        if (prefixTopicNameWithDomain) {
            result = String.format("%s---%s", domain, result);
        }
        if (prefixTopicNameWithApplicationName) {
            result = String.format("%s---%s", applicationId, result);
        }

        return result;
    }

    public static String getNickNameSwagger(OperationContext operationContext){

        String nickname = "";
        Optional<ApiOperation> apiOperation = operationContext.findAnnotation(ApiOperation.class);
        if (apiOperation.isPresent()){
            nickname = apiOperation.get().nickname();
        }

        if (StringUtils.isBlank(nickname)) {
            nickname = operationContext.getName();
        }

        return nickname;
    }

    public static String getActualMethodName(){
        return new Throwable().getStackTrace()[1].getMethodName();
    }
}
