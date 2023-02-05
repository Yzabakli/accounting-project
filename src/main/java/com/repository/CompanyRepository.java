package com.repository;

import com.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    @Query(value = "SELECT * FROM companies WHERE id <> 1 ORDER BY company_status, title", nativeQuery = true)
    List<Company> findAllCustomerCompaniesOrderByCompanyStatusAscTitleAsc();

    @Query(value = "SELECT (count(*) > 0) FROM companies WHERE title ILIKE ?1", nativeQuery = true)
    boolean existsByTitleIgnoreCaseAllIgnoreCase(String title);
}
