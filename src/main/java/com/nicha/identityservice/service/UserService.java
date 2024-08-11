package com.nicha.identityservice.service;

import com.nicha.identityservice.dto.request.UserCreationRequest;
import com.nicha.identityservice.dto.request.UserUpdateRequest;
import com.nicha.identityservice.dto.response.UserResponse;
import com.nicha.identityservice.entity.User;
import com.nicha.identityservice.enums.Role;
import com.nicha.identityservice.exception.AppException;
import com.nicha.identityservice.exception.ErrorCode;
import com.nicha.identityservice.mapper.UserMapper;
import com.nicha.identityservice.repository.RoleRepository;
import com.nicha.identityservice.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    public User createUser(UserCreationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        HashSet<String> roles = new HashSet<String>();
        roles.add(Role.USER.name());
//        user.setRoles(roles);
        return userRepository.save(user);
    }

    public UserResponse getInfo() {
        var context = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(context.getName()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toUserResponse(user);
    }

    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        userMapper.updateUser(user, request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        var roles = roleRepository.findAllById(request.getRoles());
        user.setRoles(new HashSet<>(roles));

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }

    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse getUser(String id) {
        return userMapper.toUserResponse(userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found")));
    }
}
