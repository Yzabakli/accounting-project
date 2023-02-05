package com.repository;

import com.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT (COUNT(*) > 0) FROM users WHERE username ILIKE ?1", nativeQuery = true)
    boolean existsByUsernameIgnoreCase(String username);

    @Query(value = "SELECT * FROM users WHERE company_id = ?1", nativeQuery = true)
    List<User> findByCompany_Id(Long companyId);

    @Query(value = "SELECT COUNT(*) FROM users WHERE role_id = 2 AND company_id = ?1", nativeQuery = true)
    long countAdminsByCompany(Long companyId);

    @Query(value = "SELECT * FROM users WHERE role_id = 2", nativeQuery = true)
    List<User> findAllAdmins();

    @Query(value = "SELECT * FROM users WHERE username = ?1", nativeQuery = true)
    User findByUsername(String username);

    @Query(value = "SELECT * FROM users WHERE company_id = ?1", nativeQuery = true)
    List<User> findAllByCompany(Long companyId);
}
