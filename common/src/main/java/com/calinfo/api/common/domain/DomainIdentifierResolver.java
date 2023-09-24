package com.calinfo.api.common.domain;

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

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;



@ConditionalOnProperty(DomainProperties.CONDITIONNAL_PROPERTY)
@Component
public class DomainIdentifierResolver implements CurrentTenantIdentifierResolver {

    @Autowired
    private DomainProperties domainProperties;

    @Override
    public String resolveCurrentTenantIdentifier() {

        String domainName = DomainContext.getDomain();

        if (domainName != null){
            return DomainDatasourceConfiguration.getSchemaName(domainProperties.getPrefix(), domainName);
        }

        return domainProperties.getDefaultValue();
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
