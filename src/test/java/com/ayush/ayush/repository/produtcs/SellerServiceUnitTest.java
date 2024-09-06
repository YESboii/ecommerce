package com.ayush.ayush.repository.produtcs;

import com.ayush.ayush.Utils;
import com.ayush.ayush.dto.ImageDto;
import com.ayush.ayush.dto.ProductRequest;
import com.ayush.ayush.dto.ProductResponse;
import com.ayush.ayush.mapper.ProductMapper;
import com.ayush.ayush.model.Product;
import com.ayush.ayush.repository.ProductRepository;
import com.ayush.ayush.repository.SellerRepositoryJpa;
import com.ayush.ayush.service.FileServiceImpl;
import com.ayush.ayush.service.SellerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Arrays;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SellerServiceUnitTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SellerRepositoryJpa sellerRepositoryJpa;

    @Mock
    private FileServiceImpl fileService;

    @InjectMocks
    private SellerService underTest;

    @Test
    public void testGetProductReturnsOptionalEmptyWhenNoneFound(){
        //mock
        when(productRepository.findProductByIdAndSeller(5,2)).thenReturn(Optional.empty());

        //test the unit in isolation
        Optional<ProductResponse> product = underTest.getProduct(5,2);

        //assert
        assertTrue(product.isEmpty());
    }
    @Test
    public void testGetProductReturnsProductWhenFound(){

        //mock
        when(productRepository.findProductByIdAndSeller(4,2))
                .thenReturn(Optional.of
                        (Utils.buildProduct()));

        //test the unit in isolation
        Optional<ProductResponse> product = underTest.getProduct(4,2);

        //assert
        assertTrue(product.isPresent());
        ProductResponse productResponse = product.get();
        assertTrue(productResponse.id()==4);
        assertEquals(Utils.PRODUCT_NAME,productResponse.name());
    }
    @Test
    void getImageReturnsNullIfNoImageFound(){
        when(productRepository.findImageBySellerIdAndId(5,5)).thenReturn(null);
        ImageDto imageDto = underTest.getImage(5,5);
        assertTrue(imageDto==null);
    }
    @Test
    void getImageReturnsImageWithJpg(){
        String imgName = "image.jpg";
        byte []img = new byte[]{1,2,3};

        when(productRepository.findImageBySellerIdAndId(4,2)).thenReturn(imgName);
        when(fileService.loadFileAsBytes(imgName)).thenReturn(img);

        ImageDto imageDto = underTest.getImage(2,4);

        assertTrue(Arrays.equals(img,imageDto.imgAsBytes()));
        assertThat(imageDto.mediaType()).isEqualTo(MediaType.IMAGE_JPEG);
        verify(productRepository,times(1)).findImageBySellerIdAndId(4,2);
        verify(fileService).loadFileAsBytes(imgName);
    }
    @Test
    void getImageReturnsImageWithJpeg(){
        String imgName = "image.jpeg";
        byte []img = new byte[]{1,2,3};

        when(productRepository.findImageBySellerIdAndId(4,2)).thenReturn(imgName);
        when(fileService.loadFileAsBytes(imgName)).thenReturn(img);
        ImageDto imageDto = underTest.getImage(2,4);
        assertTrue(Arrays.equals(img,imageDto.imgAsBytes()));
        assertThat(imageDto.mediaType()).isEqualTo(MediaType.IMAGE_JPEG);
        verify(productRepository,times(1)).findImageBySellerIdAndId(4,2);
        verify(fileService).loadFileAsBytes(imgName);
    }
    @Test
    void getImageReturnsImageWithPng(){
        String imgName = "image.png";
        byte []img = new byte[]{1,2,3};

        when(productRepository.findImageBySellerIdAndId(4,2)).thenReturn(imgName);
        when(fileService.loadFileAsBytes(imgName)).thenReturn(img);
        ImageDto imageDto = underTest.getImage(2,4);
        assertTrue(Arrays.equals(img,imageDto.imgAsBytes()));
        assertThat(imageDto.mediaType()).isEqualTo(MediaType.IMAGE_PNG);
        verify(productRepository,times(1)).findImageBySellerIdAndId(4,2);
        verify(fileService).loadFileAsBytes(imgName);
    }

    @Test
    void saveProduct(){
        ProductRequest productRequest = Utils.buildProductReq();
        Product productToBeSaved = Utils.productToBeSaved();
        Product savedProduct = Utils.savedProduct();

        ProductResponse expected = Utils.buildProductResponseWithoutImage();
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        ProductResponse productResponse = underTest.save(productRequest,2);

        assertThat(productResponse.id()).isEqualTo(expected.id());
        verify(sellerRepositoryJpa,times(1)).getReferenceById(2);
        verify(productRepository).save(any(Product.class));

    }
}
