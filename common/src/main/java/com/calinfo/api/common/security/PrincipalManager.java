package com.calinfo.api.common.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Created by dalexis on 05/04/2018.
 */

@Component
public class PrincipalManager {


    private static final Logger log = LoggerFactory.getLogger(PrincipalManager.class);

    /**
     * @return
     */
    public AbstractCommonPrincipal getPrincipal(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null){
            return null;
        }

        return (AbstractCommonPrincipal) authentication.getPrincipal();
    }

}
