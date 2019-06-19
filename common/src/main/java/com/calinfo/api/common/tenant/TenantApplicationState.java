package com.calinfo.api.common.tenant;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;


@Component
@ConditionalOnProperty(TenantProperties.CONDITIONNAL_PROPERTY)
public class TenantApplicationState {


    @Getter
    @Setter
    private boolean started = false;
}
