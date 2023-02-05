package com.service;

import com.dto.InvoiceProductDTO;

import java.math.BigDecimal;
import java.util.List;

public interface InvoiceProductService {
    BigDecimal getTotalPriceByInvoice(Long invoiceId);

    BigDecimal getTotalPriceWithTaxByInvoice(Long invoiceId);

    BigDecimal getTotalCostForLoggedCompany();

    BigDecimal getTotalSalesForLoggedCompany();

    BigDecimal getTotalProfitLossForLoggedCompany();

    List<InvoiceProductDTO> getAllByInvoiceStatusApprovedForLoggedCompany();

    List<InvoiceProductDTO> getAllByInvoiceId(Long id);

    void save(Long id, InvoiceProductDTO newInvoiceProduct);

    void deleteById(Long id);

    void setProfitLosses(Long id);

    void setAverageUnitPrice(Long id);

    List<InvoiceProductDTO> getAllApprovedSalesInvoices();

    boolean isStockSufficientForInvoice(List<InvoiceProductDTO> invoiceProductDTOs);
}
