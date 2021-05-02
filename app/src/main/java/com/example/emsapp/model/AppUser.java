package com.example.emsapp.model;

import com.example.emsapp.constants.UserState;
import com.example.emsapp.constants.UserType;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppUser implements Serializable {
    private String photo;
    private String uId;
    @Builder.Default
    private String userType = UserType.USER.getValue();
    private String displayName;
    private String dob;
    private String gender;
    private String designation;
    private String email;
    private String password;
    private String phone;
    private String address;
    @Builder.Default
    private String status = UserState.ACTIVE.getValue();
}
