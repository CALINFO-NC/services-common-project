package com.calinfo.api.common.security;

/*-
 * #%L
 * common
 * %%
 * Copyright (C) 2019 - 2023 CALINFO
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

import com.calinfo.api.common.ex.MessageStatusException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.security.Principal;

public class UnknownPrincipalException extends MessageStatusException {

    @Getter
    private final Serializable principal;

    public UnknownPrincipalException(Object principal, String msg){
        super(HttpStatus.UNAUTHORIZED, msg);
        if (principal == null){
            this.principal = null;
        }
        else if (principal instanceof Serializable) {
            this.principal = (Serializable)principal;
        }
        else if (principal instanceof Principal){
            this.principal = ((Principal) principal).getName();
        }
        else{
            this.principal = principal.toString();
        }
    }
}
