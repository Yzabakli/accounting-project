package com.converter;

import com.dto.CompanyDTO;
import com.service.CompanyService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CompanyDTOConverter implements Converter<String, CompanyDTO> {

    CompanyService companyService;

    public CompanyDTOConverter(CompanyService companyService) {
        this.companyService = companyService;
    }

    @Override
    public CompanyDTO convert(String source) {

        if (source.isBlank()) return null;

        return companyService.findById(Long.parseLong(source));
    }
}
