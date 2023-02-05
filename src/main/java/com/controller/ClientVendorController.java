package com.controller;

import com.bootstrap.StaticConstants;
import com.dto.ClientVendorDTO;
import com.enums.ClientVendorType;
import com.service.ClientVendorService;
import com.service.SecurityService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/clientVendors")
public class ClientVendorController {

    private final ClientVendorService clientVendorService;
    private final SecurityService securityService;

    public ClientVendorController(ClientVendorService clientVendorService, SecurityService securityService) {
        this.clientVendorService = clientVendorService;
        this.securityService = securityService;
    }

    @GetMapping("/list")
    public String listAllClientVendors(Model model) {

        model.addAttribute("clientVendors", clientVendorService.listAllClientVendorsForLoggedInCompany());

        return "clientVendor/clientVendor-list";
    }

    @GetMapping("/create")
    public String createClientVendor(Model model) {

        model.addAttribute("newClientVendor", new ClientVendorDTO());
        model.addAttribute("clientVendorTypes", ClientVendorType.values());
        model.addAttribute("countries", StaticConstants.COUNTRY_LIST);

        return "clientVendor/clientVendor-create";
    }

    @PostMapping("/create")
    public String createClientVendor(@Valid @ModelAttribute("newClientVendor") ClientVendorDTO newClientVendor, BindingResult bindingResult, Model model) {

        if (clientVendorService.isNameAlreadyInUse(newClientVendor.getClientVendorName())) {

            bindingResult.rejectValue("clientVendorName", "", "You already have a client/vendor with same name");
        }

        if (bindingResult.hasErrors()) {

            model.addAttribute("clientVendorTypes", ClientVendorType.values());
            model.addAttribute("countries", StaticConstants.COUNTRY_LIST);

            return "clientVendor/clientVendor-create";
        }

        clientVendorService.save(newClientVendor);

        return "redirect:/clientVendors/list";
    }

    @GetMapping("/update/{id}")
    public String editClientVendor(@PathVariable Long id, Model model) {

        ClientVendorDTO clientVendorDTO = clientVendorService.findById(id);

        if (!clientVendorDTO.getCompany().equals(securityService.getLoggedInUser().getCompany())) return "redirect:/clientVendors/list";

        model.addAttribute("clientVendor", clientVendorDTO);
        model.addAttribute("countries", StaticConstants.COUNTRY_LIST);
        model.addAttribute("clientVendorTypes", ClientVendorType.values());

        return "clientVendor/clientVendor-update";
    }

    @PostMapping("/update/{id}")
    public String editClientVendor(@Valid @ModelAttribute("clientVendor") ClientVendorDTO clientVendor, BindingResult bindingResult, Model model) {


        if (clientVendorService.isNameNotPrevious(clientVendor.getId(), clientVendor.getClientVendorName())) {

            bindingResult.rejectValue("clientVendorName", "", "You already have a client/vendor with same name");
        }

        if (bindingResult.hasErrors()) {

            model.addAttribute("countries", StaticConstants.COUNTRY_LIST);
            model.addAttribute("clientVendorTypes", ClientVendorType.values());

            return "clientVendor/clientVendor-update";
        }

        clientVendorService.update(clientVendor);

        return "redirect:/clientVendors/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteClientVendor(@PathVariable Long id, Model model) {

        ClientVendorDTO clientVendorDTO = clientVendorService.findById(id);

        if (!clientVendorDTO.getCompany().equals(securityService.getLoggedInUser().getCompany())) return "redirect:/clientVendors/list";

        if (clientVendorService.isThereAnyRelatedInvoice(id)) {

            model.addAttribute("error", "Cannot be deleted. Has invoice linked to Client/Vendor");
            model.addAttribute("clientVendors", clientVendorService.listAllClientVendorsForLoggedInCompany());

            return "clientVendor/clientVendor-list";
        }

        clientVendorService.deleteById(id);

        return "redirect:/clientVendors/list";
    }
}
