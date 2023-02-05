package com.service.implementation;

import com.dto.InvoiceProductDTO;
import com.service.InvoiceProductService;
import com.service.ReportingService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportingServiceImpl implements ReportingService {

    private final InvoiceProductService invoiceProductService;

    public ReportingServiceImpl(InvoiceProductService invoiceProductService) {
        this.invoiceProductService = invoiceProductService;
    }

    @Override
    public List<InvoiceProductDTO> getAllInvoiceProductsThatApprovedForLoggedCompany() {

        return invoiceProductService.getAllByInvoiceStatusApprovedForLoggedCompany();
    }

    @Override
    public Map<String, BigDecimal> getAllProfitLossPerMonth() {

        return invoiceProductService.getAllByInvoiceStatusApprovedForLoggedCompany()
                .stream()
                .collect(Collectors.toMap(invoiceProductDto -> invoiceProductDto.getInvoice()
                        .getDate()
                        .getYear() + " " + invoiceProductDto.getInvoice()
                        .getDate()
                        .getMonth(), InvoiceProductDTO::getProfitLoss, (BigDecimal::add)));
    }
}
