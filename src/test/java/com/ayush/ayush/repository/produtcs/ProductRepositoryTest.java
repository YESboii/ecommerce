package com.ayush.ayush.repository.produtcs;

import com.ayush.ayush.model.Product;
import com.ayush.ayush.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@Sql(scripts = "/dummy/products/dummy.sql")
public class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @Test
    public void testFindById(){
        Optional<Product> p = productRepository.findById(2);
        Product product = p.get();
        assertTrue(product!=null);
        assertTrue(product.getId()==2);
        assertTrue(product.getName().equals("Product A2"));
    }
    @Test
    public void testFindByIdReturnsOptionalEmpty(){
        Optional<Product> p = productRepository.findById(10);
        assertTrue(p.isEmpty());

    }

    @Test
    public void testThatProductProductRepositoryReturnsImage(){
        String imageName = productRepository.findImageBySellerIdAndId(3,2);
        assertThat(imageName).isNotNull().isEqualTo("image_b1.jpg");
    }
    @Test
    public void testThatProductProductRepositoryReturnsNullWhenNoImageFound(){
        String imageName = productRepository.findImageBySellerIdAndId(5,2);
        assertThat(imageName).isNull();
    }
}
