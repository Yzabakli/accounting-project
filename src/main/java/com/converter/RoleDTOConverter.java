package com.converter;

import com.dto.RoleDTO;
import com.service.RoleService;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class RoleDTOConverter implements Converter<String, RoleDTO> {

    RoleService roleService;

    public RoleDTOConverter(@Lazy RoleService roleService) {
        this.roleService = roleService;
    }

    @Override
    public RoleDTO convert(String source) {

        if (source.isBlank()) return null;

        return roleService.findById(Long.parseLong(source));
    }
}
