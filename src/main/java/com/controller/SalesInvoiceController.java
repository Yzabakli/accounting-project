package com.controller;

import com.dto.InvoiceDTO;
import com.dto.InvoiceProductDTO;
import com.enums.InvoiceType;
import com.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;

@Controller
@RequestMapping("/salesInvoices")
public class SalesInvoiceController {

    private final InvoiceService invoiceService;
    private final InvoiceProductService invoiceProductService;
    private final ClientVendorService clientVendorService;
    private final ProductService productService;
    private final SecurityService securityService;

    public SalesInvoiceController(InvoiceService invoiceService, InvoiceProductService invoiceProductService, ClientVendorService clientVendorService, ProductService productService, SecurityService securityService) {
        this.invoiceService = invoiceService;
        this.invoiceProductService = invoiceProductService;
        this.clientVendorService = clientVendorService;
        this.productService = productService;
        this.securityService = securityService;
    }

    @GetMapping("/list")
    public String listSaleInvoices(Model model) {

        model.addAttribute("invoices", invoiceService.listAllSalesInvoicesForLoggedInCompany());
        model.addAttribute("profitMapById", invoiceService.getAllApprovedSalesInvoicesTotalProfitMap());

        return "invoice/sales-invoice-list";
    }

    @GetMapping("/create")
    public String createInvoice(Model model) {

        model.addAttribute("newSalesInvoice", new InvoiceDTO() {{

            setInvoiceNo(invoiceService.invoiceNoGenerator(InvoiceType.SALES));
            setDate(LocalDate.now());

        }});
        model.addAttribute("clients", clientVendorService.listAllClientsForLoggedInCompany());

        return "invoice/sales-invoice-create";
    }

    @PostMapping("/create")
    public String createInvoice(@Valid @ModelAttribute("newSalesInvoice") InvoiceDTO newSalesInvoice, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {

            model.addAttribute("clients", clientVendorService.listAllClientsForLoggedInCompany());

            return "invoice/sales-invoice-create";
        }

        return "redirect:/salesInvoices/update/" + invoiceService.save(newSalesInvoice, InvoiceType.SALES);
    }

    @GetMapping({"/update/{id}", "/addInvoiceProduct/{id}"})
    public String editInvoice(@PathVariable Long id, Model model) {

        InvoiceDTO invoiceDTO = invoiceService.findById(id);

        if (!invoiceDTO.getCompany().equals(securityService.getLoggedInUser().getCompany())) return "redirect:/salesInvoices/list";

        model.addAttribute("invoice", invoiceDTO);
        model.addAttribute("clients", clientVendorService.listAllClientsForLoggedInCompany());
        model.addAttribute("newInvoiceProduct", new InvoiceProductDTO());
        model.addAttribute("products", productService.listAllByLoggedInCompany());

        return "invoice/sales-invoice-update";
    }

    @PostMapping("/update/{id}")
    public String editInvoice(@Valid @ModelAttribute("invoice") InvoiceDTO invoice, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {

            model.addAttribute("clients", clientVendorService.listAllClientsForLoggedInCompany());
            model.addAttribute("products", productService.listAllByLoggedInCompany());

            return "invoice/sales-invoice-update";
        }

        invoiceService.update(invoice);

        return "redirect:/salesInvoices/list";
    }

    @PostMapping("/addInvoiceProduct/{invoiceId}")
    public String addProduct(@Valid @ModelAttribute("newInvoiceProduct") InvoiceProductDTO newInvoiceProduct, BindingResult bindingResult, @PathVariable Long invoiceId, Model model) {

        if (newInvoiceProduct.getProduct() != null) {

            if (newInvoiceProduct.getQuantity() > newInvoiceProduct.getProduct().getQuantityInStock()) {

                model.addAttribute("error", "Not enough " + newInvoiceProduct.getProduct()
                        .getName() + " quantity to sell. Amount in the stock " + newInvoiceProduct.getProduct()
                        .getQuantityInStock());
                bindingResult.addError(new ObjectError("newInvoiceProduct", ""));
            }
        }

        if (bindingResult.hasErrors()) {

            model.addAttribute("invoice", invoiceService.findById(invoiceId));
            model.addAttribute("clients", clientVendorService.listAllClientsForLoggedInCompany());
            model.addAttribute("products", productService.listAllByLoggedInCompany());

            return "invoice/sales-invoice-update";
        }

        invoiceProductService.save(invoiceId, newInvoiceProduct);

        return "redirect:/salesInvoices/update/" + invoiceId;
    }

    @GetMapping("/removeInvoiceProduct/{invoiceId}/{id}")
    public String removeInvoiceProduct(@PathVariable Long invoiceId, @PathVariable Long id) {

        InvoiceDTO invoiceDTO = invoiceService.findById(id);

        if (!invoiceDTO.getCompany().equals(securityService.getLoggedInUser().getCompany())) return "redirect:/salesInvoices/list";

        invoiceProductService.deleteById(id);

        return "redirect:/salesInvoices/update/" + invoiceId;
    }

    @GetMapping("/delete/{id}")
    public String deleteInvoice(@PathVariable Long id) {

        InvoiceDTO invoiceDTO = invoiceService.findById(id);

        if (!invoiceDTO.getCompany().equals(securityService.getLoggedInUser().getCompany())) return "redirect:/salesInvoices/list";

        invoiceService.deleteById(id);

        return "redirect:/salesInvoices/list";
    }

    @GetMapping("/approve/{id}")
    public String approveSalesInvoice(@PathVariable Long id) {

        InvoiceDTO invoiceDTO = invoiceService.findById(id);

        if (!invoiceDTO.getCompany().equals(securityService.getLoggedInUser().getCompany())) return "redirect:/salesInvoices/list";

        invoiceService.salesApprove(id);

        return "redirect:/salesInvoices/list";
    }

    @GetMapping("/print/{id}")
    public String printInvoice(@PathVariable Long id, Model model) {

        InvoiceDTO invoiceDTO = invoiceService.findById(id);

        if (!invoiceDTO.getCompany().equals(securityService.getLoggedInUser().getCompany())) return "redirect:/salesInvoices/list";

        model.addAttribute("company", securityService.getLoggedInUser().getCompany());
        model.addAttribute("invoice", invoiceDTO);
        model.addAttribute("invoiceProducts", invoiceProductService.getAllByInvoiceId(id));

        return "invoice/invoice_print";
    }
}