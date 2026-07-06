package com.upc.learntrack.iam.mapper;

import com.upc.learntrack.iam.dto.UserDto;
import com.upc.learntrack.iam.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "role.name", target = "roleName")
    @Mapping(target = "password", ignore = true)   // No enviar password en respuestas
    UserDto toDto(User user);

    @Mapping(source = "roleName", target = "role.name")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "verified", ignore = true)
    User toEntity(UserDto userDto);
}