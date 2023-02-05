package com.controller;

import com.bootstrap.StaticConstants;
import com.dto.CompanyDTO;
import com.service.CompanyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping("/list")
    public String getCompanies(Model model) {

        model.addAttribute("companies", companyService.listAllCompanies());

        return "company/company-list";
    }

    @GetMapping("/create")
    public String createCompany(Model model) {

        model.addAttribute("newCompany", new CompanyDTO());
        model.addAttribute("countries", StaticConstants.COUNTRY_LIST);

        return "company/company-create";
    }

    @PostMapping("/create")
    public String createCompany(@Valid @ModelAttribute("newCompany") CompanyDTO newCompany, BindingResult bindingResult, Model model) {

        if (companyService.isTitleAlreadyInUse(newCompany.getTitle())) {

            bindingResult.rejectValue("title", "", "We already have a company with same title");
        }

        if (bindingResult.hasErrors()) {

            model.addAttribute("countries", StaticConstants.COUNTRY_LIST);

            return "company/company-create";
        }

        companyService.save(newCompany);

        return "redirect:/companies/list";
    }

    @GetMapping("/update/{id}")
    public String editCompany(@PathVariable Long id, Model model) {

        model.addAttribute("company", companyService.findById(id));
        model.addAttribute("countries", StaticConstants.COUNTRY_LIST);

        return "company/company-update";
    }

    @PostMapping("/update/{id}")
    public String editCompany(@Valid @ModelAttribute("company") CompanyDTO company, BindingResult bindingResult, Model model) {

        if (companyService.isTitleAlreadyInUse(company.getTitle()) && companyService.isTitleNotPrevious(company.getId(), company.getTitle())) {

            bindingResult.rejectValue("title", "", "We already have a company with same title");
        }

        if (bindingResult.hasErrors()) {

            model.addAttribute("countries", StaticConstants.COUNTRY_LIST);

            return "company/company-update";
        }

        return "redirect:/companies/list";
    }


    @GetMapping("/activate/{id}")
    public String activateCompany(@PathVariable Long id) {

        companyService.activateCompanyStatus(id);

        return "redirect:/companies/list";
    }

    @GetMapping("/deactivate/{id}")
    public String deactivateCompany(@PathVariable Long id) {

        companyService.deactivateCompanyStatus(id);

        return "redirect:/companies/list";
    }
}
