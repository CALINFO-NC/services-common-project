package com.calinfo.api.common.mocks;

import com.calinfo.api.common.FieldError;
import com.calinfo.api.common.MessageCode;
import com.calinfo.api.common.MessageCodeValue;
import com.calinfo.api.common.MessageStructure;
import com.calinfo.api.common.ex.MessageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dalexis on 20/11/2017.
 */
@RequestMapping("/mock/controller")
@RestController
public class MockResponseEntityExceptionController {

    @Autowired
    private MockServiceConstrainteVilationService mockServiceConstrainteVilationService;

    @GetMapping(value = "/sendResponseInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    public MockResponseResource sendResponseInfo() {
        MockResponseResource result = new MockResponseResource();
        result.getListInfoMessages().add(MockResponseResource.MESSAGE_INFO_TEST);

        return result;
    }

    @GetMapping(value = "/sendResponseWarning", produces = MediaType.APPLICATION_JSON_VALUE)
    public MockResponseResource sendResponseWarning() {
        MockResponseResource result = new MockResponseResource();
        result.getListWarningMessages().add(MockResponseResource.MESSAGE_WARNING_TEST);

        return result;
    }

    @GetMapping(value = "/launchMissingServletRequestParameterException", produces = MediaType.APPLICATION_JSON_VALUE)
    public void launchMissingServletRequestParameterException() throws MissingServletRequestParameterException {
        MissingServletRequestParameterException ex = new MissingServletRequestParameterException("prmName", "prmType");
        throw ex;
    }

    @GetMapping(value = "/launchMessageException", produces = MediaType.APPLICATION_JSON_VALUE)
    public void launchMessageException() {

        List<MessageStructure> listErrorMessages = new ArrayList<>();
        listErrorMessages.add(new MessageStructure(new MessageCodeValue() {
            @Override
            public String name() {
                return "Message1";
            }
        }));
        listErrorMessages.add(new MessageStructure(new MessageCodeValue() {
            @Override
            public String name() {
                return "Message2";
            }
        }));


        FieldError fieldError = new FieldError();
        fieldError.put("prop", new MessageStructure(MessageCode.INVALID_CAPTCHA_VALUE), new MessageStructure(MessageCode.LOGIN_ALL_READY_EXIST));

        throw new MessageException(listErrorMessages, fieldError);
    }

    @GetMapping(value = "/launchConstraintViolationException", produces = MediaType.APPLICATION_JSON_VALUE)
    public void launchConstraintViolationException() {

        MockDtoContrainteViolation dto = new MockDtoContrainteViolation();
        dto.setProp2("AZERTY");
        dto.setProp3(new MockDtoInerContrainteViolation());

        mockServiceConstrainteVilationService.validation(dto);
    }

    @PostMapping(value = "/launchConstraintViolationException", produces = MediaType.APPLICATION_JSON_VALUE)
    public void launchConstraintViolationExceptionPost(@Valid @RequestBody MockDtoContrainteViolation dto) {
    }

    @GetMapping(value = "/launchThrowable", produces = MediaType.APPLICATION_JSON_VALUE)
    public void launchThrowable() throws Throwable {
        throw new Throwable("Message");
    }
}
