package com.calinfo.api.common.dto;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * Created by dalexis on 21/05/2018.
 */
public class DynamicListDtoConstructeurTest {


    @Test
    public void call(){

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
}
