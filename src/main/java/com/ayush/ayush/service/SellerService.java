package com.ayush.ayush.service;

import com.ayush.ayush.controller.response.ProductListResponse;
import com.ayush.ayush.dto.ProductDto;
import com.ayush.ayush.mapper.ProductMapper;
import com.ayush.ayush.model.Product;
import com.ayush.ayush.model.Seller;
import com.ayush.ayush.repository.ProductRepository;
import com.ayush.ayush.repository.SellerRepositoryJpa;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SellerService {

    private final ProductRepository productRepository;
    private final SellerRepositoryJpa sellerRepository;


    public Optional<Product> getProduct(int id,int sellerId){
        return productRepository.findProductByIdAndSeller(id,sellerId);
    }


    public ProductListResponse getProducts(int sellerId, int page, int size, String sortDirection, String sortBy) {

        Sort sort = Sort.unsorted();
        if (sortBy != null && (sortBy.equalsIgnoreCase("quantity")
                || sortBy.equalsIgnoreCase("name")
                || sortBy.equalsIgnoreCase("amount"))) {
            if ("desc".equalsIgnoreCase(sortDirection)) {
                sort = Sort.by(sortBy).descending();
            } else {
                sort = Sort.by(sortBy).ascending();
            }
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        //Two Methods were implemented because we wanted to fetch the mapping as well as paginate the data.
       Page<Long> ids = productRepository.findProductIdsBySellerPagination(sellerId, pageable);
       List<Product> products = productRepository.findProductsBySeller(ids.toList());
       return new ProductListResponse(products,ids.getNumber(),ids.getTotalPages());
    }

    public Product save(ProductDto productDto, int sellerId){

        Product product = ProductMapper.toEntity(productDto);
        Seller seller = sellerRepository.findById(sellerId).orElseThrow(RuntimeException::new);
        product.setSeller(seller);
        return productRepository.save(product);
    }
    public void delete(int sellerId, int id){
        productRepository.deleteProductByIdAndSeller(id,sellerId);
    }
    public Product update(ProductDto productDto,int sellerId,int productId){
        Product updatedProductDetails = ProductMapper.toEntity(productDto);
        Product productToBeUpdated = productRepository.findProductByIdAndSeller(productId,sellerId).orElseThrow(
                ()-> new EntityNotFoundException("id: "+productId+" doesn't exists. Cannot be updated"));
        //copy non null fields
        utilsCopy(updatedProductDetails,productToBeUpdated);
        return productRepository.save(productToBeUpdated);
     }

     private void utilsCopy(Product source, Product target){
         Field[] fields = source.getClass().getDeclaredFields();

         for (Field field : fields) {
             try {

                 field.setAccessible(true);
                 if ("id".equals(field.getName())) {
                     continue;
                 }

                 Object value = field.get(source);


                 if (value != null) {
                     field.set(target, value);
                 }
             } catch (IllegalAccessException e) {
                 e.printStackTrace();
             }
         }

     }
}
