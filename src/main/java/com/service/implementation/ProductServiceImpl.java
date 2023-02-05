package com.service.implementation;

import com.dto.ProductDTO;
import com.entity.Product;
import com.mapper.MapperUtil;
import com.repository.ProductRepository;
import com.service.ProductService;
import com.service.SecurityService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final SecurityService securityService;

    private final MapperUtil mapper;


    public ProductServiceImpl(ProductRepository productRepository, SecurityService securityService, MapperUtil mapper) {
        this.productRepository = productRepository;
        this.securityService = securityService;
        this.mapper = mapper;
    }

    @Override
    public ProductDTO findById(Long id) {
        return mapper.convert(productRepository.findById(id).orElseThrow(), ProductDTO.class);
    }

    @Override
    public boolean existByCategoryId(Long categoryId) {
        return productRepository.existsByCategory_Id(categoryId);
    }

    @Override
    public List<ProductDTO> listAllByLoggedInCompany() {

        return productRepository.findByCategory_Company_Id(securityService.getLoggedInUser().getCompany().getId())
                .stream()
                .map(product -> mapper.convert(product, ProductDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void save(ProductDTO productDTO) {

        Product product = mapper.convert(productDTO, Product.class);

        product.setAverageUnitCost(BigDecimal.ZERO);
        product.setName(product.getName().trim());

        productRepository.save(product);
    }

    @Override
    public void update(ProductDTO productDTO) {

        productDTO.setName(productDTO.getName().trim());

        productRepository.save(mapper.convert(productDTO, Product.class));
    }

    @Override
    public void deleteById(Long id) {

        Product product = productRepository.findById(id).orElseThrow();

        product.setIsDeleted(true);

        productRepository.save(product);
    }

    @Override
    public boolean isNameAlreadyInUse(String name) {

        return productRepository.existsByNameIgnoreCaseAndCategory_Company_Id(name.trim(), securityService.getLoggedInUser()
                .getCompany()
                .getId());
    }

    @Override
    public boolean isNameNotPrevious(Long id, String name) {

        return !productRepository.findById(id).orElseThrow().getName().equals(name.trim());
    }

    @Override
    public void setProductsAverageUnitCost(Long id, BigDecimal averageUnitCost) {

        Product product = productRepository.findById(id).orElseThrow();

        product.setAverageUnitCost(averageUnitCost);

        productRepository.save(product);
    }

    @Override
    public void decreaseQuantityInStock(Long id, int quantity) {

        Product product = productRepository.findById(id).orElseThrow();

        product.setQuantityInStock(product.getQuantityInStock() - quantity);

        productRepository.save(product);
    }

    @Override
    public void increaseQuantityInStock(Long id, int quantity) {

        Product product = productRepository.findById(id).orElseThrow();

        product.setQuantityInStock(product.getQuantityInStock() + quantity);

        productRepository.save(product);
    }
}
