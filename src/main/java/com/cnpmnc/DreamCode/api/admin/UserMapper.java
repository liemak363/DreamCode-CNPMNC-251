package com.cnpmnc.DreamCode.api.admin;

import com.cnpmnc.DreamCode.dto.request.UserCreationRequest;
import com.cnpmnc.DreamCode.dto.request.UserUpdateRequest;
import com.cnpmnc.DreamCode.dto.response.UserResponse;
import com.cnpmnc.DreamCode.model.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface UserMapper {

    @Mapping(target = "roles", ignore = true)
    User toUser(UserCreationRequest request);

    @Mapping(source = "id", target = "id")
    UserResponse toUserResponse(User user);

    @Mapping(target = "roles", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
