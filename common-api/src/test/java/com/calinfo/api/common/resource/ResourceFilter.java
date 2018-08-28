package com.calinfo.api.common.resource;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoClassFilter;

/**
 * Created by dalexis on 08/05/2018.
 */
public class ResourceFilter implements PojoClassFilter {

    public static final String PACKAGE_SCAN = "com.calinfo.api.common.resource";

    @Override
    public boolean include(PojoClass pojoClass) {
        return PACKAGE_SCAN.equals(pojoClass.getPackage().getName())  && !pojoClass.getName().endsWith("Test") && !pojoClass.isNestedClass();
    }
}
