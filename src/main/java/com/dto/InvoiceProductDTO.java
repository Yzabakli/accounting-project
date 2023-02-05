package com.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InvoiceProductDTO {

    private Long id;

    @Min(value = 1, message = "Should be more than 0")
    private int quantity;

    @NotNull(message = "Required field")
    @Min(value = 1, message = "Should be more than 0")
    private BigDecimal price;

    @Min(value = 1, message = "Should be more than 0")
    private int tax;
    private BigDecimal profitLoss;
    private int remainingQuantity;
    private InvoiceDTO invoice;

    @NotNull
    private ProductDTO product;
    private BigDecimal total;
    private LocalDateTime ApprovedTime;
}
