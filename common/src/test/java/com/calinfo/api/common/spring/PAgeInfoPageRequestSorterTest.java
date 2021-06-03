package com.calinfo.api.common.spring;

import com.calinfo.api.common.dto.PageInfoDto;
import org.springframework.data.domain.Sort;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;

/**
 * Created by dalexis on 21/05/2018.
 */
public class PAgeInfoPageRequestSorterTest {


    @Test
    public void call(){

        PageInfoDto ci = new PageInfoDto(1, 50);

        ArrayList<Sort.Order> lst = new ArrayList<>();
        lst.add(new Sort.Order(Sort.Direction.ASC, "prop"));

        Sort sort = Sort.by(lst);
        PageInfoPageRequest cipr = new PageInfoPageRequest(ci, sort);

        Assert.assertTrue(sort == cipr.getSort());
    }
}
