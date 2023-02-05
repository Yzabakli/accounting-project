package com.controller;

import com.service.DashboardService;
import com.service.InvoiceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final InvoiceService invoiceService;
    private final DashboardService dashboardService;

    public DashboardController(InvoiceService invoiceService, DashboardService dashboardService) {
        this.invoiceService = invoiceService;
        this.dashboardService = dashboardService;
    }


    @GetMapping
    public String dashboard(Model model) {

        model.addAttribute("exchangeRates", dashboardService.getExchangeRates());
        model.addAttribute("invoices", invoiceService.getLast3ApprovedInvoicesForCurrentUserCompany());
        model.addAttribute("summaryNumbers", dashboardService.financialSummaryForCurrentCompany());
        model.addAttribute("profitPerExchange", dashboardService.profitPerExchangeMap());

        return "dashboard";
    }
}
