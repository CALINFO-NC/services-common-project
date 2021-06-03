package com.calinfo.api.common.spring;

import com.calinfo.api.common.dto.PageInfoDto;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by dalexis on 21/05/2018.
 */
public class PageInfoPageRequestNextPreviousFirstTest {


    @Test
    public void call(){

        PageInfoDto ci = new PageInfoDto(0, 50);
        PageInfoPageRequest cipr = new PageInfoPageRequest(ci);
        Assert.assertFalse(cipr.hasPrevious());

        PageInfoPageRequest ciprNext = (PageInfoPageRequest) cipr.next();
        Assert.assertTrue(ciprNext.hasPrevious());

        Assert.assertTrue(ciprNext.getOffset() == cipr.getOffset() + cipr.getPageSize());
        Assert.assertTrue(ciprNext.getPageSize() == cipr.getPageSize());

        PageInfoPageRequest ciprPrev = (PageInfoPageRequest) ciprNext.previousOrFirst();
        Assert.assertFalse(ciprPrev.hasPrevious());

        Assert.assertTrue(ciprPrev.getOffset() == cipr.getOffset());
        Assert.assertTrue(ciprPrev.getPageSize() == cipr.getPageSize());

        PageInfoPageRequest ciprFirst = (PageInfoPageRequest) ciprNext.first();
        Assert.assertFalse(ciprFirst.hasPrevious());

        Assert.assertTrue(ciprFirst.getOffset() == cipr.getOffset());
        Assert.assertTrue(ciprFirst.getPageSize() == cipr.getPageSize());
    }
}
