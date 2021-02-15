package com.calinfo.api.common.utils;

/*-
 * #%L
 * common
 * %%
 * Copyright (C) 2019 - 2021 CALINFO
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


import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


/**
 * Classe utilitaire sur les dates
 *
 * @author David ALEXIS / Pascal Le Léannec
 */
public final class DateUtils {


    public static final String SYSTEM_PROPERTIE_DATE_SYSTEM = "com.calinfo.api.common.date";


    /**
     * Constructeur privé conseillé par Sonar
     */
    private DateUtils() {
    }

    /**
     * L'utilisation de cette méthode permet d'overrider la date système
     *
     * @return Date et heure du jour, dans la timezone de Nouméa
     */
    public static ZonedDateTime now() {
        return now(ZoneId.systemDefault());
    }

    /**
     * L'utilisation de cette méthode permet d'overrider la date système
     *
     * @param  zoneId Zone horaire
     * @return Date et heure du jour, dans la timezone de Nouméa
     */
    public static ZonedDateTime now(ZoneId zoneId) {

        ZonedDateTime dateOverride = getOverrideDateSystem();

        if (dateOverride != null) {
            return dateOverride;
        }

        return ZonedDateTime.now(zoneId);
    }

    /**
     * L'utilisation de cette méthode permet d'overrider la date système
     *
     * @return Retourne la date du jour
     */
    public static LocalDate today() {
        return now(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * L'utilisation de cette méthode permet d'overrider la date système
     *
     * @param  zoneId Zone horaire
     * @return Retourne la date du jour
     */
    public static LocalDate today(ZoneId zoneId) {
        return now(zoneId).toLocalDate();
    }

    /**
     * @return Retourne la date système overrider par la propriété système "nc.opt.sido.common.date"
     */
    private static ZonedDateTime getOverrideDateSystem() {

        String strDateOverride = System.getProperty(SYSTEM_PROPERTIE_DATE_SYSTEM);

        if (StringUtils.isBlank(strDateOverride)) {
            return null;
        }

        return ZonedDateTime.parse(strDateOverride, DateTimeFormatter.ISO_ZONED_DATE_TIME);

    }


}

