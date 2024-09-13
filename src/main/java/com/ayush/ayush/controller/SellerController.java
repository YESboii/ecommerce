package com.ayush.ayush.controller;

import com.ayush.ayush.AppConstants;
import com.ayush.ayush.controller.response.ProductListResponse;
import com.ayush.ayush.dto.ImageDto;
import com.ayush.ayush.dto.ProductRequest;
import com.ayush.ayush.dto.ProductResponse;
import com.ayush.ayush.model.Product;
import com.ayush.ayush.model.Seller;
import com.ayush.ayush.service.SellerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Optional;


@RestController
@RequestMapping("/api/v1/seller/products")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;
    @GetMapping("/test")
    public ResponseEntity<Long> test(Authentication authentication){
        System.out.println(authentication.getPrincipal()==null);
        return  ResponseEntity.ok(getSellerId(authentication));
    }
    @GetMapping(value = "/{product-id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductResponse> getById(@PathVariable("product-id") int productId,
                                                   Authentication authentication){
        Optional<ProductResponse> product = sellerService.getProduct(productId, getSellerId(authentication));
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
                                                           Authentication authentication){
        ProductListResponse products = sellerService.getProducts(getSellerId(authentication),page,size,sort,name);

        return new ResponseEntity<>(products,HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<ProductResponse> saveProduct(@Valid @RequestBody ProductRequest productToBeSaved,
                                                       Authentication authentication){
        ProductResponse savedProduct = sellerService.save(productToBeSaved,getSellerId(authentication));
        return new ResponseEntity<>(savedProduct,HttpStatus.CREATED);
    }
    @DeleteMapping("/{product-id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("product-id") int productId,
                                              Authentication authentication){
        sellerService.delete(getSellerId(authentication),productId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PutMapping(value = "/{product-id}",
                produces = MediaType.APPLICATION_JSON_VALUE,
                consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Product> updateProduct(@PathVariable("product-id") int productId,
                                                 Authentication authentication,
                                                 @RequestBody @Valid ProductRequest productUpdated){
        Product updatedProduct = sellerService.update(productUpdated,getSellerId(authentication),productId);
        return new ResponseEntity<>(updatedProduct,HttpStatus.OK);
    }

    @PostMapping(value = "/{productId}/img",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(@RequestParam("image") MultipartFile fileToBeUploaded,
                                             Authentication authentication,
                                             @PathVariable("productId") Integer productId){

        sellerService.saveImage(getSellerId(authentication),productId,fileToBeUploaded);

        return new ResponseEntity<>("Image Uploaded for productId: %s".formatted(productId),HttpStatus.OK);
    }
    @GetMapping(value = "/{productId}/img")
    public ResponseEntity<byte[]> getFile(Authentication authentication,
                                          @PathVariable("productId") Integer productId){

        ImageDto dto = sellerService.getImage(getSellerId(authentication),productId);
        if (dto==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok()
                .contentType(dto.mediaType())
                .body(dto.imgAsBytes());
    }
    @PutMapping(value = "/{productId}/img",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateFile(@RequestParam("image") MultipartFile fileToBeUploaded,
                                             Authentication authentication,
                                             @PathVariable("productId") Integer productId){

        sellerService.updateImage(getSellerId(authentication),productId,fileToBeUploaded);

        return new ResponseEntity<>("Image Updated for productId: %s".formatted(productId),HttpStatus.OK);
    }
    private Long getSellerId(Authentication authentication){
        //alternate way is to extract id from the jwt token.....
        Object principal = authentication.getPrincipal();
        Seller seller = (Seller) principal;
        return seller.getId();
    }
}
