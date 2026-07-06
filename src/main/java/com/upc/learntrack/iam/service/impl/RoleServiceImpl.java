package com.upc.learntrack.iam.service.impl;

import com.upc.learntrack.iam.dto.RoleDto;
import com.upc.learntrack.iam.exception.RoleNotFoundException;
import com.upc.learntrack.iam.mapper.RoleMapper;
import com.upc.learntrack.iam.model.Role;
import com.upc.learntrack.iam.repository.RoleRepository;
import com.upc.learntrack.iam.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    @Transactional(readOnly = true)
    public List<RoleDto> findAll() {
        return roleRepository.findAll().stream()
                .map(roleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RoleDto findById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException("Rol no encontrado con ID: " + id));
        return roleMapper.toDto(role);
    }

    @Override
    @Transactional
    public RoleDto save(RoleDto roleDto) {
        Role role = roleMapper.toEntity(roleDto);
        return roleMapper.toDto(roleRepository.save(role));
    }
}