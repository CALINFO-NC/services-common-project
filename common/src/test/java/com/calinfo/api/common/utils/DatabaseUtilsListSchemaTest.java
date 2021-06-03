package com.calinfo.api.common.utils;

import com.calinfo.api.common.AutowiredConfig;
import com.calinfo.api.common.tenant.DomainDatasourceConfiguration;
import com.calinfo.api.common.tenant.GenericDatasourceConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@SpringBootTest(classes = {AutowiredConfig.class, GenericDatasourceConfiguration.class, DomainDatasourceConfiguration.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class DatabaseUtilsListSchemaTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private DataSource dataSource;

    @Test
    public void call(){

        DatabaseUtils.createSchema(dataSource, "schema1");
        DatabaseUtils.createSchema(dataSource, "schema2");

        List<String> schema = DatabaseUtils.listSchema(dataSource);
        Assert.assertNotNull(schema);
        schema = schema.stream().map(String::toUpperCase).collect(Collectors.toList());
        Assert.assertTrue(schema.contains("schema1".toUpperCase(Locale.ROOT)));
        Assert.assertTrue(schema.contains("schema2".toUpperCase(Locale.ROOT)));
    }
}
