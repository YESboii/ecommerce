package com.ayush.ayush.controller;

import com.ayush.ayush.dto.ProductDto;
import com.ayush.ayush.model.Product;
import com.ayush.ayush.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public ResponseEntity<List<Product>> getProducts(@RequestParam(value = "page",required = false,defaultValue = "0")int page,
                                                     @RequestParam(value = "size",required = false,defaultValue = "1")int size,
                                                     @RequestParam(value = "sort",required = false)String sort,
                                                     @RequestParam(value = "parameter",required = false)String name,
                                                     @PathVariable("id") int sellerId){
        List<Product> products = sellerService.getProducts(sellerId,page,size,sort,name);

        if (products.size()>0){
            return new ResponseEntity<>(products,HttpStatus.OK);
        }
        return new ResponseEntity<>(products,HttpStatus.NOT_FOUND);
    }
    @PostMapping
    public ResponseEntity<Product> saveProduct(@RequestBody ProductDto productToBeSaved,@PathVariable("id") int sellerId){
        Product savedProduct = sellerService.save(productToBeSaved,sellerId);
        return new ResponseEntity<>(savedProduct,HttpStatus.CREATED);
    }
    @DeleteMapping("/{product-id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("product-id") int productId, @PathVariable("id") int sellerId){
        sellerService.delete(sellerId,productId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
