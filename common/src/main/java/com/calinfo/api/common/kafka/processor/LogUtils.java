package com.calinfo.api.common.kafka.processor;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by dalexis on 19/03/2018.
 */
public class LogUtils {

    private LogUtils(){

    }

    public static void error(Exception e){
        System.err.println(String.format("[ERROR] : %s", getPrintValue(e)));
    }

    public static void warn(String msg){
        System.err.println(String.format("[WARN] : %s", msg));
    }

    public static void trace(String msg){
        System.out.println(String.format("CALINFO KAFKATOPIC ANNOTATIONS : %s", msg));
    }

    /**
     * Retourne la stacktrace d'une exception au format String
     *
     * @param e Exception
     * @return Stack trace
     */
    private static String getPrintValue(Throwable e) {

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);

        return sw.toString();
    }
}
