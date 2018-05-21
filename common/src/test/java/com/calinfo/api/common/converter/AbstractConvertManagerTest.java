package com.calinfo.api.common.converter;

import com.calinfo.api.common.mocks.MockClassConverter;
import com.calinfo.api.common.mocks.MockConvertManager;
import com.calinfo.api.common.mocks.MockInstanceConverter;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by dalexis on 21/05/2018.
 */
public class AbstractConvertManagerTest {

    @Test
    public void callConvertInstance(){

        MockConvertManager cm = new MockConvertManager();
        cm.setInstanceConverter(new MockInstanceConverter());
        cm.postConstruct();

        String src = "123";
        Integer dest = Integer.valueOf(123);
        cm.convert(src, dest);

        Assert.assertEquals(src.getClass(), cm.getInstanceConverter().getSourceClass());
        Assert.assertEquals(dest.getClass(), cm.getInstanceConverter().getDestClass());
        Assert.assertEquals(src, cm.getInstanceConverter().getSourceConvert());
        Assert.assertEquals(dest, cm.getInstanceConverter().getDestConvert());
    }

    @Test
    public void callConvertClass(){

        MockConvertManager cm = new MockConvertManager();
        cm.setClassConverter(new MockClassConverter());
        cm.postConstruct();

        String src = "123";
        Class<Integer> dest = Integer.class;
        cm.convert(src, dest);

        Assert.assertEquals(src.getClass(), cm.getClassConverter().getSourceClass());
        Assert.assertEquals(dest.getClass(), cm.getClassConverter().getDestClass());
        Assert.assertEquals(src, cm.getClassConverter().getSourceConvert());
        Assert.assertEquals(dest, cm.getClassConverter().getDestConvert());
    }
}
