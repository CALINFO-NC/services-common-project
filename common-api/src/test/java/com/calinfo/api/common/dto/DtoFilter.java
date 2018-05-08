package com.calinfo.api.common.dto;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoClassFilter;

/**
 * Created by dalexis on 08/05/2018.
 */
public class DtoFilter implements PojoClassFilter {

    public static final String PACKAGE_SCAN = "com.calinfo.api.common.dto";

    @Override
    public boolean include(PojoClass pojoClass) {
        return PACKAGE_SCAN.equals(pojoClass.getPackage().getName());
    }
}
