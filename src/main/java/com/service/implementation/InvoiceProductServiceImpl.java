package com.service.implementation;

import com.dto.InvoiceProductDTO;
import com.entity.InvoiceProduct;
import com.enums.CalculationType;
import com.enums.InvoiceType;
import com.mapper.MapperUtil;
import com.repository.InvoiceProductRepository;
import com.service.InvoiceProductService;
import com.service.InvoiceService;
import com.service.ProductService;
import com.service.SecurityService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceProductServiceImpl implements InvoiceProductService {

    private final InvoiceProductRepository invoiceProductRepository;
    private final InvoiceService invoiceService;
    private final ProductService productService;
    private final SecurityService securityService;
    private final MapperUtil mapper;

    public InvoiceProductServiceImpl(InvoiceProductRepository invoiceProductRepository, SecurityService securityService, MapperUtil mapper, InvoiceService invoiceService, ProductService productService) {
        this.invoiceProductRepository = invoiceProductRepository;
        this.invoiceService = invoiceService;
        this.securityService = securityService;
        this.mapper = mapper;
        this.productService = productService;
    }

    @Override
    public void save(Long invoiceId, InvoiceProductDTO newInvoiceProduct) {

        newInvoiceProduct.setInvoice(invoiceService.findById(invoiceId));

        InvoiceProduct invoiceProduct = mapper.convert(newInvoiceProduct, InvoiceProduct.class);

        invoiceProduct.setRemainingQuantity(invoiceProduct.getQuantity());
        invoiceProduct.setProfitLoss(BigDecimal.ZERO);

        invoiceProductRepository.save(invoiceProduct);
    }

    @Override
    public void deleteById(Long id) {
        invoiceProductRepository.deleteById(id);
    }

    @Override
    public BigDecimal getTotalPriceByInvoice(Long invoiceId) {

        return invoiceProductRepository.findTotalPriceByInvoice(invoiceId)
                .orElse(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_EVEN);
    }

    @Override
    public BigDecimal getTotalPriceWithTaxByInvoice(Long invoiceId) {

        return invoiceProductRepository.findTotalPriceWithTaxByInvoiceId(invoiceId)
                .orElse(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_EVEN);
    }

    @Override
    public BigDecimal getTotalCostForLoggedCompany() {

        return invoiceProductRepository.findTotalPriceByInvoice_Company_IdAndInvoice_InvoiceType(getCurrentCompanyId(), InvoiceType.PURCHASE.name())
                .orElse(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_EVEN);
    }

    @Override
    public BigDecimal getTotalSalesForLoggedCompany() {

        return invoiceProductRepository.findTotalPriceByInvoice_Company_IdAndInvoice_InvoiceType(getCurrentCompanyId(), InvoiceType.SALES.name())
                .orElse(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_EVEN);
    }

    @Override
    public BigDecimal getTotalProfitLossForLoggedCompany() {

        return invoiceProductRepository.findProfitLossByInvoice_Company_Id(getCurrentCompanyId())
                .orElse(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_EVEN);
    }

    @Override
    public List<InvoiceProductDTO> getAllByInvoiceStatusApprovedForLoggedCompany() {

        return dtoMapper(invoiceProductRepository.findByInvoice_Company_IdAndInvoice_InvoiceStatusIsApprovedOrderByInvoice_DateDesc(getCurrentCompanyId()));
    }

    @Override
    public List<InvoiceProductDTO> getAllByInvoiceId(Long id) {

        List<InvoiceProductDTO> dtoList = dtoMapper(invoiceProductRepository.findAllByInvoice_Id(id));

        for (InvoiceProductDTO invoiceProductDTO : dtoList) {

            invoiceProductDTO.setTotal(invoiceProductDTO.getPrice()
                    .add(invoiceProductDTO.getPrice()
                            .multiply(BigDecimal.valueOf(invoiceProductDTO.getTax()))
                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_EVEN))
                    .multiply(BigDecimal.valueOf(invoiceProductDTO.getQuantity())));
        }

        return dtoList;
    }

    @Override
    public void setProfitLosses(Long id) {

        for (InvoiceProduct salesInvoiceProduct : invoiceProductRepository.findAllByInvoice_Id(id)) {

            if (salesInvoiceProduct.getProduct().getCalculationType().equals(CalculationType.AVERAGE_RATE)) {

                salesInvoiceProduct.setProfitLoss(salesInvoiceProduct.getPrice()
                        .multiply(BigDecimal.valueOf(salesInvoiceProduct.getQuantity()))
                        .subtract(salesInvoiceProduct.getProduct()
                                .getAverageUnitCost()
                                .multiply(BigDecimal.valueOf(salesInvoiceProduct.getQuantity()))));

            } else {

                List<InvoiceProduct> purchaseInvoiceProducts = invoiceProductRepository.findByRemainingQuantityGreaterThanAndInvoice_InvoiceTypeAndProduct_IdOrderByLastUpdateDateTimeAsc(salesInvoiceProduct.getProduct()
                        .getId());

                var quantity = salesInvoiceProduct.getQuantity();
                var invoiceProductIndex = 0;
                BigDecimal totalCost = BigDecimal.ZERO;

                while (quantity >= 0) {

                    InvoiceProduct currentIndexInvoiceProduct = purchaseInvoiceProducts.get(invoiceProductIndex);

                    if (quantity > currentIndexInvoiceProduct.getRemainingQuantity()) {

                        totalCost = totalCost.add(currentIndexInvoiceProduct.getPrice()
                                .multiply(BigDecimal.valueOf(currentIndexInvoiceProduct.getRemainingQuantity())));
                        invoiceProductIndex++;
                        quantity -= currentIndexInvoiceProduct.getRemainingQuantity();
                        currentIndexInvoiceProduct.setRemainingQuantity(0);

                    } else {

                        totalCost = totalCost.add(currentIndexInvoiceProduct.getPrice()
                                .multiply(BigDecimal.valueOf(quantity)));
                        var quantityBeforeReduction = quantity;
                        quantity -= currentIndexInvoiceProduct.getRemainingQuantity();
                        currentIndexInvoiceProduct.setRemainingQuantity(currentIndexInvoiceProduct.getRemainingQuantity() - quantityBeforeReduction);

                    }

                    invoiceProductRepository.save(currentIndexInvoiceProduct);
                }
                salesInvoiceProduct.setProfitLoss(salesInvoiceProduct.getPrice()
                        .multiply(BigDecimal.valueOf(salesInvoiceProduct.getQuantity()))
                        .subtract(totalCost));
            }

            invoiceProductRepository.save(salesInvoiceProduct);

            productService.decreaseQuantityInStock(salesInvoiceProduct.getProduct()
                    .getId(), salesInvoiceProduct.getQuantity());
        }
    }

    @Override
    public void setAverageUnitPrice(Long id) {

        for (InvoiceProduct purchaseInvoiceProduct : invoiceProductRepository.findAllByInvoice_Id(id)) {

            productService.setProductsAverageUnitCost(purchaseInvoiceProduct.getProduct()
                    .getId(), purchaseInvoiceProduct.getProduct()
                    .getAverageUnitCost()
                    .multiply(BigDecimal.valueOf(purchaseInvoiceProduct.getProduct().getQuantityInStock()))
                    .add(purchaseInvoiceProduct.getPrice()
                            .multiply(BigDecimal.valueOf(purchaseInvoiceProduct.getQuantity())))
                    .divide(BigDecimal.valueOf(purchaseInvoiceProduct.getProduct()
                            .getQuantityInStock() + purchaseInvoiceProduct.getQuantity()), 2, RoundingMode.HALF_EVEN));

            productService.increaseQuantityInStock(purchaseInvoiceProduct.getProduct()
                    .getId(), purchaseInvoiceProduct.getQuantity());
        }
    }

    @Override
    public List<InvoiceProductDTO> getAllApprovedSalesInvoices() {

        return dtoMapper(invoiceProductRepository.findAllInvoicesByInvoice_Company_IdAndInvoice_InvoiceStatusIsApproved(getCurrentCompanyId(), InvoiceType.SALES.name()));
    }

    @Override
    public boolean isStockSufficientForInvoice(List<InvoiceProductDTO> invoiceProductDTOs) {

        return invoiceProductDTOs.stream()
                .collect(Collectors.toMap(InvoiceProductDTO::getProduct, InvoiceProductDTO::getQuantity, Integer::sum))
                .entrySet()
                .stream()
                .noneMatch(entry -> entry.getValue() > entry.getKey().getQuantityInStock());
    }

    private Long getCurrentCompanyId() {
        return securityService.getLoggedInUser().getCompany().getId();
    }

    private List<InvoiceProductDTO> dtoMapper(List<InvoiceProduct> invoiceProductList) {

        return invoiceProductList.stream()
                .map(invoiceProduct -> mapper.convert(invoiceProduct, InvoiceProductDTO.class))
                .collect(Collectors.toList());
    }
}
