package com.controller;

import com.dto.InvoiceDTO;
import com.dto.InvoiceProductDTO;
import com.enums.InvoiceType;
import com.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import reactor.util.annotation.Nullable;

import javax.validation.Valid;
import java.time.LocalDate;

@Controller
@RequestMapping("/purchaseInvoices")
public class PurchaseInvoiceController {

    private final InvoiceService invoiceService;
    private final InvoiceProductService invoiceProductService;
    private final ClientVendorService clientVendorService;
    private final ProductService productService;
    private final SecurityService securityService;

    public PurchaseInvoiceController(InvoiceService invoiceService, InvoiceProductService invoiceProductService, ClientVendorService clientVendorService, ProductService productService, SecurityService securityService) {
        this.invoiceService = invoiceService;
        this.invoiceProductService = invoiceProductService;
        this.clientVendorService = clientVendorService;
        this.productService = productService;
        this.securityService = securityService;
    }

    @GetMapping("/list")
    public String listPurchaseInvoices(Model model) {

        model.addAttribute("invoices", invoiceService.listAllPurchaseInvoicesForLoggedInCompany());

        return "invoice/purchase-invoice-list";
    }

    @GetMapping("/create")
    public String createInvoice(Model model) {

        model.addAttribute("newPurchaseInvoice", new InvoiceDTO() {{

            setInvoiceNo(invoiceService.invoiceNoGenerator(InvoiceType.PURCHASE));
            setDate(LocalDate.now());

        }});
        model.addAttribute("vendors", clientVendorService.listAllVendorsForLoggedInCompany());

        return "invoice/purchase-invoice-create";
    }

    @PostMapping("/create")
    public String createInvoice(@Valid @ModelAttribute("newPurchaseInvoice") InvoiceDTO newInvoice, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {

            model.addAttribute("vendors", clientVendorService.listAllVendorsForLoggedInCompany());

            return "invoice/purchase-invoice-create";
        }

        return "redirect:/purchaseInvoices/addInvoiceProduct/" + invoiceService.save(newInvoice, InvoiceType.PURCHASE);
    }

    @GetMapping({"/update/{id}", "/addInvoiceProduct/{id}"})
    public String editInvoice(@PathVariable Long id, Model model) {

        InvoiceDTO invoiceDTO = invoiceService.findById(id);

        if (!invoiceDTO.getCompany().equals(securityService.getLoggedInUser().getCompany())) return "redirect:/purchaseInvoices/list";

        model.addAttribute("invoice", invoiceDTO);
        model.addAttribute("newInvoiceProduct", new InvoiceProductDTO());
        model.addAttribute("vendors", clientVendorService.listAllVendorsForLoggedInCompany());
        model.addAttribute("products", productService.listAllByLoggedInCompany());

        return "invoice/purchase-invoice-update";
    }

    @PostMapping("/update/{id}")
    public String editInvoice(@Valid @ModelAttribute("invoice") InvoiceDTO invoice, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            
            model.addAttribute("vendors", clientVendorService.listAllVendorsForLoggedInCompany());
            model.addAttribute("products", productService.listAllByLoggedInCompany());

            return "invoice/purchase-invoice-update";
        }

        invoiceService.update(invoice);

        return "redirect:/purchaseInvoices/list";
    }

    @PostMapping("/addInvoiceProduct/{invoiceId}")
    public String addProduct(@Valid @ModelAttribute("newInvoiceProduct") InvoiceProductDTO newInvoiceProduct, BindingResult bindingResult, @PathVariable Long invoiceId, Model model) {

        if (bindingResult.hasErrors()) {

            model.addAttribute("invoice", invoiceService.findById(invoiceId));
            model.addAttribute("vendors", clientVendorService.listAllVendorsForLoggedInCompany());
            model.addAttribute("products", productService.listAllByLoggedInCompany());

            return "invoice/purchase-invoice-update";
        }

        invoiceProductService.save(invoiceId, newInvoiceProduct);

        return "redirect:/purchaseInvoices/update/" + invoiceId;
    }

    @GetMapping("/removeInvoiceProduct/{invoiceId}/{id}")
    public String removeInvoiceProduct(@PathVariable Long invoiceId, @PathVariable Long id) {

        InvoiceDTO invoiceDTO = invoiceService.findById(id);

        if (!invoiceDTO.getCompany().equals(securityService.getLoggedInUser().getCompany())) return "redirect:/purchaseInvoices/list";

        invoiceProductService.deleteById(id);

        return "redirect:/purchaseInvoices/update/" + invoiceId;
    }

    @GetMapping("/delete/{id}")
    public String deleteInvoice(@PathVariable Long id) {

        InvoiceDTO invoiceDTO = invoiceService.findById(id);

        if (!invoiceDTO.getCompany().equals(securityService.getLoggedInUser().getCompany())) return "redirect:/purchaseInvoices/list";

        invoiceService.deleteById(id);

        return "redirect:/purchaseInvoices/list";
    }

    @GetMapping("/approve/{id}")
    public String approvePurchaseInvoice(@PathVariable Long id) {

        InvoiceDTO invoiceDTO = invoiceService.findById(id);

        if (!invoiceDTO.getCompany().equals(securityService.getLoggedInUser().getCompany())) return "redirect:/purchaseInvoices/list";

        invoiceService.purchaseApprove(id);

        return "redirect:/purchaseInvoices/list";
    }

    @GetMapping("/print/{id}")
    public String printInvoice(@PathVariable Long id, Model model) {

        InvoiceDTO invoiceDTO = invoiceService.findById(id);

        if (!invoiceDTO.getCompany().equals(securityService.getLoggedInUser().getCompany())) return "redirect:/purchaseInvoices/list";

        model.addAttribute("company", securityService.getLoggedInUser().getCompany());
        model.addAttribute("invoice", invoiceDTO);
        model.addAttribute("invoiceProducts", invoiceProductService.getAllByInvoiceId(id));

        return "invoice/purchase-invoice_print";
    }
}