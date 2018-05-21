package com.calinfo.api.common.mocks;

import com.calinfo.api.common.converter.AbstractConvertManager;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by dalexis on 21/05/2018.
 */
public class MockConvertManager extends AbstractConvertManager {

    @Setter
    @Getter
    private MockInstanceConverter instanceConverter = null;

    @Setter
    @Getter
    private MockClassConverter classConverter = null;

    @Override
    public void postConstruct() {
        if (getClassConverter() != null)
            addConverter(getClassConverter());

        if (getInstanceConverter() != null)
            addConverter(getInstanceConverter());
    }
}
