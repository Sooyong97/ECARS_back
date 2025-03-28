package com.sbb.ecars.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequestDto {
    private String id;
    private String password;
}
