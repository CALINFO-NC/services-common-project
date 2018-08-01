package com.calinfo.api.common.spring;

import com.calinfo.api.common.dto.ChargementInfoDto;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by dalexis on 21/05/2018.
 */
public class ChargementInfoPageRequestNextPreviousFirstTest {


    @Test
    public void call(){

        ChargementInfoDto ci = new ChargementInfoDto(0, 50);
        ChargementInfoPageRequest cipr = new ChargementInfoPageRequest(ci);
        Assert.assertFalse(cipr.hasPrevious());

        ChargementInfoPageRequest ciprNext = (ChargementInfoPageRequest) cipr.next();
        Assert.assertTrue(ciprNext.hasPrevious());

        Assert.assertTrue(ciprNext.getOffset() == cipr.getOffset() + cipr.getPageSize());
        Assert.assertTrue(ciprNext.getPageSize() == cipr.getPageSize());

        ChargementInfoPageRequest ciprPrev = (ChargementInfoPageRequest) ciprNext.previousOrFirst();
        Assert.assertFalse(ciprPrev.hasPrevious());

        Assert.assertTrue(ciprPrev.getOffset() == cipr.getOffset());
        Assert.assertTrue(ciprPrev.getPageSize() == cipr.getPageSize());

        ChargementInfoPageRequest ciprFirst = (ChargementInfoPageRequest) ciprNext.first();
        Assert.assertFalse(ciprFirst.hasPrevious());

        Assert.assertTrue(ciprFirst.getOffset() == cipr.getOffset());
        Assert.assertTrue(ciprFirst.getPageSize() == cipr.getPageSize());
    }
}
