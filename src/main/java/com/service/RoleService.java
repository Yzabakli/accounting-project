package com.service;

import com.dto.RoleDTO;

import java.util.List;

public interface RoleService {
    RoleDTO findById(Long id);

    List<RoleDTO> getAllRoles();
}
