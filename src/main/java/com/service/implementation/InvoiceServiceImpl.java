package com.service.implementation;

import com.dto.CompanyDTO;
import com.dto.InvoiceDTO;
import com.dto.InvoiceProductDTO;
import com.entity.Invoice;
import com.enums.InvoiceStatus;
import com.enums.InvoiceType;
import com.mapper.MapperUtil;
import com.repository.InvoiceRepository;
import com.service.InvoiceProductService;
import com.service.InvoiceService;
import com.service.SecurityService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceProductService invoiceProductService;
    private final SecurityService securityService;
    private final MapperUtil mapper;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, @Lazy InvoiceProductService invoiceProductService, SecurityService securityService, MapperUtil mapper) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceProductService = invoiceProductService;
        this.securityService = securityService;
        this.mapper = mapper;
    }

    public static void main(String[] args) {
        System.out.println();
    }

    @Override
    public InvoiceDTO findById(Long id) {

        InvoiceDTO invoiceDTO = mapper.convert(invoiceRepository.findById(id).orElseThrow(), InvoiceDTO.class);

        invoiceDTO.setInvoiceProducts(invoiceProductService.getAllByInvoiceId(invoiceDTO.getId()));

        return invoiceDTO;
    }

    @Override
    public List<InvoiceDTO> getLast3ApprovedInvoicesForCurrentUserCompany() {

        return priceSetter(dtoMapper(invoiceRepository.getLast5ApprovedInvoicesByCompanyId(getLoggedInCompanyId())));
    }

    @Override
    public List<InvoiceDTO> listAllPurchaseInvoicesForLoggedInCompany() {

        return priceSetter(dtoMapper(invoiceRepository.findByCompany_IdAndInvoiceTypeOrderByInvoiceNoDesc(getLoggedInCompanyId(), InvoiceType.PURCHASE.name())));
    }

    @Override
    public List<InvoiceDTO> listAllSalesInvoicesForLoggedInCompany() {

        List<InvoiceDTO> invoiceDTOS = priceSetter(dtoMapper(invoiceRepository.findByCompany_IdAndInvoiceTypeOrderByInvoiceNoDesc(getLoggedInCompanyId(), InvoiceType.SALES.name())));

        for (InvoiceDTO invoiceDTO : invoiceDTOS) {

            invoiceDTO.setStockSufficient(invoiceProductService.isStockSufficientForInvoice(invoiceDTO.getInvoiceProducts()));
        }

        return invoiceDTOS;
    }

    @Override
    public Long save(InvoiceDTO invoiceDTO, InvoiceType invoiceType) {

        invoiceDTO.setCompany(getLoggedInCompany());

        Invoice invoice = mapper.convert(invoiceDTO, Invoice.class);

        invoice.setInvoiceStatus(InvoiceStatus.AWAITING_APPROVAL);
        invoice.setInvoiceType(invoiceType);

        return invoiceRepository.save(invoice).getId();
    }

    @Override
    public void update(InvoiceDTO invoiceDTO) {

        Invoice invoiceFromDB = invoiceRepository.findById(invoiceDTO.getId()).orElseThrow();

        Invoice invoice = mapper.convert(invoiceDTO, Invoice.class);

        invoice.setInvoiceStatus(invoiceFromDB.getInvoiceStatus());

        invoiceRepository.save(invoice);
    }

    @Override
    public void deleteById(Long id) {

        for (InvoiceProductDTO invoiceProductDTO : invoiceProductService.getAllByInvoiceId(id)) {

            invoiceProductService.deleteById(invoiceProductDTO.getId());
        }

        invoiceRepository.deleteById(id);
    }

    @Override
    public boolean existsByClientVendorId(Long clientVendorId) {

        return invoiceRepository.existsByClientVendor_Id(clientVendorId);
    }

    @Override
    public String invoiceNoGenerator(InvoiceType invoiceType) {

        var invoiceNo = Integer.parseInt(invoiceRepository.getLatestInvoice_InvoiceNo(getLoggedInCompanyId(), invoiceType.name())
                .substring(2)) + 1;

        return invoiceType.getValue().charAt(0) + "-" + "0".repeat(3 - String.valueOf(invoiceNo)
                .length()) + invoiceNo;
    }

    @Override
    public InvoiceDTO findByIdWithPrices(Long id) {

        InvoiceDTO invoiceDTO = findById(id);

        invoiceDTO.setPrice(invoiceProductService.getTotalPriceByInvoice(id));
        invoiceDTO.setTotal(invoiceProductService.getTotalPriceWithTaxByInvoice(id));
        invoiceDTO.setTax(invoiceDTO.getTotal().subtract(invoiceDTO.getPrice()));
        invoiceDTO.setInvoiceProducts(invoiceProductService.getAllByInvoiceId(id));

        return invoiceDTO;
    }

    @Override
    public void salesApprove(Long id) {

        Invoice invoice = invoiceRepository.findById(id).orElseThrow();

        invoice.setDate(LocalDate.now());
        invoice.setInvoiceStatus(InvoiceStatus.APPROVED);
        invoiceProductService.setProfitLosses(id);
        invoice.setLastUpdateDateTime(LocalDateTime.now());

        invoiceRepository.save(invoice);
    }

    @Override
    public void purchaseApprove(Long id) {

        Invoice invoice = invoiceRepository.findById(id).orElseThrow();

        invoice.setDate(LocalDate.now());
        invoice.setInvoiceStatus(InvoiceStatus.APPROVED);
        invoiceProductService.setAverageUnitPrice(id);
        invoice.setLastUpdateDateTime(LocalDateTime.now());

        invoiceRepository.save(invoice);
    }

    @Override
    public Map<Long, BigDecimal> getAllApprovedSalesInvoicesTotalProfitMap() {

        return invoiceProductService.getAllApprovedSalesInvoices()
                .stream().collect(Collectors.toMap(invoiceProductDto -> invoiceProductDto.getInvoice()
                        .getId(), InvoiceProductDTO::getProfitLoss, BigDecimal::add));
    }

    private Long getLoggedInCompanyId() {
        return securityService.getLoggedInUser().getCompany().getId();
    }

    private CompanyDTO getLoggedInCompany() {
        return securityService.getLoggedInUser().getCompany();
    }

    private List<InvoiceDTO> priceSetter(List<InvoiceDTO> invoiceDtoList) {

        for (InvoiceDTO invoiceDTO : invoiceDtoList) {

            invoiceDTO.setPrice(invoiceProductService.getTotalPriceByInvoice(invoiceDTO.getId()));
            invoiceDTO.setTotal(invoiceProductService.getTotalPriceWithTaxByInvoice(invoiceDTO.getId()));
            invoiceDTO.setTax(invoiceDTO.getTotal().subtract(invoiceDTO.getPrice()));
            invoiceDTO.setInvoiceProducts(invoiceProductService.getAllByInvoiceId(invoiceDTO.getId()));

            if (invoiceDTO.getInvoiceProducts().isEmpty()) deleteById(invoiceDTO.getId());
        }

        return invoiceDtoList;
    }

    private List<InvoiceDTO> dtoMapper(List<Invoice> clientVendors) {

        return clientVendors.stream()
                .map(invoice -> mapper.convert(invoice, InvoiceDTO.class))
                .collect(Collectors.toList());
    }
}
