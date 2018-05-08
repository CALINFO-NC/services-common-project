package com.calinfo.api.common.tenant;

/**
 * Created by dalexis on 08/05/2018.
 */
public class TenantError extends Error {

    public TenantError(String message, Throwable cause) {
        super(message, cause);
    }
}
