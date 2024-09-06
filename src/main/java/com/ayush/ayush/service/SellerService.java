package com.ayush.ayush.service;

import com.ayush.ayush.controller.response.ProductListResponse;
import com.ayush.ayush.dto.ImageDto;
import com.ayush.ayush.dto.ProductRequest;
import com.ayush.ayush.dto.ProductResponse;
import com.ayush.ayush.exceptions.StorageException;
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
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SellerService {

    private final ProductRepository productRepository;
    private final SellerRepositoryJpa sellerRepository;
    private final FileService fileService;


    public Optional<ProductResponse> getProduct(int id, int sellerId){
         Optional<Product> product = productRepository.findProductByIdAndSeller(id,sellerId);
         if (product.isEmpty()){
             return Optional.empty();
         }
         Product toBeMapped = product.get();
         byte[] img =  fileService.loadFileAsBytes(product.get().getImage());
         ProductResponse response = ProductMapper.toDto(toBeMapped,img);
         return Optional.of(response);
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
       List<ProductResponse> productResponses = new ArrayList<>();
       products.forEach(
               product -> {
                   byte[] img =  fileService.loadFileAsBytes(product.getImage());
                   ProductResponse response = ProductMapper.toDto(product,img);
                   productResponses.add(response);
               }
       );
       return new ProductListResponse(productResponses,ids.getNumber(),ids.getTotalPages());
    }

    public ProductResponse save(ProductRequest productRequest, int sellerId){

        Product product = ProductMapper.toEntity(productRequest);
        Seller seller = sellerRepository.getReferenceById(sellerId);
        product.setSeller(seller);
        return ProductMapper.toDto(productRepository.save(product),null);
    }
    public void delete(int sellerId, int id){
        productRepository.deleteProductByIdAndSeller(id,sellerId);
    }
    public Product update(ProductRequest productRequest, int sellerId, int productId){
        Product updatedProductDetails = ProductMapper.toEntity(productRequest);
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

     @Transactional(rollbackFor = {RuntimeException.class, StorageException.class} )
    public void saveImage(int sellerId, Integer productId, MultipartFile fileToBeUploaded) {
        Product product = productRepository.findProductByIdAndSeller(productId,sellerId).orElseThrow(
                 ()-> new EntityNotFoundException("id: "+productId+" doesn't exists. Cannot be uploaded"));

        String imageNameForProduct = fileService.save(fileToBeUploaded);
        product.setImage(imageNameForProduct);
    }
    public ImageDto getImage(int sellerId, int productId){
        String nameImg = productRepository.findImageBySellerIdAndId(productId,sellerId);
        if (nameImg == null) return null;
        byte[] imgAsBytes = fileService.loadFileAsBytes(nameImg);
        String extension = StringUtils.getFilenameExtension(nameImg);
        if(extension==null){
            throw new RuntimeException("Image: %s does not have a file extension".formatted(nameImg));
        }
        MediaType mt = switch (extension) {
            case "png" -> MediaType.IMAGE_PNG;
            case "jpg","jpeg" -> MediaType.IMAGE_JPEG;
            // Add more cases for other supported formats
            default -> throw new IllegalArgumentException("Unsupported image format: " + extension);
        };
        return new ImageDto(imgAsBytes,mt);
    }
    @Transactional(rollbackFor = {RuntimeException.class, StorageException.class} )
    public void updateImage(int sellerId, Integer productId, MultipartFile fileToBeUploaded) {
        Product product = productRepository.findProductByIdAndSeller(productId,sellerId).orElseThrow(
                ()-> new EntityNotFoundException("id: "+productId+" doesn't exists. Cannot be updated"));
        if(product.getImage()==null){
            saveImage(sellerId,productId,fileToBeUploaded);
        }
        fileService.update(fileToBeUploaded,product.getImage());
    }
}
