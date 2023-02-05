package com.service;

import com.dto.InvoiceProductDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ReportingService {
    List<InvoiceProductDTO> getAllInvoiceProductsThatApprovedForLoggedCompany();

    Map<String, BigDecimal> getAllProfitLossPerMonth();
}