package com.calinfo.api.common.spring;

import com.calinfo.api.common.dto.ChargementInfoDto;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by dalexis on 21/05/2018.
 */
public class ChargementInfoPageRequestPageSizeAndOffsetTest {


    @Test
    public void call(){

        ChargementInfoDto ci = new ChargementInfoDto(1, 50);
        ChargementInfoPageRequest cipr = new ChargementInfoPageRequest(ci);

        Assert.assertEquals(ci.getStart(), cipr.getOffset());
        Assert.assertEquals(ci.getLimit().intValue(), cipr.getPageSize());

        ChargementInfoPageRequest.setMaxLimit(50);
        ci = new ChargementInfoDto(1, ChargementInfoPageRequest.getMaxLimit() + 1);
        cipr = new ChargementInfoPageRequest(ci);

        Assert.assertEquals(ChargementInfoPageRequest.getMaxLimit(), cipr.getPageSize());
        Assert.assertNull(cipr.getSort());
    }
}
