package com.nicha.identityservice.mapper;

import com.nicha.identityservice.dto.request.UserCreationRequest;
import com.nicha.identityservice.dto.request.UserUpdateRequest;
import com.nicha.identityservice.dto.response.UserResponse;
import com.nicha.identityservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);

    UserResponse toUserResponse(User user);

    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
