package com.ayush.ayush.controllers.seller;


import com.ayush.ayush.Utils;
import com.ayush.ayush.controller.SellerController;
import com.ayush.ayush.controller.response.ProductListResponse;
import com.ayush.ayush.dto.ProductRequest;
import com.ayush.ayush.dto.ProductResponse;
import com.ayush.ayush.security.JwtAuthenticationFilter;
import com.ayush.ayush.service.SellerService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static  org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//disable @EnableJpaAuditing while testing this
@AutoConfigureMockMvc
@WebMvcTest(value = SellerController.class,excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE
,classes = JwtAuthenticationFilter.class))
public class SellerControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SellerService sellerService;



    @Test
    @WithSeller(id="2")
    public void testControllerReturnsAuthenticatedSellerId() throws Exception{
        mockMvc.perform(
                get("/api/v1/seller/products/test")
        ).andExpect(status().isOk()).andExpect(content().string("2"));
    }
    @Test
    @WithSeller
    public void testControllerReturns404WhenNoProductFound()throws Exception{

        when(sellerService.getProduct(3,1L)).thenReturn(Optional.empty());

        mockMvc.perform(
                get("/api/v1/seller/products/3")
        ).andExpect(status().isNotFound());
    }
    @Test
    @WithSeller
    public void testControllerReturns200WhenNoProductFound()throws Exception{
        ProductResponse response = Utils.buildProductResponseWithImage();

        when(sellerService.getProduct(4,1L)).thenReturn(Optional.of(response));

        mockMvc.perform(
                get("/api/v1/seller/products/4")
        ).andExpect(status().isOk());
    }

    @Test
    @WithSeller
    public void testControllerReturnsProductWithImage()throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        ProductResponse response = Utils.buildProductResponseWithImage();
        String expectedResponse =  objectMapper.writeValueAsString(response);
        when(sellerService.getProduct(4,1L)).thenReturn(Optional.of(response));

        MvcResult result = mockMvc.perform(
                get("/api/v1/seller/products/4")
        ).andExpect(status().isOk()).andReturn();

        String responseBody = result.getResponse().getContentAsString();

        assertTrue(responseBody.equals(expectedResponse));

    }

    @Test
    @WithSeller
    public void testControllerReturnsProductWithNoImage()throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        ProductResponse response = Utils.buildProductResponseWithoutImage();
        String expectedResponse =  objectMapper.writeValueAsString(response);
        when(sellerService.getProduct(4,1L)).thenReturn(Optional.of(response));

        MvcResult result = mockMvc.perform(
                get("/api/v1/seller/products/4")
        ).andExpect(status().isOk()).andReturn();

        String responseBody = result.getResponse().getContentAsString();
        System.out.println(responseBody);

        JsonNode responseNode = objectMapper.readTree(responseBody);
        JsonNode expectedNode = objectMapper.readTree(expectedResponse);


        assertThat(responseNode).isEqualTo(expectedNode);


        JsonNode imageField = responseNode.get("img");
        assertTrue(imageField.isNull());

    }
    @Test
    @WithSeller
    public void testControllerReturns200AndReturnsDefaultConfig()throws Exception{
        ProductResponse productResponse = Utils.buildProductResponseWithImageId(4,200.0,"name");
        ProductListResponse productListResponse = new ProductListResponse(List.of(productResponse),
                0,3);
        when(sellerService.getProducts(1L,0,1,"asc",null))
                .thenReturn(productListResponse);
        mockMvc.perform(
                get("/api/v1/seller/products")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }
    @Test
    @WithSeller
    public void testControllerReturns200AndReturnsMoreThanOneProductWhenPageSizeIsGreaterThanOneAndSorted()throws Exception{
        List<ProductResponse> productResponses = Utils.getProductListResponse();
        Comparator<ProductResponse> comparator = (a, b) -> Double.compare(a.amount(), b.amount());
        productResponses.sort(comparator);
        ProductListResponse expected = new ProductListResponse(productResponses,
                0,1);
        when(sellerService.getProducts(1L,0,3,"asc","amount"))
                .thenReturn(expected);
        MvcResult result = mockMvc.perform(
                get("/api/v1/seller/products")
                        .accept(MediaType.APPLICATION_JSON)
                        .queryParam("size","3")
                        .queryParam("parameter","amount")
        ).andExpect(status().isOk()).andReturn();

        ObjectMapper objectMapper = new ObjectMapper();

        String response = result.getResponse().getContentAsString();
        System.out.println(response);
        ProductListResponse actual = objectMapper.readValue(response,ProductListResponse.class);

        assertThat(actual.currentPage()).isEqualTo(expected.currentPage());
        assertThat(actual.totalPages()).isEqualTo(expected.totalPages());
        assertThat(actual.products()).isSortedAccordingTo(comparator);
    }
    @Test
    @WithSeller
    public void testControllerReturns200AndReturnsMoreThanOneProductWhenPageSizeIsGreaterThanOneAndSortedDesc()
            throws Exception{
        List<ProductResponse> productResponses = Utils.getProductListResponse();
        Comparator<ProductResponse> comparator = (a, b) -> Double.compare(b.amount(),a.amount());
        productResponses.sort(comparator);
        ProductListResponse expected = new ProductListResponse(productResponses,
                0,1);
        when(sellerService.getProducts(1L,0,3,"desc","amount"))
                .thenReturn(expected);
        MvcResult result = mockMvc.perform(
                get("/api/v1/seller/products")
                        .accept(MediaType.APPLICATION_JSON)
                        .queryParam("size","3")
                        .queryParam("sort","desc")
                        .queryParam("parameter","amount")
        ).andExpect(status().isOk()).andReturn();

        ObjectMapper objectMapper = new ObjectMapper();

        String response = result.getResponse().getContentAsString();
        System.out.println(response);
        ProductListResponse actual = objectMapper.readValue(response,ProductListResponse.class);

        assertThat(actual.currentPage()).isEqualTo(expected.currentPage());
        assertThat(actual.totalPages()).isEqualTo(expected.totalPages());
        assertThat(actual.products()).isSortedAccordingTo(comparator);
    }
    @Test
    @WithSeller
    public void testControllerReturns200AndReturnsMoreThanOneProductWhenPageSizeIsGreaterThanOneAndSortedByName()
            throws Exception{
        List<ProductResponse> productResponses = Utils.getProductListResponse();
        Comparator<ProductResponse> comparator = (a, b) -> a.name().compareTo(b.name());
        productResponses.sort(comparator);
        ProductListResponse expected = new ProductListResponse(productResponses,
                0,1);
        when(sellerService.getProducts(1L,0,3,"asc","name"))
                .thenReturn(expected);
        MvcResult result = mockMvc.perform(
                get("/api/v1/seller/products")
                        .accept(MediaType.APPLICATION_JSON)
                        .queryParam("size","3")
                        .queryParam("parameter","name")
        ).andExpect(status().isOk()).andReturn();

        ObjectMapper objectMapper = new ObjectMapper();

        String response = result.getResponse().getContentAsString();
        System.out.println(response);
        ProductListResponse actual = objectMapper.readValue(response,ProductListResponse.class);

        assertThat(actual.currentPage()).isEqualTo(expected.currentPage());
        assertThat(actual.totalPages()).isEqualTo(expected.totalPages());
        assertThat(actual.products()).isSortedAccordingTo(comparator);
    }
    @Test
    @WithSeller
    public void testControllerReturns200AndReturnsMoreThanOneProductWhenPageSizeIsGreaterThanOneAndSortedByNameDesc()
            throws Exception{
        List<ProductResponse> productResponses = Utils.getProductListResponse();
        Comparator<ProductResponse> comparator = (a, b) -> a.name().compareTo(b.name());
        productResponses.sort(comparator);
        ProductListResponse expected = new ProductListResponse(productResponses,
                0,1);
        when(sellerService.getProducts(1L,0,3,"desc","name"))
                .thenReturn(expected);
        MvcResult result = mockMvc.perform(
                get("/api/v1/seller/products")
                        .accept(MediaType.APPLICATION_JSON)
                        .queryParam("size","3")
                        .queryParam("sort", "desc")
                        .queryParam("parameter","name")
        ).andExpect(status().isOk()).andReturn();

        ObjectMapper objectMapper = new ObjectMapper();

        String response = result.getResponse().getContentAsString();
        System.out.println(response);
        ProductListResponse actual = objectMapper.readValue(response,ProductListResponse.class);

        assertThat(actual.currentPage()).isEqualTo(expected.currentPage());
        assertThat(actual.totalPages()).isEqualTo(expected.totalPages());
        assertThat(actual.products()).isSortedAccordingTo(comparator);
    }
    @Test
    @WithSeller
    public void testThatControllerReturns201ForCorrectProductRequest()throws Exception{
        ProductRequest request = Utils.validRequest();
        ProductResponse response = Utils.responseForValidRequest();
        when(sellerService.save(request,1L)).thenReturn(response);
        ObjectMapper mapper = new ObjectMapper();

        MvcResult result = mockMvc.perform(
                post("/api/v1/seller/products")
                        .with(csrf())
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andReturn();

        String responseBody = result.getResponse().getContentAsString();
        JsonNode node = mapper.readTree(responseBody);
        assertThat(request.getName()).isEqualTo(node.get("name").asText());
        assertThat(request.getAmount()).isEqualTo(node.get("amount").asDouble());
        assertThat(request.getDescription()).isEqualTo(node.get("description").asText());
    }
    @Test
    @WithSeller
    public void testThatControllerReturns204ForDelete()throws Exception{

         mockMvc.perform(

                delete("/api/v1/seller/products/4").with(csrf())
        ).andExpect(status().isNoContent());
        doNothing().when(sellerService).delete(1L, 4);
        verify(sellerService).delete(1L,4);

    }
}
