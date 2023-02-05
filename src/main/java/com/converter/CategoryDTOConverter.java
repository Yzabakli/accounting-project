package com.converter;

import com.dto.CategoryDTO;
import com.service.CategoryService;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CategoryDTOConverter implements Converter<String, CategoryDTO> {

    CategoryService categoryService;

    public CategoryDTOConverter(@Lazy CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public CategoryDTO convert(String source) {

        if (source.isBlank()) return null;

        return categoryService.findById(Long.parseLong(source));
    }
}
