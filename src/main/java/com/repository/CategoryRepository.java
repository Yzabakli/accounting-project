package com.repository;

import com.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query(value = "SELECT (count(*) > 0) FROM categories WHERE company_id = ?1 AND description ILIKE ?2", nativeQuery = true)
    boolean existsByCompany_IdAndDescriptionIgnoreCase(Long companyId, String description);

    @Query(value = "SELECT * FROM categories WHERE company_id = ?1 ORDER BY description", nativeQuery = true)
    List<Category> findByCompany_IdOrderByDescriptionAsc(Long companyId);
}
