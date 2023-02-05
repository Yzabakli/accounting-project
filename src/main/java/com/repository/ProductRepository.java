package com.repository;

import com.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(value = "SELECT (count(*) > 0) FROM products WHERE name ILIKE ?1 AND category_id IN (SELECT id FROM categories WHERE company_id = ?2)", nativeQuery = true)
    boolean existsByNameIgnoreCaseAndCategory_Company_Id(String name, Long companyId);

    @Query(value = "SELECT * FROM products WHERE category_id IN (SELECT id FROM categories WHERE company_id = ?1)", nativeQuery = true)
    List<Product> findByCategory_Company_Id(Long companyId);

    @Query(value = "SELECT (count(*) > 0) FROM products WHERE category_id = ?1", nativeQuery = true)
    boolean existsByCategory_Id(Long categoryId);
}
