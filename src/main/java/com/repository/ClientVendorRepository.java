package com.repository;

import com.entity.ClientVendor;
import com.enums.ClientVendorType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClientVendorRepository extends JpaRepository<ClientVendor,Long> {

    @Query(value = "SELECT * FROM clients_vendors WHERE company_id = ?1 AND client_vendor_type = ?2 ORDER BY client_vendor_name", nativeQuery = true)
    List<ClientVendor> findByCompany_IdAndClientVendorTypeOrderByClientVendorNameAsc(Long companyId, String clientVendorType);

    @Query(value = "SELECT (count(*) > 0) FROM clients_vendors WHERE company_id = ?1 AND client_vendor_name ILIKE ?2", nativeQuery = true)
    boolean existsByCompany_IdAndClientVendorNameIgnoreCase(Long companyId, String clientVendorName);

    @Query(value = "SELECT * FROM clients_vendors WHERE company_id = ?1 ORDER BY client_vendor_type, client_vendor_name", nativeQuery = true)
    List<ClientVendor> findByCompany_IdOrderByClientVendorTypeAsc(Long companyId);
}
