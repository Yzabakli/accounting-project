package com.service;

import com.dto.CategoryDTO;

import java.util.List;

public interface CategoryService {

    CategoryDTO findById(Long id);

    List<CategoryDTO> listAllCategories();

    void save(CategoryDTO categoryDTO);

    void update(CategoryDTO categoryDTO);

    void deleteById(Long id);

    boolean isDescriptionAlreadyInUse(String categoryDTO);
}
