package com.calinfo.api.common.utils;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by dalexis on 11/05/2018.
 */
public class DateUtilsNowTest {

    @Test
    public void call(){

        // Vérifier que la date retournée n'est pas null
        ZonedDateTime now = DateUtils.now();
        Assert.assertNotNull(now);

        // Tester l'override de la date Systèem
        String strDate = "2005-04-01T13:38:09-08:00";
        System.setProperty(DateUtils.SYSTEM_PROPERTIE_DATE_SYSTEM, strDate);
        now = DateUtils.now();
        System.clearProperty(DateUtils.SYSTEM_PROPERTIE_DATE_SYSTEM);
        ZonedDateTime dateToTest = ZonedDateTime.parse(strDate, DateTimeFormatter.ISO_ZONED_DATE_TIME);
        Assert.assertEquals(dateToTest, now);
    }
}
