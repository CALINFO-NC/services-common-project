package com.calinfo.api.common.teavm.processors;

import com.calinfo.api.common.teavm.processor.JsProcessor;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.tools.JavaFileObject;
import java.io.File;
import java.io.IOException;

/**
 * Created by dalexis on 11/05/2018.
 */
public class AllProcessorTest {

    @Test
    public void callProcessor() throws IOException{

        System.setProperty("calinfo.common.teavm.log.level", "trace");

        JsProcessor processor = new JsProcessor();
        File source = new File("src/test/resources/processor/JsClassTest.java");


        JavaFileObject[] files = {JavaFileObjects.forResource(source.toURI().toURL())};
        Compilation compilation = Compiler.javac()
                .withProcessors(processor)
                .compile(files);

        compilation.errors().stream().forEach(item -> {
            System.out.println(item.toString());
        });

        Assert.assertEquals(0, compilation.errors().size());

    }

}

