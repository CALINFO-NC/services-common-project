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

    @Test
    public void callConvertSameObject(){

        MockConvertManager cm = new MockConvertManager();
        cm.postConstruct();

        MockClassConverter data = new MockClassConverter();
        MockClassConverter dataResult = cm.convert(data, data);

        Assert.assertTrue(data == dataResult);
    }

    @Test
    public void callConvertButNoConverterFound(){

        MockConvertManager cm = new MockConvertManager();
        cm.postConstruct();

        String src = "123";
        Integer dest = Integer.valueOf(123);

        // Cas d'une convertion par intance
        Assert.assertTrue(dest == cm.convert(src, dest));

        try {
            // Cas d'une convertion par typage
            cm.convert(src, Integer.class);
            Assert.fail();
        }
        catch(ConverterNotFoundException e){
            // OK : C'est normal d'avoir cet exception
        }

    }

    @Test
    public void callConvertButNoClassConverterFound(){

        MockConvertManager cm = new MockConvertManager();
        cm.setClassConverter(new MockClassConverter());
        cm.postConstruct();

        String src = "123";
        Integer dest = Integer.valueOf(123);

        try {
            // Pas de converteur de type instance trouvé
            cm.convert(src, dest);
            Assert.fail();
        }
        catch(ConverterNotFoundException e){
            // OK : C'est normal d'avoir cet exception
        }


        cm = new MockConvertManager();
        cm.setInstanceConverter(new MockInstanceConverter());
        cm.postConstruct();

        src = "123";
        Class<MockClassConverter> destMockClass = MockClassConverter.class;

        // Pas de converteur de type Instance trouvé
        cm.convert(src, destMockClass);

        Assert.assertEquals(src, cm.getInstanceConverter().getSourceConvert());
        Assert.assertEquals(destMockClass, cm.getInstanceConverter().getDestConvert().getClass());

    }
}
