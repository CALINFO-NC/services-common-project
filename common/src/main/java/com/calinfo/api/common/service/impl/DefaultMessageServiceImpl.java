package com.calinfo.api.common.service.impl;

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
import com.calinfo.api.common.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Locale;

/**
 * Utilise le fichier messages.properties, pour charger toutes les clés de message existantes
 * puis délègue à spring le soin de charger le bon message selon la locale précisée.
 */

@ConditionalOnProperty(prefix = "common.configuration.service.message", name = "enabled", havingValue = "true", matchIfMissing = true)
@Service
@Slf4j
@RequiredArgsConstructor
class DefaultMessageServiceImpl implements MessageService {


    private final MessageSource messageSource;

    @Override
    public String translate(Locale locale, MessageCodeValue codeMessage, Serializable... params) {
        try {
            return this.messageSource.getMessage(codeMessage.name(), params, locale);
        } catch (NoSuchMessageException e) {
            log.warn(e.getMessage());
            log.debug(e.getMessage(), e);
            return codeMessage.name();
        }
    }
}
