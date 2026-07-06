package com.upc.learntrack.iam.mapper;

import com.upc.learntrack.iam.dto.RoleDto;
import com.upc.learntrack.iam.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper {
    RoleDto toDto(Role role);
    Role toEntity(RoleDto roleDto);
}