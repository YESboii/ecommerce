package com.ayush.ayush.controller;

import com.ayush.ayush.AppConstants;
import com.ayush.ayush.controller.response.ProductListResponse;
import com.ayush.ayush.dto.ImageDto;
import com.ayush.ayush.dto.ProductRequest;
import com.ayush.ayush.dto.ProductResponse;
import com.ayush.ayush.model.Product;
import com.ayush.ayush.service.SellerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

/*
* The Seller id in the URI is not required because we can fet it from the JWT
* */
@RestController
@RequestMapping("/api/v1/seller/{id}/products")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;
    @GetMapping(value = "/{product-id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductResponse> getById(@PathVariable("product-id") int productId, @PathVariable("id") int sellerId){
        Optional<ProductResponse> product = sellerService.getProduct(productId, sellerId);
        if (product.isPresent()){
            return ResponseEntity.ok(product.get());
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductListResponse> getProducts(@RequestParam(value = "page",required = false,defaultValue = AppConstants.PAGE_NUMBER)int page,
                                                           @RequestParam(value = "size",required = false,defaultValue = AppConstants.PAGE_SIZE)int size,
                                                           @RequestParam(value = "sort",required = false,defaultValue = AppConstants.SORT_DIR)String sort,
                                                           @RequestParam(value = "parameter",required = false)String name,
                                                           @PathVariable("id") Integer sellerId){
        ProductListResponse products = sellerService.getProducts(sellerId,page,size,sort,name);

        return new ResponseEntity<>(products,HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<ProductResponse> saveProduct(@Valid @RequestBody ProductRequest productToBeSaved, @PathVariable("id") int sellerId){
        ProductResponse savedProduct = sellerService.save(productToBeSaved,sellerId);
        return new ResponseEntity<>(savedProduct,HttpStatus.CREATED);
    }
    @DeleteMapping("/{product-id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("product-id") int productId, @PathVariable("id") int sellerId){
        sellerService.delete(sellerId,productId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PutMapping(value = "/{product-id}",
                produces = MediaType.APPLICATION_JSON_VALUE,
                consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Product> updateProduct(@PathVariable("product-id") int productId,
                                                 @PathVariable("id") int sellerId,
                                                 @RequestBody @Valid ProductRequest productUpdated){
        Product updatedProduct = sellerService.update(productUpdated,sellerId,productId);
        return new ResponseEntity<>(updatedProduct,HttpStatus.OK);
    }

    @PostMapping(value = "/{productId}/img",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(@RequestParam("image") MultipartFile fileToBeUploaded,
                                             @PathVariable("id")int sellerId, @PathVariable("productId") Integer productId){

        sellerService.saveImage(sellerId,productId,fileToBeUploaded);

        return new ResponseEntity<>("Image Uploaded for productId: %s".formatted(productId),HttpStatus.OK);
    }
    @GetMapping(value = "/{productId}/img")
    public ResponseEntity<byte[]> getFile(@PathVariable("id")int sellerId,
                                          @PathVariable("productId") Integer productId){

        ImageDto dto = sellerService.getImage(sellerId,productId);
        if (dto==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok()
                .contentType(dto.mediaType())
                .body(dto.imgAsBytes());
    }
    @PutMapping(value = "/{productId}/img",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateFile(@RequestParam("image") MultipartFile fileToBeUploaded,
                                             @PathVariable("id")int sellerId, @PathVariable("productId") Integer productId){

        sellerService.updateImage(sellerId,productId,fileToBeUploaded);

        return new ResponseEntity<>("Image Updated for productId: %s".formatted(productId),HttpStatus.OK);
    }

}
