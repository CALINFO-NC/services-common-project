package com.calinfo.api.common.tenant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by dalexis on 29/05/2018.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TenantJpaRepositories {

    String entityManagerFactoryRef();
    String transactionManagerRef();
    String basePackagesConfJsonPath();
}
