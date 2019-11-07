package com.calinfo.api.common.utils;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by dalexis on 23/11/2017.
 */
public class MiscUtilsTest {


    @Test
    public void testGetActualMethodName() throws Exception{
        Assert.assertEquals(MiscUtils.getActualMethodName(), "testGetActualMethodName");
    }

}
