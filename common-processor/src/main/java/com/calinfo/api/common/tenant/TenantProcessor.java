package com.calinfo.api.common.tenant;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.auto.service.AutoService;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.util.List;
import java.util.Set;

/**
 * Created by dalexis on 29/05/2018.
 */
@SupportedAnnotationTypes({"com.calinfo.api.common.tenant.TenantJpaRepositories"})
@AutoService(Processor.class)
public class TenantProcessor extends AbstractProcessor {

    private static final String BASE_ABSTRACT_CLASS = "Abstract";

    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        try {
            LogUtils.trace("Start tenant generation");

            YAMLFactory yamlFactory = new YAMLFactory();
            ObjectMapper mapper = new ObjectMapper(yamlFactory);

            String profil = System.getProperty("spring.profiles.active", "");
            if (!profil.equals("")){
                profil = String.format("-%s", profil);
            }

            LogUtils.trace(String.format("Profile : %s", profil));

            URL url = TenantProcessor.class.getResource(String.format("/application%s.yml", profil));
            JsonNode rootNode = mapper.readTree(url);


            for (Element item : roundEnv.getRootElements()) {

                if (!(item instanceof TypeElement)) {
                    continue;
                }

                TypeElement typeElement = (TypeElement) item;
                TenantJpaRepositories tenantJpaRepositories = typeElement.getAnnotation(TenantJpaRepositories.class);

                if (tenantJpaRepositories == null) {
                    continue;
                }

                if (!item.getSimpleName().toString().startsWith(BASE_ABSTRACT_CLASS)){
                    continue;
                }

                JsonNode nodeEnabled = rootNode.at("/common/configuration/tenant/enabled");

                if (nodeEnabled == null || !nodeEnabled.asBoolean()){
                    continue;
                }

                LogUtils.trace(tenantJpaRepositories.basePackagesConfJsonPath());
                JsonNode node = rootNode.at(tenantJpaRepositories.basePackagesConfJsonPath());

                String basePackage = extractBasePackage(mapper, node);
                writeFile(item.getSimpleName().toString().substring(BASE_ABSTRACT_CLASS.length()),
                        tenantJpaRepositories.entityManagerFactoryRef(),
                        tenantJpaRepositories.transactionManagerRef(),
                        basePackage);
            }

        }
        catch(Exception e){
            LogUtils.error(e);
            throw new ProcessorRuntimeException(e);
        }

        // Processeur suivant peuvent traiter les annotations
        return true;
    }

    private void writeFile(String fileName, String entityManagerFactoryRef, String transactionManagerRef, String basePackages) throws IOException {

        String pkg = "com.calinfo.api.common.tenant";
        String fullFileName = String.format("%s.%s", pkg, fileName);

        JavaFileObject jfo = this.processingEnv.getFiler().createSourceFile(fullFileName);
        LogUtils.trace(jfo.getName());

        try(Writer wJfo = jfo.openWriter(); PrintWriter writer = new PrintWriter(wJfo)){

            writer.println(String.format("package %s;", pkg));
            writer.println("");

            writer.println("import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;");
            writer.println("import org.springframework.context.annotation.Conditional;");
            writer.println("import org.springframework.context.annotation.Configuration;");
            writer.println("import org.springframework.data.jpa.repository.config.EnableJpaRepositories;");
            writer.println("import org.springframework.transaction.annotation.EnableTransactionManagement;");
            writer.println("");

            writer.println("@ConditionalOnProperty(\"common.configuration.tenant.enabled\")");
            writer.println("@Configuration");
            writer.println("@EnableTransactionManagement");
            writer.println("@EnableJpaRepositories(");
            writer.println(String.format("        entityManagerFactoryRef = \"%s\",", entityManagerFactoryRef));
            writer.println(String.format("        transactionManagerRef = \"%s\",", transactionManagerRef));
            writer.println(String.format("        basePackages = {%s}", basePackages));
            writer.println(")");
            writer.println(String.format("public class %s extends Abstract%s {", fileName, fileName));
            writer.println("}");
        }
    }

    private String extractBasePackage(ObjectMapper mapper, JsonNode node){

        JavaType type = mapper.getTypeFactory().constructParametricType(List.class, String.class);
        List<String> basePacjkageList = (List<String>)mapper.convertValue(node, type);

        StringBuilder basePackageBuilder = new StringBuilder();
        for (String basePackage : basePacjkageList){

            if (basePackageBuilder.length() != 0){
                basePackageBuilder.append(", ");
            }

            basePackageBuilder.append("\"");
            basePackageBuilder.append(basePackage);
            basePackageBuilder.append("\"");
        }

        return basePackageBuilder.toString();
    }
}
