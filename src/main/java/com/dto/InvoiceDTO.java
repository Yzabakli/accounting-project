package com.dto;

import com.enums.InvoiceStatus;
import com.enums.InvoiceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InvoiceDTO {

    private Long id;
    private String invoiceNo;
    private InvoiceStatus invoiceStatus;
    private InvoiceType invoiceType;

    @DateTimeFormat(pattern = "MM-dd-yyyy")
    private LocalDate date;

    @NotNull(message = "Required field")
    private ClientVendorDTO clientVendor;
    private CompanyDTO company;

    private BigDecimal price;
    private BigDecimal tax;
    private BigDecimal total;
    private boolean isStockSufficient;
    private List<InvoiceProductDTO> invoiceProducts;
}
