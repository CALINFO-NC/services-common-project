package com.calinfo.api.common.teavm.js;

/*-
 * #%L
 * common-teavm
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

import org.teavm.jso.JSBody;

public class Console {

    @JSBody(params = { "message" }, script = "console.log(message)")
    public static native void log(String message);

    @JSBody(params = { "message" }, script = "console.error(message)")
    public static native void error(String message);

    @JSBody(params = { "message" }, script = "console.debug(message)")
    public static native void debug(String message);

    @JSBody(params = { "message" }, script = "console.info(message)")
    public static native void info(String message);

    @JSBody(params = { "message" }, script = "console.warn(message)")
    public static native void warn(String message);

    @JSBody(params = { "message" }, script = "console.trace(message)")
    public static native void trace(String message);
}
