package com.calinfo.api.common.tenant;

import com.google.common.truth.Truth;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created by dalexis on 11/05/2018.
 */
public class AllProcessorTest {

    @Test
    public void callCompileWithFileModelNotExist() throws IOException, MalformedURLException{

        compileAndVerify();


    }

    public void compileAndVerify() throws MalformedURLException{


        TenantProcessor processor = new TenantProcessor();
        File source = new File("src/test/java/com/calinfo/api/common/tenant/AbstractTenantDatasourceConfiguration.java");

        Truth.assertAbout(JavaSourceSubjectFactory.javaSource())
                .that(JavaFileObjects.forResource(source.toURI().toURL()))
                .processedWith(processor)
                .compilesWithoutError();

        // On vérifie que les fichiers ont été écrit
        /*for(Map.Entry<String, ModuleConf> entry : configuration.getModules().entrySet()){
            String path = entry.getValue().getXdsPath();
            File pathMetadata = new File(String.join(File.separator, path, "metadata", "model"));
            File file = new File(String.join(File.separator, pathMetadata.getAbsolutePath(), "pkg.MyModel"));
            Assert.assertTrue(String.format("%s not exist", file.getAbsolutePath()), file.exists());
            Assert.assertTrue(pathMetadata.listFiles().length == 1);
        }*/
    }
}

