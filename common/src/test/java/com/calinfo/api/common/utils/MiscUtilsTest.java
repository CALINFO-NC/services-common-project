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

    @Test
    public void testFormatEndUrl() throws Exception{
        Assert.assertEquals(MiscUtils.formatEndUrl("http://abc/"), "http://abc");
        Assert.assertEquals(MiscUtils.formatEndUrl("http://abc"), "http://abc");
    }

}
