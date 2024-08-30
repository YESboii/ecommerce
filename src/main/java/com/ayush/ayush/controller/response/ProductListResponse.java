package com.ayush.ayush.controller.response;

import com.ayush.ayush.dto.ProductResponse;
import com.ayush.ayush.model.Product;

import java.util.List;

public record ProductListResponse(List<ProductResponse> products, int currentPage, int totalPages) {}
