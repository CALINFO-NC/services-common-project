package com.calinfo.api.common.spring;

import com.calinfo.api.common.dto.ChargementInfoDto;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;

/**
 * Created by dalexis on 21/05/2018.
 */
public class ChargementInfoPageRequestSorterTest {


    @Test
    public void call(){

        ChargementInfoDto ci = new ChargementInfoDto(1, 50);
        ChargementInfoPageRequest cipr = new ChargementInfoPageRequest(ci);

        Assert.assertNull(cipr.getSort());

        ArrayList<Sort.Order> lst = new ArrayList<>();
        lst.add(new Sort.Order(Sort.Direction.ASC, "prop"));

        Sort sort = new Sort(lst);
        cipr.setSort(sort);

        Assert.assertTrue(sort == cipr.getSort());
    }
}
