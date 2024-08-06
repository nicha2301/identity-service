package com.nicha.identityservice.mapper;


import com.nicha.identityservice.dto.request.RoleRequest;
import com.nicha.identityservice.dto.response.RoleResponse;
import com.nicha.identityservice.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);
    RoleResponse toRoleResponse(Role role);
}
