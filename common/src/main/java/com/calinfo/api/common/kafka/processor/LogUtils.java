package com.calinfo.api.common.kafka.processor;

/*-
 * #%L
 * common
 * %%
 * Copyright (C) 2019 - 2022 CALINFO
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
