package com.calinfo.api.common.handler;

import com.calinfo.api.common.dto.AttributDto;
import com.calinfo.api.common.mocks.MockDtoInerContrainteViolation;
import com.calinfo.api.common.resource.BadRequestParameterResource;
import com.calinfo.api.common.utils.MiscUtils;
import com.calinfo.api.common.mocks.MockDtoContrainteViolation;
import com.calinfo.api.common.mocks.MockMessageCode;
import com.calinfo.api.common.resource.BadResponseResource;
import com.calinfo.api.common.service.MessageService;
import com.calinfo.api.common.type.TypeAttribut;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RestResponseEntityExceptionHandlerMissignServletRequestParameterExceptionTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MessageService messageService;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = webAppContextSetup(this.context).build();
    }

    @After
    public  void downUp(){
        this.mockMvc = null;
    }

    @Test
    public void testLaunchMissingServletRequestParameterException() throws Exception{


        MockHttpServletRequestBuilder httpRequest = get("/mock/controller/launchMissingServletRequestParameterException");


        // Construction du résultat attendue
        BadRequestParameterResource valueTester = new BadRequestParameterResource();
        AttributDto dto = new AttributDto();
        valueTester.getListErrorMessages().add(dto);
        dto.setName("prmName");
        dto.setType(TypeAttribut.REQUEST);
        dto.getListMessages().add("Required prmType parameter 'prmName' is not present");

        ObjectMapper objectMapper = MiscUtils.getObjectMapper();

        this.mockMvc.perform(httpRequest)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(valueTester)));
    }

    @Test
    public void testLaunchMessageException() throws Exception{

        MockHttpServletRequestBuilder httpRequest = get("/mock/controller/launchMessageException")
                .header("language", "fr");

        // Construction du résultat attendue
        BadResponseResource valueTester = new BadResponseResource();
        List<String> lstMsg = new ArrayList<>();
        lstMsg.add(messageService.translate(Locale.FRANCE, MockMessageCode.INVALID_CAPTCHA_VALUE));
        lstMsg.add(messageService.translate(Locale.FRANCE, MockMessageCode.LOGIN_ALL_READY_EXIST));
        valueTester.getMapErrorMessagesFields().put("prop", lstMsg);
        valueTester.getListErrorMessages().add("fr_Message1");
        valueTester.getListErrorMessages().add("fr_Message2");

        ObjectMapper objectMapper = MiscUtils.getObjectMapper();

        this.mockMvc.perform(httpRequest)
                .andExpect(status().isNotImplemented())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(valueTester)));
    }

    
    @Test
    public void testLaunchConstraintViolationException() throws Exception{

        MockHttpServletRequestBuilder httpRequest = get("/mock/controller/launchConstraintViolationException");

        // Construction du résultat attendue
        AttributDto dto;
        BadRequestParameterResource valueTester = new BadRequestParameterResource();

        dto = new AttributDto();
        valueTester.getListErrorMessages().add(dto);
        dto.setName("prm.prop1");
        dto.setType(TypeAttribut.RESOURCE);
        dto.getListMessages().add("may not be null");

        dto = new AttributDto();
        valueTester.getListErrorMessages().add(dto);
        dto.setName("prm.prop2");
        dto.setType(TypeAttribut.RESOURCE);
        dto.getListMessages().add("must be null");

        dto = new AttributDto();
        valueTester.getListErrorMessages().add(dto);
        dto.setName("prm.prop3.prop1");
        dto.setType(TypeAttribut.RESOURCE);
        dto.getListMessages().add("may not be null");

        ObjectMapper objectMapper = MiscUtils.getObjectMapper();

        this.mockMvc.perform(httpRequest)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(valueTester)));


    }

    @Test
    public void testLaunchConstraintViolationExceptionPost() throws Exception{

        ObjectMapper objectMapper = MiscUtils.getObjectMapper();

        MockDtoContrainteViolation dtoConstrainte = new MockDtoContrainteViolation();
        dtoConstrainte.setProp2("AZERTY");
        dtoConstrainte.setProp3(new MockDtoInerContrainteViolation());

        MockHttpServletRequestBuilder httpRequest = post("/mock/controller/launchConstraintViolationException")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoConstrainte));

        // Construction du résultat attendue
        BadRequestParameterResource valueTester = new BadRequestParameterResource();
        AttributDto dto = new AttributDto();
        valueTester.getListErrorMessages().add(dto);
        dto.setName("mockDtoContrainteViolation.prop1");
        dto.setType(TypeAttribut.RESOURCE);
        dto.getListMessages().add("may not be null");

        dto = new AttributDto();
        valueTester.getListErrorMessages().add(dto);
        dto.setName("mockDtoContrainteViolation.prop2");
        dto.setType(TypeAttribut.RESOURCE);
        dto.getListMessages().add("must be null");

        dto = new AttributDto();
        valueTester.getListErrorMessages().add(dto);
        dto.setName("mockDtoContrainteViolation.prop3.prop1");
        dto.setType(TypeAttribut.RESOURCE);
        dto.getListMessages().add("may not be null");

        this.mockMvc.perform(httpRequest)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(valueTester)));
    }

    @Test
    public void testLaunchThrowable() throws Exception{

        MockHttpServletRequestBuilder httpRequest = get("/mock/controller/launchThrowable");

        this.mockMvc.perform(httpRequest)
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
}
