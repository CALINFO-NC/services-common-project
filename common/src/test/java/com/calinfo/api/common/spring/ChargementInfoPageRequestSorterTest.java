package com.calinfo.api.common.spring;

import com.calinfo.api.common.dto.ChargementInfoDto;
import org.springframework.data.domain.Sort;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;

/**
 * Created by dalexis on 21/05/2018.
 */
@Deprecated
public class ChargementInfoPageRequestSorterTest {


    @Test
    public void call(){

        ChargementInfoDto ci = new ChargementInfoDto(1, 50);

        ArrayList<Sort.Order> lst = new ArrayList<>();
        lst.add(new Sort.Order(Sort.Direction.ASC, "prop"));

        Sort sort = Sort.by(lst);
        ChargementInfoPageRequest cipr = new ChargementInfoPageRequest(ci, sort);

        Assert.assertTrue(sort == cipr.getSort());
    }
}
