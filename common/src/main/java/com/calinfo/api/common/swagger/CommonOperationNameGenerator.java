package com.calinfo.api.common.swagger;

/*-
 * #%L
 * common
 * %%
 * Copyright (C) 2019 - 2021 CALINFO
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


import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.OperationNameGenerator;
import springfox.documentation.service.ApiInfo;


@Component
@Primary
@ConditionalOnClass({ApiInfo.class})
@ConditionalOnProperty(value = "common.configuration.swagger.name-generator.enabled", matchIfMissing = true)
public class CommonOperationNameGenerator implements OperationNameGenerator {

    @Override
    public String startingWith(String prefix) {
        return prefix;
    }
}
