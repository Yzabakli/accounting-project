package com.service.implementation;

import com.dto.RoleDTO;
import com.mapper.MapperUtil;
import com.repository.RoleRepository;
import com.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final MapperUtil mapper;

    public RoleServiceImpl(RoleRepository roleRepository, MapperUtil mapper) {
        this.roleRepository = roleRepository;
        this.mapper = mapper;
    }

    @Override
    public RoleDTO findById(Long id) {

        return mapper.convert(roleRepository.findById(id).orElseThrow(), RoleDTO.class);
    }

    @Override
    public List<RoleDTO> getAllRoles() {

        return roleRepository.findAllRoles()
                .stream()
                .map(role -> mapper.convert(role, RoleDTO.class))
                .collect(Collectors.toList());
    }
}
