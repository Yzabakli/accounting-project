package com.service.implementation;

import com.dto.CompanyDTO;
import com.entity.Company;
import com.enums.CompanyStatus;
import com.mapper.MapperUtil;
import com.repository.CompanyRepository;
import com.service.CompanyService;
import com.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final UserService userService;
    private final MapperUtil mapper;

    public CompanyServiceImpl(CompanyRepository companyRepository, MapperUtil mapperUtil, UserService userService) {
        this.companyRepository = companyRepository;
        this.mapper = mapperUtil;
        this.userService = userService;
    }

    @Override
    public CompanyDTO findById(Long id) {

        return mapper.convert(companyRepository.findById(id).orElseThrow(), CompanyDTO.class);
    }

    @Override
    public List<CompanyDTO> listAllCompanies() {

        return companyRepository.findAllCustomerCompaniesOrderByCompanyStatusAscTitleAsc()
                .stream()
                .map(company -> mapper.convert(company, CompanyDTO.class))
                .collect(Collectors.toList());

    }

    @Override
    public void save(CompanyDTO companyDTO) {

        Company company = mapper.convert(companyDTO, Company.class);

        company.setCompanyStatus(CompanyStatus.ACTIVE);
        company.setTitle(company.getTitle().trim());

        companyRepository.save(company);
    }

    @Override
    public void update(CompanyDTO companyDTO) {

        Company dbCompany = companyRepository.findById(companyDTO.getId()).orElseThrow();

        Company convertedCompany = mapper.convert(companyDTO, Company.class);

        convertedCompany.setCompanyStatus(dbCompany.getCompanyStatus());
        convertedCompany.setTitle(convertedCompany.getTitle().trim());

        companyRepository.save(convertedCompany);
    }

    @Override
    public void activateCompanyStatus(Long id) {

        Company company = companyRepository.findById(id).orElseThrow();

        company.setCompanyStatus(CompanyStatus.ACTIVE);

        companyRepository.save(company);
    }

    @Override
    public void deactivateCompanyStatus(Long id) {

        Company company = companyRepository.findById(id).orElseThrow();

        company.setCompanyStatus(CompanyStatus.PASSIVE);

        companyRepository.save(company);
    }

    @Override
    public boolean isTitleAlreadyInUse(String title) {

        return companyRepository.existsByTitleIgnoreCaseAllIgnoreCase(title.trim());
    }

    @Override
    public boolean isTitleNotPrevious(Long id, String title) {

        return !companyRepository.findById(id).orElseThrow().getTitle().equals(title.trim());
    }
}
