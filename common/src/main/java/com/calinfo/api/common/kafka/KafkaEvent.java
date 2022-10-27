package com.calinfo.api.common.kafka;

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

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;


public class KafkaEvent {

    @Getter
    @Setter
    private String version = "1.0.0";

    @Getter
    @Setter
    private String domain;

    @Getter
    @Setter
    private KafkaUser user;

    @Getter
    @Setter
    private KafkaApplication application;

    @Getter
    @Setter
    private String topic;

    @Getter
    @Setter
    private KafkaMetadata metadata;

    @Getter
    @Setter
    private KafkaData data;

    @JsonIgnore
    public KafkatValue getValues(){
        return new KafkatValue(metadata, data);
    }
}
