package com.calinfo.api.common.config;

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
