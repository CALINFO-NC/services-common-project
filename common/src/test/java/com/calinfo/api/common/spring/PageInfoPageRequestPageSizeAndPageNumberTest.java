package com.calinfo.api.common.spring;

import com.calinfo.api.common.dto.PageInfoDto;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by dalexis on 21/05/2018.
 */
public class PageInfoPageRequestPageSizeAndPageNumberTest {


    @Test
    public void call(){

        PageInfoDto ci = new PageInfoDto(1, 50);
        PageInfoPageRequest cipr = new PageInfoPageRequest(ci);

        Assert.assertEquals(ci.getPage() , cipr.getPageNumber());
        Assert.assertEquals(ci.getLimit().intValue(), cipr.getPageSize());

        PageInfoPageRequest.setMaxLimit(50);
        ci = new PageInfoDto(1, PageInfoPageRequest.getMaxLimit() + 1);
        cipr = new PageInfoPageRequest(ci);

        Assert.assertEquals(PageInfoPageRequest.getMaxLimit(), cipr.getPageSize());
    }
}
