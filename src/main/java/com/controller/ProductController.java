package com.controller;

import com.dto.ProductDTO;
import com.enums.CalculationType;
import com.enums.ProductUnit;
import com.service.CategoryService;
import com.service.ProductService;
import com.service.SecurityService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final SecurityService securityService;

    public ProductController(ProductService productService, CategoryService categoryService, SecurityService securityService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.securityService = securityService;
    }

    @GetMapping("/list")
    public String listAllProducts(Model model) {

        model.addAttribute("products", productService.listAllByLoggedInCompany());

        return "product/product-list";
    }

    @GetMapping("/create")
    public String createProduct(Model model) {

        model.addAttribute("newProduct", new ProductDTO());
        model.addAttribute("categories", categoryService.listAllCategories());
        model.addAttribute("productUnits", ProductUnit.values());
        model.addAttribute("calculationTypes", CalculationType.values());

        return "product/product-create";
    }

    @PostMapping("/create")
    public String createProduct(@Valid @ModelAttribute("newProduct") ProductDTO newProduct, BindingResult bindingResult, Model model) {

        if (productService.isNameAlreadyInUse(newProduct.getName())) {

            bindingResult.rejectValue("name", "", "You already have a product with same name");
        }

        if (bindingResult.hasErrors()) {

            model.addAttribute("categories", categoryService.listAllCategories());
            model.addAttribute("productUnits", ProductUnit.values());
            model.addAttribute("calculationTypes", CalculationType.values());

            return "product/product-create";
        }

        productService.save(newProduct);

        return "redirect:/products/list";
    }

    @GetMapping("/update/{id}")
    public String editProduct(@PathVariable Long id, Model model) {

        ProductDTO productDTO = productService.findById(id);

        if (!productDTO.getCategory().getCompany().equals(securityService.getLoggedInUser().getCompany())) return "redirect:/products/list";

        model.addAttribute("product", productDTO);
        model.addAttribute("categories", categoryService.listAllCategories());
        model.addAttribute("productUnits", ProductUnit.values());
        model.addAttribute("calculationTypes", CalculationType.values());

        return "product/product-update";
    }

    @PostMapping("/update/{id}")
    public String editProduct(@Valid @ModelAttribute("product") ProductDTO product, BindingResult bindingResult, Model model) {

        if (productService.isNameAlreadyInUse(product.getName()) && productService.isNameNotPrevious(product.getId(), product.getName())) {

            bindingResult.rejectValue("name", "", "You already have a product with same name");
        }

        if (bindingResult.hasErrors()) {

            model.addAttribute("categories", categoryService.listAllCategories());
            model.addAttribute("productUnits", ProductUnit.values());
            model.addAttribute("calculationTypes", CalculationType.values());

            return "product/product-update";
        }

        productService.update(product);

        return "redirect:/products/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {

        ProductDTO productDTO = productService.findById(id);

        if (!productDTO.getCategory().getCompany().equals(securityService.getLoggedInUser().getCompany())) return "redirect:/products/list";

        productService.deleteById(id);

        return "redirect:/products/list";
    }
}
