package com.repository;

import com.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Query(value = "SELECT (count(*) > 0) FROM invoices WHERE client_vendor_id = ?1", nativeQuery = true)
    boolean existsByClientVendor_Id(Long clientVendorId);

    @Query(value = "SELECT * FROM invoices WHERE invoice_status = 'APPROVED' AND company_id = ?1 ORDER BY last_update_date_time DESC LIMIT 5", nativeQuery = true)
    List<Invoice> getLast5ApprovedInvoicesByCompanyId(Long companyId);

    @Query(value = "SELECT invoice_no FROM invoices WHERE company_id = ?1 AND invoice_type = ?2 ORDER BY insert_date_time DESC LIMIT 1", nativeQuery = true)
    String getLatestInvoice_InvoiceNo(Long companyId, String invoiceType);

    @Query(value = "SELECT * FROM invoices WHERE company_id = ?1 AND invoice_type LIKE ?2 ORDER BY invoice_no DESC", nativeQuery = true)
    List<Invoice> findByCompany_IdAndInvoiceTypeOrderByInvoiceNoDesc(Long companyId, String invoiceType);
}

