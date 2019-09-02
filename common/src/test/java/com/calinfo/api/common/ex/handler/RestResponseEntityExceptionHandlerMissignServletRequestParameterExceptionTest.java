package com.calinfo.api.common.ex.handler;

import com.calinfo.api.common.dto.AttributDto;
import com.calinfo.api.common.dto.BadRequestParameterDto;
import com.calinfo.api.common.dto.BadResponseDto;
import com.calinfo.api.common.mocks.MockDtoContrainteViolation;
import com.calinfo.api.common.mocks.MockDtoInerContrainteViolation;
import com.calinfo.api.common.mocks.MockMessageCode;
import com.calinfo.api.common.security.AbstractCommonPrincipal;
import com.calinfo.api.common.security.CommonPrincipal;
import com.calinfo.api.common.service.MessageService;
import com.calinfo.api.common.type.TypeAttribut;
import com.calinfo.api.common.utils.MiscUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
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
        BadRequestParameterDto valueTester = new BadRequestParameterDto();
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

        MockHttpServletRequestBuilder httpRequest = get("/mock/controller/launchMessageException");
        httpRequest.locale(Locale.FRANCE);

        // Construction du résultat attendue
        BadResponseDto valueTester = new BadResponseDto();
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
        BadRequestParameterDto valueTester = new BadRequestParameterDto();

        dto = new AttributDto();
        valueTester.getListErrorMessages().add(dto);
        dto.setName("prm.prop1");
        dto.setType(TypeAttribut.RESOURCE);
        dto.getListMessages().add("must not be null");

        dto = new AttributDto();
        valueTester.getListErrorMessages().add(dto);
        dto.setName("prm.prop2");
        dto.setType(TypeAttribut.RESOURCE);
        dto.getListMessages().add("must be null");

        dto = new AttributDto();
        valueTester.getListErrorMessages().add(dto);
        dto.setName("prm.prop3.prop1");
        dto.setType(TypeAttribut.RESOURCE);
        dto.getListMessages().add("must not be null");

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
        BadRequestParameterDto valueTester = new BadRequestParameterDto();
        AttributDto dto = new AttributDto();
        valueTester.getListErrorMessages().add(dto);
        dto.setName("mockDtoContrainteViolation.prop2");
        dto.setType(TypeAttribut.RESOURCE);
        dto.getListMessages().add("must be null");

        dto = new AttributDto();
        valueTester.getListErrorMessages().add(dto);
        dto.setName("mockDtoContrainteViolation.prop1");
        dto.setType(TypeAttribut.RESOURCE);
        dto.getListMessages().add("must not be null");

        dto = new AttributDto();
        valueTester.getListErrorMessages().add(dto);
        dto.setName("mockDtoContrainteViolation.prop3.prop1");
        dto.setType(TypeAttribut.RESOURCE);
        dto.getListMessages().add("must not be null");

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
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN));
    }



    @Test
    public void testLaunchMessageStatusForbidenException() throws Exception{

        MockHttpServletRequestBuilder httpRequest = get("/mock/controller/launchMessageStatusForbidenException");

        this.mockMvc.perform(httpRequest)
                .andExpect(status().isForbidden())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN));
    }

    @Test
    public void testLaunchNoSuchElementException() throws Exception{

        MockHttpServletRequestBuilder httpRequest = get("/mock/controller/launchNoSuchElementException");

        this.mockMvc.perform(httpRequest)
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN));
    }





    @Test
    public void testLaunchAccessDeniedException() throws Exception{

        MockHttpServletRequestBuilder httpRequest = get("/mock/controller/launchAccessDeniedException");

        this.mockMvc.perform(httpRequest)
                .andExpect(status().isForbidden())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN));


        // Mettre en place l'authentification
        AbstractCommonPrincipal principal = new CommonPrincipal(null, null, null, "toto", "", new ArrayList<>());
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        httpRequest = get("/mock/controller/launchAccessDeniedExceptionWithAnnotation");

        this.mockMvc.perform(httpRequest)
                .andExpect(status().isForbidden())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN));
    }
}
