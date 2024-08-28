package com.ayush.ayush.controller.response;

import com.ayush.ayush.model.Product;

import java.util.List;

public record ProductListResponse(List<Product> products,int currentPage, int totalPages) {}
