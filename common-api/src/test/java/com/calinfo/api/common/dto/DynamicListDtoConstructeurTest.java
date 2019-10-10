package com.calinfo.api.common.dto;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * Created by dalexis on 21/05/2018.
 */
public class DynamicListDtoConstructeurTest {


    @Test
    public void callPage(){

        List<Integer> lst = Arrays.asList(3, 4);

        PageImpl<Integer> page = new PageImpl<>(lst, PageRequest.of(3, 2), 200);

        DynamicListDto<SimpleDto<String>> dyn = new DynamicListDto(new Function<Integer, SimpleDto<String>>() {

            @Override
            public SimpleDto<String> apply(Integer val) {
                return new SimpleDto<String>(val.toString());
            }
        }, page);

        Assert.assertTrue(dyn.getCount() == 200);
        Assert.assertTrue(dyn.getData().size() == 2);
        Assert.assertTrue(dyn.getData().stream().anyMatch(d -> d.getValue().equals("3")));
        Assert.assertTrue(dyn.getData().stream().anyMatch(d -> d.getValue().equals("4")));
    }

    @Test
    public void callGlobalList(){

        List<Integer> lst = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        DynamicListDto<Integer> dyn = new DynamicListDto(lst, 0, 3);

        Assert.assertEquals(10, dyn.getCount());
        Assert.assertEquals(3, dyn.getData().size());
        Assert.assertTrue(dyn.getData().contains(1));
        Assert.assertTrue(dyn.getData().contains(2));
        Assert.assertTrue(dyn.getData().contains(3));

        dyn = new DynamicListDto(lst, 1, 3);

        Assert.assertEquals(10, dyn.getCount());
        Assert.assertEquals(3, dyn.getData().size());
        Assert.assertTrue(dyn.getData().contains(4));
        Assert.assertTrue(dyn.getData().contains(5));
        Assert.assertTrue(dyn.getData().contains(6));

        dyn = new DynamicListDto(lst, 4, 2);

        Assert.assertEquals(10, dyn.getCount());
        Assert.assertEquals(2, dyn.getData().size());
        Assert.assertTrue(dyn.getData().contains(9));
        Assert.assertTrue(dyn.getData().contains(10));

        dyn = new DynamicListDto(lst, 3, 3);

        Assert.assertEquals(10, dyn.getCount());
        Assert.assertEquals(1, dyn.getData().size());
        Assert.assertTrue(dyn.getData().contains(10));
    }
}
