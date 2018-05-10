package com.calinfo.api.common.utils;


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

