package com.calinfo.api.common.utils;

import com.calinfo.api.common.AutowiredConfig;

import com.calinfo.api.common.tenant.DomainDatasourceConfiguration;
import com.calinfo.api.common.tenant.GenericDatasourceConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.sql.DataSource;


@SpringBootTest(classes = {AutowiredConfig.class, GenericDatasourceConfiguration.class, DomainDatasourceConfiguration.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class LiquibaseUtilsUpdateSchemaTest extends AbstractTestNGSpringContextTests {

    @Value("${spring.liquibase.change-log}")
    private String changelogFileConf;

    // Bug IntelliJ qui ne support pas l'injection de la source de donnée
    // https://intellij-support.jetbrains.com/hc/en-us/community/posts/207338055-Autowiring-for-Bean-Class-inspection-in-Spring-Boot-project
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private DataSource dataSource;

    @Test
    public void call() throws Exception {

        // Vérifier que l'on peut mettre à jour le schéma public
        LiquibaseUtils.updateSchema(dataSource, changelogFileConf, "public");

        try{
            // Vérifer que l'on ne peut pas mettre à jour une autre table
            LiquibaseUtils.updateSchema(dataSource, changelogFileConf, "erefdsvdf");
            Assert.fail();
        }
        catch(Exception e){
            // RAF on cherche à lever cette exception
        }
    }


}
