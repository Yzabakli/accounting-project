package com.controller;

import com.dto.CategoryDTO;
import com.service.CategoryService;
import com.service.SecurityService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final SecurityService securityService;

    public CategoryController(CategoryService categoryService, SecurityService securityService) {
        this.categoryService = categoryService;
        this.securityService = securityService;
    }

    @GetMapping("/list")
    public String listAllCategories(Model model) {

        model.addAttribute("categories", categoryService.listAllCategories());

        return "category/category-list";
    }

    @GetMapping("/create")
    public String createCategory(Model model) {

        model.addAttribute("newCategory", new CategoryDTO());

        return "category/category-create";
    }

    @PostMapping("/create")
    public String createCategory(@Valid @ModelAttribute("newCategory") CategoryDTO newCategory, BindingResult bindingResult) {

        if (categoryService.isDescriptionAlreadyInUse(newCategory.getDescription())) {

            bindingResult.rejectValue("description", "", "You already have a category with same description");
        }

        if (bindingResult.hasErrors()) return "category/category-create";

        categoryService.save(newCategory);

        return "redirect:list";
    }

    @GetMapping("/update/{id}")
    public String editCategory(@PathVariable Long id, Model model) {

        CategoryDTO categoryDTO = categoryService.findById(id);

        if (!categoryDTO.getCompany().equals(securityService.getLoggedInUser().getCompany())) return "redirect:list";

        model.addAttribute("category", categoryDTO);

        return "category/category-update";
    }

    @PostMapping("/update/{id}")
    public String editCategory(@Valid @ModelAttribute("category") CategoryDTO category, BindingResult bindingResult) {

        if (categoryService.isDescriptionAlreadyInUse(category.getDescription())) {

            bindingResult.rejectValue("description", "", "You already have a category with same description");
        }

        if (bindingResult.hasErrors()) return "category/category-update";

        categoryService.update(category);

        return "redirect:/categories/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {

        categoryService.deleteById(id);

        return "redirect:/categories/list";
    }
}
