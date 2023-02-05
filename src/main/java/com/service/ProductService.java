package com.service;

import com.dto.ProductDTO;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    ProductDTO findById(Long id);

    boolean existByCategoryId(Long categoryId);

    List<ProductDTO> listAllByLoggedInCompany();

    void save(ProductDTO productDTO);

    void update(ProductDTO productDTO);

    void deleteById(Long id);

    boolean isNameAlreadyInUse(String name);

    boolean isNameNotPrevious(Long id, String name);

    void decreaseQuantityInStock(Long id, int quantity);

    void setProductsAverageUnitCost(Long id, BigDecimal averageUnitCost);

    void increaseQuantityInStock(Long id, int quantity);
}
