package com.calinfo.api.common.service;

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

import com.calinfo.api.common.MessageCodeValue;

import java.io.Serializable;
import java.util.Locale;

/**
 * Created by dalexis on 20/11/2017.
 */

public interface MessageService {


    String translate(Locale locale, MessageCodeValue codeMessage, Serializable... params);

    default String translate(MessageCodeValue codeMessage, Serializable... params){
        return translate(Locale.getDefault(), codeMessage, params);
    }
}
