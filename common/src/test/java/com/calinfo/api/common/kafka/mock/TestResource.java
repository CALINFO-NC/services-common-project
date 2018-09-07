package com.calinfo.api.common.kafka.mock;

import com.calinfo.api.common.resource.Resource;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestResource extends Resource {

    private String name;

}
