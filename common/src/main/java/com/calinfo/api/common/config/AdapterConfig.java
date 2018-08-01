package com.calinfo.api.common.config;

import com.calinfo.api.common.utils.MiscUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Created by dalexis on 20/11/2017.
 */
@Configuration
public class AdapterConfig implements WebMvcConfigurer {


    /**
     * {@inheritDoc}
     */
    @Override
    public void configureMessageConverters(final List<HttpMessageConverter<?>> messageConverters) {

        final MappingJackson2HttpMessageConverter conv = new MappingJackson2HttpMessageConverter();

        conv.setObjectMapper(MiscUtils.getObjectMapper());
        messageConverters.add(conv);
    }


}
