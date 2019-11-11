package com.calinfo.api.common.mocks;

import com.calinfo.api.common.manager.ApiKeyManager;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by dalexis on 20/06/2018.
 */
public class MockApiKeyManager implements ApiKeyManager{

    @Getter
    @Setter
    private String newToken = null;

    @Override
    public String refreshToken(String apiKey) {
        return getNewToken();
    }
}
