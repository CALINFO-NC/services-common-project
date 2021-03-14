package com.calinfo.api.common.spring;

import com.calinfo.api.common.dto.ChargementInfoDto;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by dalexis on 21/05/2018.
 */
public class ChargementInfoPageRequestPageSizeAndPageNumberTest {


    @Test
    public void call(){

        ChargementInfoDto ci = new ChargementInfoDto(1, 50);
        ChargementInfoPageRequest cipr = new ChargementInfoPageRequest(ci);

        Assert.assertEquals(ci.getPage() , cipr.getPageNumber());
        Assert.assertEquals(ci.getLimit().intValue(), cipr.getPageSize());

        ChargementInfoPageRequest.setMaxLimit(50);
        ci = new ChargementInfoDto(1, ChargementInfoPageRequest.getMaxLimit() + 1);
        cipr = new ChargementInfoPageRequest(ci);

        Assert.assertEquals(ChargementInfoPageRequest.getMaxLimit(), cipr.getPageSize());
    }
}
