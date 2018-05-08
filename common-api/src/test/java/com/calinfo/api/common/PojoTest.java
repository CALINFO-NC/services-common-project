package com.calinfo.api.common;

import com.calinfo.api.common.dto.DtoFilter;
import com.calinfo.api.common.resource.ResourceFilter;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.rule.impl.NoNestedClassRule;
import com.openpojo.validation.rule.impl.NoPublicFieldsExceptStaticFinalRule;
import com.openpojo.validation.rule.impl.NoStaticExceptFinalRule;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import org.junit.Test;

/**
 * Created by dalexis on 08/05/2018.
 */
public class PojoTest {

    @Test
    public void validatePojo() {
        Validator validator = ValidatorBuilder.create() //
                //.with(new StringTester()) //
                .with(new GetterTester()) // Test the getter and ensure it retrieves from the field being tested if and only if it has a getter defined.
                .with(new SetterTester()) // Test the setter and ensure it retrieves from the field being tested if and only if it has a getter defined.
                //.with(new BusinessIdentityTester()) // Test equals and hashcode
                .with(new GetterMustExistRule()) // This rule ensures that all Fields have a getter associated with them. Exception are fields defined static final since those are usually constants.
                .with(new NoNestedClassRule()) // This Rule checks for classes being "nested".
                .with(new NoStaticExceptFinalRule()) // This rule ensures that there are no static fields unless they are final.
                .with(new NoPublicFieldsExceptStaticFinalRule()) // ok for public fields only if they are static and final
                .build();

        validator.validateRecursively(ResourceFilter.PACKAGE_SCAN, new ResourceFilter());
        validator.validateRecursively(DtoFilter.PACKAGE_SCAN, new DtoFilter());

    }
}
