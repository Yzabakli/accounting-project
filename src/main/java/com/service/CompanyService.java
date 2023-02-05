package com.service;

import com.dto.CompanyDTO;

import java.util.List;

public interface CompanyService {

    CompanyDTO findById(Long id);

    List<CompanyDTO> listAllCompanies();

    void save(CompanyDTO dto);

    void update(CompanyDTO dto);

    void activateCompanyStatus(Long id);

    void deactivateCompanyStatus(Long id);

    boolean isTitleAlreadyInUse(String title);

    boolean isTitleNotPrevious(Long id, String title);
}
