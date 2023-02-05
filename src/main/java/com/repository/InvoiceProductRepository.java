package com.repository;

import com.entity.InvoiceProduct;
import com.enums.InvoiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface InvoiceProductRepository extends JpaRepository<InvoiceProduct, Long> {

    @Query(value = "SELECT p.* " +
            "FROM invoice_products p " +
            "LEFT JOIN invoices i ON p.invoice_id = i.id " +
            "WHERE i.invoice_type = 'PURCHASE' " +
            "AND i.invoice_status = 'APPROVED' AND p.remaining_quantity > 0 AND p.product_id = ?1 " +
            "ORDER BY i.last_update_date_time DESC", nativeQuery = true)
    List<InvoiceProduct> findByRemainingQuantityGreaterThanAndInvoice_InvoiceTypeAndProduct_IdOrderByLastUpdateDateTimeAsc(Long productId);

    @Query(value = "SELECT * FROM invoice_products WHERE invoice_id = ?1", nativeQuery = true)
    List<InvoiceProduct> findAllByInvoice_Id(Long invoiceId);

    @Query(value = "SELECT * FROM invoice_products WHERE invoice_id IN (" +
            "SELECT id FROM invoices " +
            "WHERE company_id = ?1 AND invoice_status = 'APPROVED' AND invoice_type = ?2)", nativeQuery = true)
    List<InvoiceProduct> findAllInvoicesByInvoice_Company_IdAndInvoice_InvoiceStatusIsApproved(Long companyId, String invoiceType);

    @Query(value = "SELECT p.* " +
            "FROM invoice_products p " +
            "LEFT JOIN invoices i ON p.invoice_id = i.id " +
            "WHERE i.invoice_status = 'APPROVED' AND i.company_id = :id " +
            "ORDER BY i.insert_date_time DESC", nativeQuery = true)
    List<InvoiceProduct> findByInvoice_Company_IdAndInvoice_InvoiceStatusIsApprovedOrderByInvoice_DateDesc(@Param("id") Long companyId);

    @Query(value = "SELECT SUM(price * quantity) FROM invoice_products WHERE invoice_id = ?1", nativeQuery = true)
    Optional<BigDecimal> findTotalPriceByInvoice(Long invoiceId);

    @Query(value = "SELECT SUM((price + (price * tax / 100)) * quantity) FROM invoice_products WHERE invoice_id = ?1", nativeQuery = true)
    Optional<BigDecimal> findTotalPriceWithTaxByInvoiceId(Long invoiceId);

    @Query(value = "SELECT SUM(price) FROM invoice_products WHERE invoice_id IN (SELECT id FROM invoices WHERE company_id = ?1 AND invoice_status = 'APPROVED' AND invoice_type = ?2)", nativeQuery = true)
    Optional<BigDecimal> findTotalPriceByInvoice_Company_IdAndInvoice_InvoiceType(Long companyId, String invoiceType);

    @Query(value = "SELECT SUM(profit_loss) FROM invoice_products WHERE invoice_id IN (" +
            "SELECT id FROM invoices WHERE company_id = ?1 AND invoice_status = 'APPROVED')", nativeQuery = true)
    Optional<BigDecimal> findProfitLossByInvoice_Company_Id(Long companyId);
}