package com.resumefit.resumefit_backend.domain.user.dto;

import lombok.Data;

@Data
public class UserInfoDto {

    private String name;
    private String email;
    private String phoneNumber;
    private String photo;
    private String academic;
    private String schoolName;
    private String major;
}
