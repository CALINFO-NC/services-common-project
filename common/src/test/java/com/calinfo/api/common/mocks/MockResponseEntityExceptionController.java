package com.calinfo.api.common.mocks;

import com.calinfo.api.common.FieldErrorStructure;
import com.calinfo.api.common.MessageCodeValue;
import com.calinfo.api.common.MessageStructure;
import com.calinfo.api.common.ServiceErrorStructure;
import com.calinfo.api.common.ex.MessageException;
import com.calinfo.api.common.ex.MessageStatusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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

        ServiceErrorStructure serviceErrorStructure = new ServiceErrorStructure();


        List<MessageStructure> listErrorMessages = serviceErrorStructure.getGlobalErrors();
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


        FieldErrorStructure fieldError = serviceErrorStructure.getFieldsErrors();
        fieldError.put("prop", new MessageStructure(MessageCode.INVALID_CAPTCHA_VALUE), new MessageStructure(MessageCode.LOGIN_ALL_READY_EXIST));

        throw new MessageException(serviceErrorStructure);
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

    @GetMapping(value = "/launchMessageStatusForbidenException", produces = MediaType.APPLICATION_JSON_VALUE)
    public void launchMessageStatusForbidenException() throws Throwable {
        throw new MessageStatusException(HttpStatus.FORBIDDEN, "Message");
    }

    @GetMapping(value = "/launchAccessDeniedException", produces = MediaType.APPLICATION_JSON_VALUE)
    public void launchAccessDeniedException() throws Throwable {
        throw new AccessDeniedException("Message");
    }

    @PreAuthorize("hasRole('ROLE_ARBITRAIRE')")
    @GetMapping(value = "/launchAccessDeniedExceptionWithAnnotation", produces = MediaType.APPLICATION_JSON_VALUE)
    public void launchAccessDeniedExceptionWithAnnotation() throws Throwable {
    }
}
