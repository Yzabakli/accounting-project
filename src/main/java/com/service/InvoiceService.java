package com.service;

import com.dto.InvoiceDTO;
import com.enums.InvoiceType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface InvoiceService {
    List<InvoiceDTO> getLast3ApprovedInvoicesForCurrentUserCompany();

    InvoiceDTO findById(Long id);

    List<InvoiceDTO> listAllPurchaseInvoicesForLoggedInCompany();

    List<InvoiceDTO> listAllSalesInvoicesForLoggedInCompany();

    Long save(InvoiceDTO invoiceDTO, InvoiceType invoiceType);

    void update(InvoiceDTO invoiceDTO);

    void deleteById(Long id);

    boolean existsByClientVendorId(Long clientVendorId);

    String invoiceNoGenerator(InvoiceType invoiceType);

    InvoiceDTO findByIdWithPrices(Long id);

    void salesApprove(Long id);

    void purchaseApprove(Long id);

    Map<Long, BigDecimal> getAllApprovedSalesInvoicesTotalProfitMap();

}
