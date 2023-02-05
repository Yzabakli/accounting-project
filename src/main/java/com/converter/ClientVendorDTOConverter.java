package com.converter;

import com.dto.ClientVendorDTO;
import com.service.ClientVendorService;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ClientVendorDTOConverter implements Converter<String, ClientVendorDTO> {

    ClientVendorService clientVendorService;

    public ClientVendorDTOConverter(@Lazy ClientVendorService clientVendorService) {
        this.clientVendorService = clientVendorService;
    }

    @Override
    public ClientVendorDTO convert(String source) {

        if (source.isBlank()) return null;

        return clientVendorService.findById(Long.parseLong(source));
    }
}
