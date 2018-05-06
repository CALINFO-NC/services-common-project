package com.calinfo.api.common.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Méthodes utilitaires sur les exceptions
 *
 * @author David ALEXIS
 */
public final class ExceptionUtils {

    /**
     * Permet déviter que l'on puisse instancier une classe utilitaire
     */
    private ExceptionUtils(){
    }


    /**
     * Retourne la stacktrace d'une exception au format String
     *
     * @param e Exception
     * @return Stack trace
     */
    public static String getPrintValue(Throwable e) {

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);

        return sw.toString();
    }

}

