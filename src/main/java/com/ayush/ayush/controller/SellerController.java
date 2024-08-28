package com.ayush.ayush.controller;

import com.ayush.ayush.AppConstants;
import com.ayush.ayush.controller.response.ProductListResponse;
import com.ayush.ayush.dto.ProductDto;
import com.ayush.ayush.model.Product;
import com.ayush.ayush.service.SellerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/seller/{id}/products")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;
    @GetMapping("/{product-id}")
    public ResponseEntity<Product> getById(@PathVariable("product-id") int productId, @PathVariable("id") int sellerId){
        Optional<Product> product = sellerService.getProduct(productId, sellerId);
        if (product.isPresent()){
            return ResponseEntity.ok(product.get());
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @GetMapping
    public ResponseEntity<ProductListResponse> getProducts(@RequestParam(value = "page",required = false,defaultValue = AppConstants.PAGE_NUMBER)int page,
                                                           @RequestParam(value = "size",required = false,defaultValue = AppConstants.PAGE_SIZE)int size,
                                                           @RequestParam(value = "sort",required = false,defaultValue = AppConstants.SORT_DIR)String sort,
                                                           @RequestParam(value = "parameter",required = false)String name,
                                                           @PathVariable("id") Integer sellerId){
        ProductListResponse products = sellerService.getProducts(sellerId,page,size,sort,name);

        return new ResponseEntity<>(products,HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<Product> saveProduct(@Valid @RequestBody ProductDto productToBeSaved, @PathVariable("id") int sellerId){
        Product savedProduct = sellerService.save(productToBeSaved,sellerId);
        return new ResponseEntity<>(savedProduct,HttpStatus.CREATED);
    }
    @DeleteMapping("/{product-id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("product-id") int productId, @PathVariable("id") int sellerId){
        sellerService.delete(sellerId,productId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PutMapping("/{product-id}")
    public ResponseEntity<Product> updateProduct(@PathVariable("product-id") int productId,
                                                 @PathVariable("id") int sellerId,
                                                 @RequestBody @Valid ProductDto productUpdated){
        Product updatedProduct = sellerService.update(productUpdated,sellerId,productId);
        return new ResponseEntity<>(updatedProduct,HttpStatus.OK);
    }

}
