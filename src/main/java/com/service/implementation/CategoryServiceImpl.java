package com.service.implementation;

import com.dto.CategoryDTO;
import com.dto.CompanyDTO;
import com.entity.Category;
import com.mapper.MapperUtil;
import com.repository.CategoryRepository;
import com.service.CategoryService;
import com.service.ProductService;
import com.service.SecurityService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductService productService;
    private final SecurityService securityService;
    private final MapperUtil mapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ProductService productService, MapperUtil mapper, SecurityService securityService) {
        this.categoryRepository = categoryRepository;
        this.productService = productService;
        this.securityService = securityService;
        this.mapper = mapper;
    }

    @Override
    public CategoryDTO findById(Long id) {

        CategoryDTO categoryDTO = mapper.convert(categoryRepository.findById(id).orElseThrow(), CategoryDTO.class);

        categoryDTO.setHasProduct(productService.existByCategoryId(categoryDTO.getId()));

        return categoryDTO;
    }

    @Override
    public List<CategoryDTO> listAllCategories() {

        List<CategoryDTO> list = categoryRepository.findByCompany_IdOrderByDescriptionAsc(getLoggedCompany().getId())
                .stream()
                .map(category -> mapper.convert(category, CategoryDTO.class))
                .collect(Collectors.toList());

        for (CategoryDTO categoryDTO : list) {
            categoryDTO.setHasProduct(productService.existByCategoryId(categoryDTO.getId()));
        }

        return list;
    }

    @Override
    public void save(CategoryDTO categoryDTO) {

        categoryDTO.setCompany(getLoggedCompany());

        Category category = mapper.convert(categoryDTO, new Category());

        category.setDescription(category.getDescription().trim());

        categoryRepository.save(category);
    }

    @Override
    public void update(CategoryDTO categoryDTO) {

        categoryDTO.setDescription(categoryDTO.getDescription().trim());

        categoryRepository.save(mapper.convert(categoryDTO, Category.class));
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public boolean isDescriptionAlreadyInUse(String description) {

        return categoryRepository.existsByCompany_IdAndDescriptionIgnoreCase(getLoggedCompany().getId(), description.trim());
    }

    private CompanyDTO getLoggedCompany() {
        return securityService.getLoggedInUser().getCompany();
    }
}
