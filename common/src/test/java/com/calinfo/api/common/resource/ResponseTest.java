package com.calinfo.api.common.resource;

import com.calinfo.api.common.mocks.MockResponseResource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ResponseTest {

    @Autowired
    private WebApplicationContext context;

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
    public void testResponseInfo() throws Exception{

        MockHttpServletRequestBuilder httpRequest = get("/mock/controller/sendResponseInfo");

        this.mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("listInfoMessages").isArray())
                .andExpect(jsonPath("listWarningMessages").isArray())
                .andExpect(jsonPath("listInfoMessages", hasSize(1)))
                .andExpect(jsonPath("listWarningMessages", hasSize(0)))
                .andExpect(jsonPath("listInfoMessages[0]").value(MockResponseResource.MESSAGE_INFO_TEST));
    }

    @Test
    public void testResponseWarning() throws Exception{

        MockHttpServletRequestBuilder httpRequest = get("/mock/controller/sendResponseWarning");

        this.mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("listInfoMessages").isArray())
                .andExpect(jsonPath("listWarningMessages").isArray())
                .andExpect(jsonPath("listInfoMessages", hasSize(0)))
                .andExpect(jsonPath("listWarningMessages", hasSize(1)))
                .andExpect(jsonPath("listWarningMessages[0]").value(MockResponseResource.MESSAGE_WARNING_TEST));
    }
}
