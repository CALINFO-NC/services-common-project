package com.calinfo.api.common.utils;


import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
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
     * Sido s'appuie sur cette méthode plutôt que de passer par ZonedDateTime.now() en direct, pour permettre de simuler facilement dans le futur une nouvelle date/heure courante.
     *
     * @return Date et heure du jour, dans la timezone de Nouméa
     */
    public static ZonedDateTime now() {

        ZonedDateTime dateOverride = getOverrideDateSystem();

        if (dateOverride != null) {
            return dateOverride;
        }

        return ZonedDateTime.now();
    }

    /**
     * @return Retourne la date du jour
     */
    public static LocalDate today() {

        ZonedDateTime dateOverride = getOverrideDateSystem();

        if (dateOverride != null) {
            return dateOverride.toLocalDate();
        }

        return LocalDate.now();
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

