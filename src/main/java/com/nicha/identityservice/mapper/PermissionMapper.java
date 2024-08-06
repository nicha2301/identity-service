package com.nicha.identityservice.mapper;


import com.nicha.identityservice.dto.request.PermissionRequest;
import com.nicha.identityservice.dto.response.PermissionResponse;
import com.nicha.identityservice.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);
    PermissionResponse toPermissionResponse(Permission permission);
}
