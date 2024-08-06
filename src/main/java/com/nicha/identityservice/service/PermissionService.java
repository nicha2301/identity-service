package com.nicha.identityservice.service;

import com.nicha.identityservice.dto.request.PermissionRequest;
import com.nicha.identityservice.dto.response.PermissionResponse;
import com.nicha.identityservice.entity.Permission;
import com.nicha.identityservice.mapper.PermissionMapper;
import com.nicha.identityservice.repository.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    public PermissionResponse create(PermissionRequest request) {
        Permission permission = permissionRepository.save(permissionMapper.toPermission(request));
        return permissionMapper.toPermissionResponse(permission);
    }

    public List<PermissionResponse> getAll() {
        return permissionRepository.findAll()
                .stream()
                .map(permissionMapper::toPermissionResponse)
                .toList();
    }

    public void delete(String request) {
        permissionRepository.deleteById(request);
    }
}
