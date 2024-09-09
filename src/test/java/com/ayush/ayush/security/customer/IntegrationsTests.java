package com.ayush.ayush.security.customer;


import com.ayush.ayush.dto.AuthenticationRequest;
import com.ayush.ayush.model.Customer;
import com.ayush.ayush.model.embeddedable.Role;
import com.ayush.ayush.repository.CustomerRepository;
import com.ayush.ayush.service.JwtService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
@AutoConfigureMockMvc
@SpringBootTest
@AutoConfigureCache
@AutoConfigureDataJpa
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
@ImportAutoConfiguration
@Import(TestConfig.class)
public class IntegrationsTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @BeforeEach
    public void init(){
        Customer customer = new Customer();
        customer.setName("Ayush");
        customer.setUsername("xyz@gmail.com");
        customer.setEnabled(true);
        customer.setRole(Role.CUSTOMER);
        customer.setPassword(passwordEncoder.encode("12345"));
        customer.setTwoFactorEnabled(false);
        customerRepository.save(customer);
    }

    @Test
    public void testThatCustomerSecuredEndpointReturns401() throws Exception {
        mockMvc.perform(
                get("/api/v1/customer/test")
        ).andExpect(status().isUnauthorized());
    }
    @Test
    public void testThatCustomerLoginReturns401OnWrongPassword() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("xyz@gmail.com","1234566");
        mockMvc.perform(
                post("/auth/customer/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isUnauthorized());
    }
    @Test
    public void testThatCustomerLoginReturnsJwtAnd200OnCorrectCredentials()throws Exception{
        AuthenticationRequest request = new AuthenticationRequest("xyz@gmail.com","12345");
        mockMvc.perform(
                post("/auth/customer/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").isNotEmpty())
                .andExpect(jsonPath("$.refresh_token").isNotEmpty());
    }

    @Test
    public void testThatCustomerLoginReturnsValidJwtAnd200OnCorrectCredentials()throws Exception{
       AuthenticationRequest request = new AuthenticationRequest("xyz@gmail.com","12345");
        MvcResult result = mockMvc.perform(
                post("/auth/customer/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").isNotEmpty())
                .andExpect(jsonPath("$.refresh_token").isNotEmpty()).andReturn();
        String jsonResponse = result.getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(jsonResponse);
        String access = node.get("access_token").asText();
        String refresh = node.get("refresh_token").asText();
        assertTrue(jwtService.getIdClaim(access)==1L);
        assertTrue(jwtService.getIdClaim(refresh)==1L);
        assertTrue(jwtService.getRoleClaim(access)==Role.CUSTOMER.ordinal());
        assertTrue(jwtService.getRoleClaim(refresh)==Role.CUSTOMER.ordinal());
    }
    @Test
    public void testThatJwtReceivedIsValidForAccessingSecuredEndpoint() throws Exception{
        AuthenticationRequest request = new AuthenticationRequest("xyz@gmail.com","12345");
        MvcResult result = mockMvc.perform(
                post("/auth/customer/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(jsonResponse);
        String access = node.get("access_token").asText();

        mockMvc.perform(
                get("/api/v1/customer/test")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(access))
        ).andExpect(
                status().isOk()
        ).andExpect(
                content().string("from secured test")
        );
    }
    @Test
    @WithMockUser(authorities = "SELLER")
    public void testThatCustomerSecuredCanOnlyBeAccessedByRoleCustomer() throws Exception{
        mockMvc.perform(
                get("/api/v1/customer/test")
        ).andExpect(
                status().isForbidden()
        );
    }
}
