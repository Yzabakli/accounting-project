package com.converter;

import com.dto.ProductDTO;
import com.service.ProductService;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ProductDTOConverter implements Converter<String, ProductDTO> {

    ProductService productService;

    public ProductDTOConverter(@Lazy ProductService productService) {
        this.productService = productService;
    }

    @Override
    public ProductDTO convert(String source) {

        if (source.isBlank()) return null;

        return productService.findById(Long.parseLong(source));
    }
}
