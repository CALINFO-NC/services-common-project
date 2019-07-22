package com.calinfo.api.common.tenant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Created by dalexis on 29/05/2018.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DomainContext {

    private static ThreadLocal<String> currentTenant = new ThreadLocal<>();

    public static void setDomain(String tenant) {
        currentTenant.set(tenant);
    }

    public static String getDomain() {
        return currentTenant.get();
    }

    public static boolean isDomainInitialized(){
        return getDomain() != null;
    }
}
