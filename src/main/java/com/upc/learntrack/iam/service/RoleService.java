package com.upc.learntrack.iam.service;

import com.upc.learntrack.iam.dto.RoleDto;
import java.util.List;

public interface RoleService {
    List<RoleDto> findAll();
    RoleDto findById(Long id);
    RoleDto save(RoleDto roleDto);
}