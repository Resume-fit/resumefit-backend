package com.resumefit.resumefit_backend.domain.user.mapper;

import com.resumefit.resumefit_backend.domain.user.dto.UserInfoDto;
import com.resumefit.resumefit_backend.domain.user.entity.User;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring", // Spring Bean으로 등록
        unmappedTargetPolicy = ReportingPolicy.IGNORE, // DTO에 없는 필드는 무시
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    void updateUserFromDto(UserInfoDto dto, @MappingTarget User entity);

    UserInfoDto toUserInfoDto(User user);
}
