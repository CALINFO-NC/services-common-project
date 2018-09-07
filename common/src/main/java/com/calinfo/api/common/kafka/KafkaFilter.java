package com.calinfo.api.common.kafka;

import com.calinfo.api.common.swagger.SwaggerCollector;
import com.calinfo.api.common.swagger.SwaggerItemCollector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.function.Function;


@ConditionalOnProperty("common.configuration.kafka.enabled")
@Component
@Order(KafkaFilter.ORDER_FILTER)
public class KafkaFilter extends OncePerRequestFilter {

    public static final int ORDER_FILTER = Integer.MAX_VALUE - 10000;

    private static final List<MediaType> RESPONSPE_MEDIA_TYPE = Arrays.asList(
            MediaType.APPLICATION_JSON,
            MediaType.APPLICATION_XML,
            MediaType.valueOf("application/*+json"),
            MediaType.valueOf("application/*+xml")
    );

    @Autowired
    private SwaggerCollector swaggerCollector;

    @Autowired
    private KafkaTopicNameResolver kafkaTopicNameResolver;

    /*@Qualifier("kafkaEventTemplate")
    @Autowired
    private KafkaTemplate<String, KafkaEvent> kafkaTemplate;*/
    @Autowired
    private KafkaTemplate<String, KafkaEvent> kafkaTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        doFilterWrapped(wrapRequest(request), wrapResponse(response), filterChain);
    }

    private void doFilterWrapped(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        }
        finally {
            afterRequest(request, response);
            response.copyBodyToResponse();
        }
    }


    private void afterRequest(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response) throws UnsupportedEncodingException {

        MediaType mediaType = MediaType.valueOf(response.getContentType());
        boolean sendKafkaMessage = RESPONSPE_MEDIA_TYPE.stream().anyMatch(visibleType -> visibleType.includes(mediaType));
        if (sendKafkaMessage){

            KafkaRequest kafkaRequest = new KafkaRequest();

            Enumeration<String> headers = request.getHeaderNames();
            kafkaRequest.setHeaders(copyHeader(headers, request::getHeader));
            kafkaRequest.setParameters(request.getParameterMap());
            kafkaRequest.setMethod(request.getMethod());
            kafkaRequest.setBody(contentToString(request.getContentAsByteArray(), request.getCharacterEncoding()));

            KafkaResponse kafkaResponse = new KafkaResponse();
            kafkaResponse.setStatus(response.getStatus());
            kafkaResponse.setHeaders(copyHeader(headers, response::getHeader));
            kafkaRequest.setBody(contentToString(response.getContentAsByteArray(), response.getCharacterEncoding()));

            String uri = request.getRequestURI();
            for (SwaggerItemCollector itemCollector: swaggerCollector.getSwaggerItemByUri(uri)){

                KafkaEvent kafkaEvent = new KafkaEvent();
                kafkaEvent.setRequest(kafkaRequest);
                kafkaEvent.setResponse(kafkaResponse);

                String topicName = kafkaTopicNameResolver.getTopicName(itemCollector);
                kafkaEvent.setTopic(topicName);

                kafkaTemplate.send(kafkaEvent.getTopic(), kafkaEvent);
            }
        }
    }

    private static String contentToString(byte[] content, String charEncoding) throws UnsupportedEncodingException {

        if (content.length > 0) {
            return new String(content, charEncoding);
        }
        else{
            return null;
        }
    }


    private static ContentCachingRequestWrapper wrapRequest(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper) {
            return (ContentCachingRequestWrapper) request;
        } else {
            return new ContentCachingRequestWrapper(request);
        }
    }

    private static ContentCachingResponseWrapper wrapResponse(HttpServletResponse response) {
        if (response instanceof ContentCachingResponseWrapper) {
            return (ContentCachingResponseWrapper) response;
        } else {
            return new ContentCachingResponseWrapper(response);
        }
    }

    private Map<String, String> copyHeader(Enumeration<String> headersSource, Function<String, String> headerGetter){

        Map<String, String> headersCible = new HashMap<>();
        while(headersSource.hasMoreElements()){
            String headerName = headersSource.nextElement();

            String headerValue = headerGetter.apply(headerName);
            headersCible.put(headerName, headerValue);
        }

        return headersCible;
    }

}
