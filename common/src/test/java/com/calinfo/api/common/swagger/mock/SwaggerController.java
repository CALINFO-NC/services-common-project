package com.calinfo.api.common.swagger.mock;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by dalexis on 06/01/2018.
 */
@RestController
@RequestMapping
@Deprecated(since = "1.2.0", forRemoval = true)
public class SwaggerController {


    @GetMapping(value = "/api/v1/swagger/mock")
    public SwaggerResoruce mockSecurityPrivateUrl(SwaggerResoruce resource){
        return null;
    }
}
