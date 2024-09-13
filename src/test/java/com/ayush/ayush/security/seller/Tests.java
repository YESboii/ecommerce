package com.ayush.ayush.security.seller;

import com.ayush.ayush.dto.AuthenticationRequest;
import com.ayush.ayush.model.Seller;
import com.ayush.ayush.model.embeddedable.Role;
import com.ayush.ayush.repository.SellerRepositoryJpa;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


//Integration tests
@SpringBootTest
@AutoConfigureCache
@AutoConfigureDataJpa
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
@ImportAutoConfiguration
@AutoConfigureMockMvc
@Import(TestConfig.class)
public class Tests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private SellerRepositoryJpa sellerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;

    @BeforeEach
    public void setUp(){
        Seller seller = new Seller();
        seller.setUsername("xyz@gmail.com");
        seller.setPassword(passwordEncoder.encode("12345"));
        seller.setRole(Role.SELLER);
        seller.setEnabled(true);
        seller.setGstId("ssks");
        seller.setTwoFactorEnabled(false);
        seller.setName("Ayush");

        sellerRepository.save(seller);
    }

    @Test
    public void testThatSellerLoginReturns401() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("xyz@gmail.com","123455");
        mockMvc.perform(post("/auth/seller/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized());
    }
    @Test
    public void testThatSellerLoginReturns200AndValidate() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("xyz@gmail.com","12345");
        mockMvc.perform(post("/auth/seller/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").isNotEmpty())
                .andExpect(jsonPath("$.refresh_token").isNotEmpty())
        ;
    }
    @Test
    public void testThatSellerLoginReturns200AndValidateItReturnsCorrectJwt() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("xyz@gmail.com","12345");
        MvcResult result = mockMvc.perform(
                        post("/auth/seller/authenticate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .accept(MediaType.APPLICATION_JSON)
                )
                // Step 3: Check for the expected 200 OK response
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.refresh_token").exists())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(json);
        String access = node.get("access_token").asText();
        String refresh = node.get("refresh_token").asText();

        assertTrue(jwtService.getIdClaim(access)==1L);
        assertTrue(jwtService.getIdClaim(refresh)==1L);
        assertTrue(jwtService.getRoleClaim(refresh)==Role.SELLER.ordinal());
        assertTrue(jwtService.getRoleClaim(access)==Role.SELLER.ordinal());
    }
    @Test
    public void testThatJwtTokenReceivedIsValidForAccessingEndpoints()throws Exception{
        AuthenticationRequest request = new AuthenticationRequest("xyz@gmail.com","12345");
        MvcResult result = mockMvc.perform(
                post("/auth/seller/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON)

        ).andReturn();
        String response = result.getResponse().getContentAsString();

        JsonNode node = objectMapper.readTree(response);
        String jwt = node.get("access_token").asText();

        mockMvc.perform(
                get("/api/v1/seller/products/test")
                        .header(HttpHeaders.AUTHORIZATION,"Bearer %s".formatted(jwt))
        ).andExpect(status().isOk()).andExpect(content().string("1"));
    }

    @Test
    @WithMockUser(authorities = "CUSTOMER")
    public void testThatAuthorizationForSellerApiReturnsForbidden()throws Exception{
        mockMvc.perform(
                get("/api/v1/seller/products/test")
        ).andExpect(status().isForbidden());

    }

}
