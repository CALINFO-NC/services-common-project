package com.calinfo.api.common.swagger.mock;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

@Component
@ConditionalOnClass({ApiInfo.class})
@EnableSwagger2
public class Config {

    @Bean
    public Docket swaggerDocket() {

        // Ajout des informations du contact
        Contact contact = new Contact("CALINFO", null, "calinfo@calinfo-nc.com");

        // Ajout des informations de l'API
        ApiInfo apiInfo = new ApiInfo("DOMAIN", "Voir aussi la documentation suivante : https://calinfo.atlassian.net/wiki/spaces/SER/pages/164954113/Connexion+par+REST+API", "v1", null, contact, null, null, new ArrayList<>());

        Docket result = new Docket(DocumentationType.SWAGGER_2)
                .groupName("v1")
                .apiInfo(apiInfo).select()
                .paths(PathSelectors.regex("\\/api\\/.*"))
                .build()
                .useDefaultResponseMessages(false);

        return result;
    }
}