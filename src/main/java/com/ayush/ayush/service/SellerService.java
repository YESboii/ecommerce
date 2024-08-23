package com.ayush.ayush.service;

import com.ayush.ayush.dto.ProductDto;
import com.ayush.ayush.mapper.ProductMapper;
import com.ayush.ayush.model.Product;
import com.ayush.ayush.model.Seller;
import com.ayush.ayush.repository.ProductRepository;
import com.ayush.ayush.repository.SellerRepository;
import com.ayush.ayush.repository.SellerRepositoryJpa;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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

    public List<Product> getProducts(int sellerId, int page, int size, String sort, String name) {
        if (sort==null || name == null){
            Pageable pageable = PageRequest.of(page,
                    size);
            return productRepository.findProductsBySeller(sellerId,pageable).toList();
        }
        Sort sortObj = Sort.by(name);
        if (name.equals("asc") ){
            sortObj.ascending();
        }else {
            sortObj.descending();
        }
        Pageable pageable = PageRequest.of(page,size,sortObj);
        return productRepository.findProductsBySeller(sellerId,pageable).toList();
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
}
